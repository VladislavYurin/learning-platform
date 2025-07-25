package ru.mentor.util;

import java.util.UUID;

public class RqGenerator {

    public static String generateRqId() {
        return UUID.randomUUID().toString();
    }

}
