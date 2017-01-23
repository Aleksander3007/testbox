package com.blackteam.testbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Экзамеционный вопрос из теста с вариантами ответа.
 */
public class TestQuestion {
    private String mQuestion;
    private List<TestAnswer> mAnswers = new ArrayList<>();

    public TestQuestion(String question) {
        this.mQuestion = question;
    }

    public TestQuestion(String question, List<TestAnswer> answers) {
        this.mQuestion = question;
        this.mAnswers = answers;
    }

    public String getText() { return mQuestion; }

    public List<TestAnswer> getAnswers() {return mAnswers; }

    public void addAnswers(List<TestAnswer> answers) { mAnswers.addAll(answers); }
}