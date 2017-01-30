package com.blackteam.testbox.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestQuestion;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Страница с результами прохожения теста/тренировки.
 */
public class TestResultActivity extends Activity {

    @BindView(R.id.tv_num_correct_answers) TextView mCorrectAnswersTextView;
    @BindView(R.id.tv_percent_correct_answers) TextView mPercentCorrectAnswersTextView;

    private ExamTest mExamTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        ButterKnife.bind(this);

        mExamTest = (ExamTest) getIntent().getExtras().getSerializable("ExamTest");
        showResult();
    }

    /**
     * Отобразить результаты.
     */
    private void showResult() {
        int numCorrectAnswers = geNumCorrectAnswers();
        mCorrectAnswersTextView.setText(String.format("%d/%d", numCorrectAnswers, getNumQuestions()));
        mPercentCorrectAnswersTextView.setText(
                String.format(
                        "%.2f",
                        getPercentCorrectAnswers(numCorrectAnswers, getNumQuestions())
                )
        );
    }

    private int getNumQuestions() {
        return mExamTest.getQuestions().size();
    }

    private int geNumCorrectAnswers() {
        int numCorrectAnswers = 0;
        for (TestQuestion question : mExamTest.getQuestions()) {
            if (question.rightAnswer()) numCorrectAnswers++;
        }
        return numCorrectAnswers;
    }

    private float getPercentCorrectAnswers(int numCorrectAnswers, int totalAnswers) {
        return ((float)numCorrectAnswers / totalAnswers) * 100f;
    }
}
