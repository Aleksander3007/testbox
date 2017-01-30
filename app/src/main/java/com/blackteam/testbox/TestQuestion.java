package com.blackteam.testbox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Экзамеционный вопрос из теста с вариантами ответа.
 */
public class TestQuestion implements Serializable {
    private String mQuestion;
    private List<TestAnswer> mAnswers = new ArrayList<>();
    private String mExplanation;

    public TestQuestion(String question) {
        this.mQuestion = question;
    }

    public TestQuestion(String question, List<TestAnswer> answers, String explanation) {
        this.mQuestion = question;
        this.mAnswers = answers;
        this.mExplanation = explanation;
    }

    public String getText() { return mQuestion; }

    public String getExplanation() { return mExplanation; }
    public void setExplanation(String explanation) { mExplanation = explanation; }

    public List<TestAnswer> getAnswers() {return mAnswers; }

    public void addAnswers(List<TestAnswer> answers) { mAnswers.addAll(answers); }

    /**
     * Правильно ли был дан ответ на вопрос.
     * @return true - если правильно.
     */
    public boolean verify() {
        for (TestAnswer answer : mAnswers) {
            if (!answer.verify())
                return false;
        }
        return true;
    }
}
