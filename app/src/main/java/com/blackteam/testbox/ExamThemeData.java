package com.blackteam.testbox;

/**
 * Данные для экзамеционной темы.
 */
public class ExamThemeData {

    /** Тема содержит в себе другие темы (т.е. данная тема не является конечной). */
    public static final String COMPOSITE_THEME = null;

    /** Название темы. */
    private String mName;
    /** Имя файла, где хранятся данные по экзамеционной теме. */
    private String mSource = COMPOSITE_THEME;

    public ExamThemeData(String name) {
        this.mName = name;
    }

    public ExamThemeData(String name, String source) {
        this.mName = name;
        this.mSource = source;
    }

    public String getName() { return mName; }
    public String getSource() { return mSource; }

    public void setSource(String src) { mSource = src; }

    @Override
    public String toString() {
        return mName;
    }

    @Override
    public boolean equals(Object obj) {
        return mName.equals(((ExamThemeData)obj).getName());
    }
}
