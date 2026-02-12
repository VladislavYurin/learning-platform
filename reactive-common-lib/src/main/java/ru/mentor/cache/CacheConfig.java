package ru.mentor.cache;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.mentor.common.CourseResponse;

/**
 * Конфигурация кэширования для превью курсов.
 * Создаёт и настраивает асинхронный кэш Caffeine и адаптер для работы с ним в реактивном Webflux.
 */
@Configuration
public class CacheConfig {

    /**
     * Создаёт асинхронный кэш для хранения списка превью активных курсов.
     * Время жизни записи - 60 минут
     * Максимальный размер — 100 записей
     * @return настроенный экземпляр {@link AsyncCache}
     */
    @Bean
    public AsyncCache<String, List<CourseResponse>> coursePreviewCache(){
        return Caffeine.newBuilder()
                       .expireAfterWrite(60, TimeUnit.MINUTES)
                       .maximumSize(100)
                       .buildAsync();
    }

    /**
     * Создаёт адаптер для работы с асинхронным кэшем в реактивном стиле
     * {@link CacheAdapter} оборачивает {@link AsyncCache} и предоставляет методы,
     * возвращающие Mono вместо CompletableFuture.
     * @param asyncCache бин асинхронного кэша
     * @return CacheAdapter для кэширования превью курсов
     */
    @Bean
    public CacheAdapter<String, List<CourseResponse>> cacheAdapter
            (AsyncCache<String, List<CourseResponse>> asyncCache){
        return new CacheAdapter<>(asyncCache);
    }
}
