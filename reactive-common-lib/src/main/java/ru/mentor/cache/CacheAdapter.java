package ru.mentor.cache;

import com.github.benmanes.caffeine.cache.AsyncCache;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Класс-обертка для удобной работы с Caffeine Cache в Webflux.
 * Преобразует асинхронный CompletableFuture-based Caffeine API в реактивный Mono/Flux API.
 * @param <K>
 * @param <V>
 */
@Slf4j
@RequiredArgsConstructor
public class CacheAdapter <K, V> {

    private final AsyncCache<K, V> asyncCache;
    private final Scheduler scheduler = Schedulers.boundedElastic();

    /**
     * Получить значение из кэша по ключу.
     * Если нет в кэше - загружает через loader и после завершения CompletableFuture сохраняет результат в кэш.
     * @param key - ключ
     * @param loader - функция загрузки (Mono)
     * @return Mono с результатом
     */
    public Mono<V> get(K key, Function<K, Mono<V>> loader) {
        return Mono.defer(()-> {
            CompletableFuture<V> future = asyncCache.getIfPresent(key);
            if (future != null) {
                log.info("Cache HIT for key: {}", key);
                return Mono.fromCompletionStage(future);
            }
            log.info("Cache MISS for key: {}", key);
            return Mono.fromCompletionStage(
                    asyncCache.get(key, (k, executor) -> loader.apply(k).toFuture())
            );
        });

    }

    /**
     * Удалить запись из кэша по ключу.
     * Выполняется асинхронно в отдельном потоке.
     * @param key - ключ
     * @return Mono<Void>
     */
    public Mono<Void> invalidate(K key){
        return Mono.fromRunnable(()-> {
            log.info("Invalidating cache key: {}", key);
            asyncCache.synchronous().invalidate(key);
        }).subscribeOn(scheduler).then();
    }

}
