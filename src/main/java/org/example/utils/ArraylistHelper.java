package org.example.utils;

import java.util.ArrayList;
import java.util.Random;

public class ArraylistHelper {
    public static <T> int getRandomElement(ArrayList<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List is null or empty");
        }
        Random random = new Random();
        return random.nextInt(list.size());
    }

    public static <T> ArrayList<T> castArrayList(ArrayList<?> list, Class<T> elementType) {
        ArrayList<T> result = new ArrayList<>();
        for (Object obj : list) {
            if (elementType.isInstance(obj)) {
                result.add(elementType.cast(obj));
            }
        }
        return result;
    }
}
