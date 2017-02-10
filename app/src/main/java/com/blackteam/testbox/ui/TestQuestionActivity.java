package com.blackteam.testbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
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
    /** Таймер обратного отсчета времени прохождения теста. */
    private CountDownTimer mTestTimer;
    private boolean mIsTestFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_question);
        ButterKnife.bind(this);

        mExamTest = (ExamTest) getIntent().getExtras().getSerializable("ExamTest");

        // Перемешиваем вопросы и ответы.
        mExamTest.shuffle();
        mExamTest.setTest();

        TestQuestionAdapter testQuestionAdapter = new TestQuestionAdapter(
                getSupportFragmentManager(),
                mExamTest.getQuestions());
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

        mIsTestFinished = false;
        // Запускаем таймер здесь, т.к. нам необходимо дождаться инициализации меню,
        // чтобы отображать оставшиеся время в пункт меню.
        startTimer(mTestTime, sTimeUpdateInterval);

        return menuDisplayed;
    }

    @Override
    public void onBackPressed() {
        mTestTimer.cancel();
        super.onBackPressed();
    }

    /**
     * Завершение тестирования.
     */
    public synchronized void finishTest() {
        // Защита от многократного запуска Activity.
        // Ведь тест может завершиться по двум причинам: пользователем и таймером.

        if (!mIsTestFinished) {
            mIsTestFinished = true;
            // Тест завершился, а значит таймер больше не нужен.
            mTestTimer.cancel();
            Intent trainingResultActivity = new Intent(this, TestResultActivity.class);
            trainingResultActivity.putExtra("ExamTest", mExamTest);
            startActivity(trainingResultActivity);
        }
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
        mTestTimer = new CountDownTimer(testTotalTime * 1000, updateIterval * 1000) {
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
        mTestTimer.start();
    }
}
