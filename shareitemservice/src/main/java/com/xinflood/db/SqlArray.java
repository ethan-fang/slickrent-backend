package com.xinflood.db;

import com.google.common.collect.Iterables;

/**
 */
public class SqlArray<T>
{
    private final Iterable<T> elements;
    private final Class<T> type;

    public SqlArray(Class<T> type, Iterable<T> elements) {
        this.elements = elements;
        this.type = type;
    }

//    public static <T> SqlArray<T> arrayOf(Class<T> type, T... elements) {
//        return new SqlArray<T>(type, asList(elements));
//    }

    public static <T> SqlArray<T> arrayOf(Class<T> type, Iterable<T> elements) {
        return new SqlArray<T>(type, elements);
    }

    public Object[] getElements()
    {
        return Iterables.toArray(elements, Object.class);
    }

    public Class<T> getType()
    {
        return type;
    }
}

