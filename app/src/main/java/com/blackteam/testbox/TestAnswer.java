package com.blackteam.testbox;

/**
 * Вариант ответа на вопрос.
 */
public class TestAnswer {
    private String mAnswerText;
    private boolean mIsRight;

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
}
