package com.kzaemrio.ithome;

import com.kzaemrio.ithome.function.Function;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class ListHelper {
    @Inject
    public ListHelper() {
    }

    public <T, V extends Comparable<? super V>> int binarySearch(List<T> list, V key, Function<T, V> mapper) {
        return Collections.binarySearch(mapList(list, mapper), key, (o1, o2) -> o2.compareTo(o1));
    }

    public <T, V> List<V> mapList(List<T> list, Function<T, V> mapper) {
        return new AbstractList<V>() {
            @Override
            public V get(int index) {
                return mapper.apply(list.get(index));
            }

            @Override
            public int size() {
                return list.size();
            }
        };
    }
}
