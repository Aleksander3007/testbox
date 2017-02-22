package com.blackteam.testbox.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestAnswer;
import com.blackteam.testbox.TestQuestion;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;

/**
 * Тестирование класса {@link EditQuestionActivity}
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditQuestionActivityTest {

    private static final String mStubExamTest = "StubTest1";
    private static final String sStubNewAnswer = "StubNewAnswer";
    private static final String mStubNewQuestionText = "StubNewQuestionText";
    private static final String sStubQuestionText = "StubQuestionText";
    private static final String sStubQuestionExplanation = "StubQuestionExplanation";
    private static final String sStubStartAnswerText = "StubStartAnswer";
    private static final boolean sStubStartAnswerState = false;
    private static final String sStubEditedAnswerAnswerText = "StubEditedAnswer";
    private ExamTest mFakeExamTest;

    @Rule
    public ActivityTestRule<EditQuestionActivity> mActivityRule =
            new ActivityTestRule<>(EditQuestionActivity.class, true, false);

    @Before
    public void initExamTest() {

        mFakeExamTest = new ExamTest(mStubExamTest);
        TestAnswer testAnswer = new TestAnswer(sStubStartAnswerText, sStubStartAnswerState);
        List<TestAnswer> answers = new ArrayList<>();
        answers.add(testAnswer);
        TestQuestion mStubTestQuestion =
                new TestQuestion(sStubQuestionText, answers, sStubQuestionExplanation);
        mFakeExamTest.getAllQuestions().add(mStubTestQuestion);

        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, MainActivity.class);
        intent.putExtra(EditQuestionActivity.EXTRA_EXAM_TEST, mFakeExamTest);
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void createNewAnswerOnClick_Ok() {
        createAnswer(sStubNewAnswer);
        // Проверяем, что добавленный вопрос отображается.
        onView(allOf(withId(R.id.tv_answerText), withText(sStubNewAnswer)))
                .check(matches(
                        isDisplayed()));
    }

    @Test
    public void createNewAnswerOnClick_Cancel() {
        // Открываем окно создания вопроса.
        onView(withId(R.id.fab_createNewItem))
                .perform(
                        click());
        // Создаем вопрос.
        onView(withId(R.id.et_answerText))
                .perform(
                        typeText(sStubNewAnswer));
        // Закрываем окно создания вопроса.
        onView(withId(R.id.btn_cancel))
                .perform(
                        click());
        // Проверяем, что добавленный вопрос НЕ отображается.
        onView(allOf(withId(R.id.tv_answerText), withText(sStubNewAnswer)))
                .check(
                        doesNotExist());
    }

    public void finishOnClick_RollbackChanges() {
        createAnswer(sStubNewAnswer);
        // Создаем название вопроса.
        onView(withId(R.id.et_question))
                .perform(
                        replaceText(mStubNewQuestionText));
        // Завершаем редактирование.
        onView(withId(R.id.btn_finish))
                .perform(
                        click());
        // Открывается окно завершения редактирования, выбираем "не сохранять".
        onView(withText(R.string.btn_rollback))
                .perform(
                        click());
        // Проверяем, что окно закрылось.
        onView(withId(R.id.et_question))
                .check(
                        doesNotExist());
    }

    @Test
    public void orientationChange_EditAnswer() {

        // Открываем окно редактирования вопроса.
        onView(allOf(withId(R.id.tv_answerText), withText(sStubStartAnswerText)))
                .perform(
                        longClick());
        // Изменяем текст вопроса.
        onView(withId(R.id.et_answerText))
                .perform(
                        replaceText(sStubEditedAnswerAnswerText));
        // Меняем ориентацию экрана.
        changeOrientation();
        // Закрываем клавиатуру.
        Espresso.closeSoftKeyboard();
        // Закрываем окно редактирования вопроса.
        onView(withId(R.id.btn_ok))
                .perform(
                        click());
        // Проверям, что текст вопроса изменился.
        onView(allOf(withId(R.id.tv_answerText), withText(sStubEditedAnswerAnswerText)))
                .check(matches(
                        isDisplayed()));
    }

    public void changeOrientation() {
        if (mActivityRule.getActivity().getRequestedOrientation()
                != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mActivityRule.getActivity()
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            mActivityRule.getActivity()
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void createAnswer(String answerName) {
        // Открываем окно создания вопроса.
        onView(withId(R.id.fab_createNewItem))
                .perform(
                        click());
        // Создаем вопрос.
        onView(withId(R.id.et_answerText))
                .perform(
                        typeText(answerName));
        // Закрываем окно создания вопроса.
        onView(withId(R.id.btn_ok))
                .perform(
                        click());
    }

}