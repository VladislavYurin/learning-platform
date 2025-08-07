package ru.mentor.annotaion;

import com.google.protobuf.GeneratedMessageV3;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, используемая над методами, обслуживающими механизм GRpc
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GrpcMethod {

    /**
     * Тип сущности запроса по протоколу GRpc
     */
    Class<? extends GeneratedMessageV3> requestType();

    /**
     * Тип механизма GRpc (класс)
     */
    Class<?> grpcInstanceType();

    /**
     * Свойство механизма GRpc (SERVER, CLIENT) {@link GrpcMethodType}
     */
    GrpcMethodType grpcMethodType();

    /**
     * Список геттеров для получения необходимых данных от сущности запроса
     */
    String[] getters() default {};

}
