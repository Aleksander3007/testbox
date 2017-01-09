package com.blackteam.testbox;

/**
 * Данные для экзамеционной темы.
 */
public class ExamThemeData {

    /** Название темы. */
    private String mName;
    /** Идентификатор темы. */
    private String mId;

    public ExamThemeData(String name) {
        this.mName = name;
    }

    public ExamThemeData(String name, String id) {
        this.mName = name;
        this.mId = id;
    }

    public String getName() { return mName; }
    public String getId() { return mId; }

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
