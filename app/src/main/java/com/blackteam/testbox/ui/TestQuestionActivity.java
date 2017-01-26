package com.blackteam.testbox.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestAnswer;
import com.blackteam.testbox.TestQuestion;
import com.blackteam.testbox.TestQuestionAdapter;

import java.util.ArrayList;
import java.util.List;

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
}
