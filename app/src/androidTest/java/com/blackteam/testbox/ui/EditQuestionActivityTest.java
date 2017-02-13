package com.blackteam.testbox.ui;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.R;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.Espresso;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anyOf;
import static org.junit.Assert.*;

/**
 * Тестирование класса {@link EditQuestionActivity}
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditQuestionActivityTest {

    private static final String mStubExamTest = "StubTest1";
    private static final String mStubAnswer = "StubAnswer";
    private static final String mStubQuestion = "StubQuestion";
    private ExamTest mFakeExamTest;

    @Rule
    public ActivityTestRule<EditQuestionActivity> mActivityRule =
            new ActivityTestRule<>(EditQuestionActivity.class, true, false);

    @Before
    public void initExamTest() {
        mFakeExamTest = new ExamTest(mStubExamTest);
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, MainActivity.class);
        intent.putExtra(EditQuestionActivity.ARG_EXAM_TEST, mFakeExamTest);
        mActivityRule.launchActivity(intent);
    }

    @Test
    public void createNewAnswerOnClick_Ok() {
        createAnswer(mStubAnswer);
        // Проверяем, что добавленный вопрос отображается.
        onView(anyOf(withId(R.id.tv_answerText), withText(mStubAnswer)))
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
                        typeText(mStubAnswer));
        // Закрываем окно создания вопроса.
        onView(withId(R.id.btn_cancel))
                .perform(
                        click());
        // Проверяем, что добавленный вопрос отображается.
        onView(anyOf(withId(R.id.tv_answerText), withText(mStubAnswer)))
                .check(
                        doesNotExist());
    }

    public void finishOnClick_RollbackChanges() {
        createAnswer(mStubAnswer);
        // Создаем название вопроса.
        onView(withId(R.id.et_question))
                .perform(
                        typeText(mStubQuestion));
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