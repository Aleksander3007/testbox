package com.blackteam.testbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestQuestion;
import com.blackteam.testbox.TestQuestionAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Страница с экзамеционными вопросами в режиме "Пользователь" (тестируемый).
 */
public class TestQuestionActivity extends FragmentActivity {

    @BindView(R.id.vp_questions) ViewPager mQuestionsViewPager;

    private ExamTest mExamTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_question);
        ButterKnife.bind(this);

        mExamTest = (ExamTest) getIntent().getExtras().getSerializable("ExamTest");

        TestQuestionAdapter testQuestionAdapter = new TestQuestionAdapter(
                        getSupportFragmentManager(),
                        (ArrayList<TestQuestion>) mExamTest.getQuestions());
        mQuestionsViewPager.setAdapter(testQuestionAdapter);
    }

    /**
     * Завершение тестирования.
     */
    public void finishTest() {
        Intent trainingResultActivity = new Intent(this, TestResultActivity.class);
        trainingResultActivity.putExtra("ExamTest", mExamTest);
        startActivity(trainingResultActivity);
    }
}
