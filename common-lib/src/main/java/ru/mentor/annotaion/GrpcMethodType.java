package ru.mentor.annotaion;

/**
 * Перечисление типов gRPC методов.
 * Определяет два типа взаимодействия в gRPC:
 * <ul>
 *   <li>{@link #SERVER} - серверный потоковый метод</li>
 *   <li>{@link #CLIENT} - клиентский потоковый метод</li>
 * </ul>
 */

public enum GrpcMethodType {
    SERVER,
    CLIENT
}
