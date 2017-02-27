package com.blackteam.testbox.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.Toast;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestQuestionAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Страница с экзамеционными вопросами в режиме "Пользователь" (тестируемый).
 */
public class TestQuestionActivity extends BaseActivity {

    public static final String EXTRA_EXAM_TEST = "com.testbox.extras.EXTRA_EXAM_TEST";

    @BindView(R.id.vp_questions) ViewPager mQuestionsViewPager;

    @icepick.State ExamTest mExamTest;
    /** Время обновления отображения таймера, c. */
    private static final int sTimeUpdateInterval = 1;
    /** Время на прохождение теста, с. */
    @icepick.State int mTestTime;
    /** Таймер обратного отсчета времени прохождения теста. */
    private CountDownTimer mTestTimer;
    @icepick.State boolean mIsTestFinished;
    /** Видима ли Activity. */
    private boolean mIsVisible;
    /** Был ли открыто окно результата тестирования. */
    @icepick.State boolean mIsResultActivityOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_question);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            mExamTest = (ExamTest) getIntent().getExtras().getSerializable(EXTRA_EXAM_TEST);

            // Перемешиваем вопросы и ответы.
            mExamTest.shuffle();
            mExamTest.setTest();

            mTestTime = mExamTest.getTimeLimit();
        }

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
    protected void onStart() {
        super.onStart();
        mIsVisible = true;
        if (mIsTestFinished) startTestResultActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsVisible = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTestTimer != null) mTestTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder confirmExitTest = new AlertDialog.Builder(this);
        confirmExitTest.setTitle(R.string.title_finish_testing)
                .setMessage(R.string.msg_do_you_want_to_get_out_of_testing)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishTest();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
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
            // Отображаем окно результатов только в том случае, если мы в foreground-е.
            if (this.mIsVisible) startTestResultActivity();
        }
    }

    /**
     * Открыть окно результатов тестирования.
     */
    public synchronized void startTestResultActivity() {
        if (!mIsResultActivityOpened) {
            Intent trainingResultActivity = new Intent(this, TestResultActivity.class);
            trainingResultActivity.putExtra(TestResultActivity.EXTRA_EXAM_TEST, mExamTest);
            startActivity(trainingResultActivity);
            mIsResultActivityOpened = true;
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
                mTestTime = (int) (millisUntilFinished / 1000);
                mTimerMenuItem.setTitle(formatTime(mTestTime));
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
