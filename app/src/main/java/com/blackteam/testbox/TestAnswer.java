package com.blackteam.testbox;

import java.io.Serializable;

/**
 * Вариант ответа на вопрос.
 */
public class TestAnswer implements Serializable {
    private String mAnswerText;
    /** Данный ответ является верным. */
    private boolean mIsRight;
    /** Данный ответ был помечен (например, пользователем) как правильный. */
    private boolean mMarked;

    /**
     * Конструктор.
     * @param answerText Текст ответа.
     * @param isRight Правильный или не правильный ответ.
     */
    public TestAnswer(String answerText, boolean isRight) {
        this.mAnswerText = answerText;
        this.mIsRight = isRight;
    }

    public String getText() { return mAnswerText; }
    public boolean isRight() { return mIsRight; }

    public void mark() { mMarked = true; }
    public void removeMark() { mMarked = false; }
    public boolean isMarked() { return mMarked; }
}
