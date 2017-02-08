package com.blackteam.testbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

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
public class TestQuestionActivity extends BaseActivity {

    @BindView(R.id.vp_questions) ViewPager mQuestionsViewPager;

    private ExamTest mExamTest;
    /** Время обновления отображения таймера, c. */
    private static final int sTimeUpdateInterval = 1;
    /** Время на прохождение теста, с. */
    private int mTestTime = 12; // TODO: Заглушка по времени.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_question);
        ButterKnife.bind(this);

        mExamTest = (ExamTest) getIntent().getExtras().getSerializable("ExamTest");

        mExamTest.shuffle(); // Перемешиваем вопросы и ответы.

        TestQuestionAdapter testQuestionAdapter = new TestQuestionAdapter(
                        getSupportFragmentManager(),
                        (ArrayList<TestQuestion>) mExamTest.getQuestions());
        mQuestionsViewPager.setAdapter(testQuestionAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // INFO: onCreateOptionsMenu() вызывается после Activity.onResume().

        // Проверяем есть ли меню.
        boolean menuDisplayed = super.onCreateOptionsMenu(menu);
        if (menuDisplayed) {
            // Скрываем кнопку перехода между режимами.
            mUserTypeMenuItem.setVisible(false);
            // Отображаем таймер.
            mTimerMenuItem.setTitle(formatTime(mTestTime));
            mTimerMenuItem.setVisible(true);
        }

        // Запускаем таймер здесь, т.к. нам необходимо дождаться инициализации меню,
        // чтобы отображать оставшиеся время в пункт меню.
        startTimer(mTestTime, sTimeUpdateInterval);

        return menuDisplayed;
    }

    /**
     * Завершение тестирования.
     */
    public void finishTest() {
        Intent trainingResultActivity = new Intent(this, TestResultActivity.class);
        trainingResultActivity.putExtra("ExamTest", mExamTest);
        startActivity(trainingResultActivity);
    }

    /**
     * Представление времени в виде строки вида mm::ss.
     * @param seconds время в секундах.
     * @return время в виде строки вида mm::ss.
     */
    private String formatTime(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    /**
     * Запустить таймер обратного отсчета.
     * @param testTotalTime время на прохождение теста, с.
     * @param updateIterval время обновления отображения таймера, с.
     */
    private void startTimer(int testTotalTime, int updateIterval) {
        CountDownTimer testTimer = new CountDownTimer(testTotalTime * 1000, updateIterval * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimerMenuItem.setTitle(formatTime((int) (millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                mTimerMenuItem.setTitle(formatTime(0));
                Toast.makeText(getBaseContext(), R.string.msg_test_time_ended, Toast.LENGTH_SHORT).show();
                finishTest();
            }
        };
        testTimer.start();
    }
}
