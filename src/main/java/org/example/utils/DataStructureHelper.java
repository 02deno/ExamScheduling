package org.example.utils;

import java.util.*;

public class DataStructureHelper {
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

    public static <K, V extends Comparable<? super V>> HashMap<K, V> sortByValueDescending(HashMap<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());

        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        HashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static <K, V extends Comparable<? super V>> HashMap<K, V> sortByValueAscending(HashMap<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());

        list.sort(Map.Entry.comparingByValue());

        HashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
