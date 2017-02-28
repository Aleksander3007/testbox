package com.blackteam.testbox;

import java.io.Serializable;

/**
 * Данные для экзамеционной темы.
 */
public class ExamThemeData implements Serializable {

    /** Название темы. */
    private final String mName;
    /** Идентификатор темы. */
    private String mId;
    /** Содержит ли тема тест. */
    private boolean mContainsTest;

    public ExamThemeData(String name) {
        this.mName = name;
    }

    public ExamThemeData(String name, String id, boolean containsTest) {
        this.mName = name;
        this.mId = id;
        this.mContainsTest = containsTest;
    }

    public String getName() { return mName; }
    public String getId() { return mId; }
    public boolean containsTest() { return mContainsTest; }

    public void setId(String id) { mId = id; }

    @Override
    public String toString() {
        return mName;
    }

    @Override
    public boolean equals(Object obj) {
        return mName.equals(((ExamThemeData)obj).getName());
    }
}
