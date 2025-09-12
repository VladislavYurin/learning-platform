package ru.mentor.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.entity.NotificationTemplateEntity;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.repository.NotificationTemplateRepository;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Кэш шаблонов уведомлений по {@link NotificationTypeEnum}.
 * <p>
 * Загружается из БД через {@link NotificationTemplateRepository}, обновляется автоматически и доступен
 * через {@link #getTemplateCache(NotificationTypeEnum)}. Потокобезопасен ({@link ConcurrentHashMap}).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationCacheProcessor {

    private final NotificationTemplateRepository notificationTemplateRepository;
    private final ConcurrentMap<NotificationTypeEnum, String> templateCache = new ConcurrentHashMap<>();

    /**
     * Возвращает шаблон уведомления для указанного типа.
     * <p>
     * Если шаблона нет в кэше, он загружается из БД и добавляется в кэш.
     * </p>
     *
     * @param type тип уведомления ({@link NotificationTypeEnum})
     * @return {@link Optional}, содержащий шаблон, либо пустой {@link Optional}, если шаблон не найден
     */
    public String getTemplateCache(NotificationTypeEnum type) {
        return templateCache.computeIfAbsent(type, t -> {
            String template = loadTemplateFromDB(t);
            if (template == null) {
                log.error("Template not found with type {}", t);
                throw new EntityNotFoundException("Template with type " + t + " not found");
            }
            return template;
        });
    }

    /**
     * Инициализирует кэш шаблонов при старте приложения.
     * <p>
     * Вызывается автоматически благодаря аннотации {@link PostConstruct}.
     * </p>
     */
    @PostConstruct
    public void initTemplateCache() {
        updateCache();
    }

    /**
     * Явная инвалидизация кэша.
     * <p>
     * Вызов метода приводит к обновлению кэша из базы данных.
     * </p>
     */
    public void invalidateTemplateCache() {
        updateCache();
    }

     /**
      * Периодически обновляет кэш шаблонов уведомлений из базы данных.
      * <p>
      * Интервал обновления задаётся в настройках:
      * <pre>
      *     cache.notification.update.interval.min = N
      * </pre>
      * где N – задержка в минутах между обновлениями.
      * </p>
      */
    @Scheduled(fixedDelayString = "${cache.notification.template.update.interval.min}", timeUnit = TimeUnit.MINUTES)
    public void updateCache(){
        try {
            log.debug("Начинается обновление кеша шаблонов уведомлений...");
            notificationTemplateRepository.findAll().forEach(notificationTemplate -> {
                NotificationTypeEnum notificationTypeEnum = notificationTemplate.getTemplateType();
                if (notificationTypeEnum != null) {
                    templateCache.put(notificationTypeEnum, notificationTemplate.getTemplateText());
                }
            });
            log.info("Кеш заголовков уведомлений успешно обновлён - {}", templateCache.size());
        } catch (Exception e) {
            log.error("Ошибка при обновлении кеша шаблонов ", e);
        }
    }

    /**
     * Загружает шаблон уведомления из базы данных для указанного типа.
     * @param notificationTypeEnum тип уведомления ({@link NotificationTypeEnum})
     * @return текст шаблона или {@code null}, если шаблон не найден
     */
    private String loadTemplateFromDB(NotificationTypeEnum notificationTypeEnum) {
        try {
            return notificationTemplateRepository.findByTemplateType(notificationTypeEnum)
                    .map(NotificationTemplateEntity::getTemplateText)
                    .orElse(null);
        } catch (Exception e) {
            log.error("Ошибка при загрузке шаблона для типа: {}", notificationTypeEnum, e);
            return null;
        }
    }
}