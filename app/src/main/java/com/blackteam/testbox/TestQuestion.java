package com.blackteam.testbox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Экзамеционный вопрос из теста с вариантами ответа.
 */
public class TestQuestion implements Serializable {
    private final String mQuestion;
    private List<TestAnswer> mAnswers = new ArrayList<>();
    private String mExplanation;

    /** На данный вопрос был данный правильный ответ? */
    private boolean mRightAnswer;

    public TestQuestion(String question) {
        this.mQuestion = question;
    }

    public TestQuestion(String question, List<TestAnswer> answers, String explanation) {
        this.mQuestion = question;
        this.mAnswers = answers;
        this.mExplanation = explanation;
        mRightAnswer = false;
    }

    public String getText() { return mQuestion; }

    public String getExplanation() { return mExplanation; }
    public void setExplanation(String explanation) { mExplanation = explanation; }

    public List<TestAnswer> getAnswers() {return mAnswers; }

    public void addAnswers(List<TestAnswer> answers) { mAnswers.addAll(answers); }

    public boolean rightAnswer() { return mRightAnswer; }

    /**
     * Правильно ли был дан ответ на вопрос.
     * @return true - если правильно.
     */
    public boolean verify() {
        mRightAnswer = true;
        for (TestAnswer answer : mAnswers) {
            if (!answer.verify()) {
                mRightAnswer = false;
                break;
            }
        }
        return mRightAnswer;
    }

    /**
     * Перемешать ответы.
     */
    public void shuffleAnswers() {
        Collections.shuffle(mAnswers);
    }
}
