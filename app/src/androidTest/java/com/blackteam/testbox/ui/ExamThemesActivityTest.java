package com.blackteam.testbox.ui;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;

import com.blackteam.testbox.R;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

/*

https://google.github.io/android-testing-support-library/docs/espresso/setup/

To avoid flakiness, we highly recommend that you turn off system animations on the virtual or physical device(s) used for testing.

On your device, under Settings->Developer options disable the following 3 settings:
    Window animation scale
    Transition animation scale
    Animator duration scale
 */

/**
 * Тестирование класса {@link ExamThemesActivity}
 */
@RunWith(JUnit4.class)
@LargeTest
public class ExamThemesActivityTest {

    private static final String sTestNewTheme = "TestNewTheme";

    @Rule
    public ActivityTestRule<ExamThemesActivity> mActivityRule =
            new ActivityTestRule<>(ExamThemesActivity.class);

    /**
     * Проверка создания новой темы.
     */
    @Test
    public void createNewExamTheme_Ok() {
        createExamTheme(sTestNewTheme);
        // Проверям что добавленная тема появилась.
        onView(withText(sTestNewTheme))
                .check(matches(
                        isDisplayed()));
    }

    /**
     * Проверка удаления темы.
     */
    @Test
    public void deleteExamTheme_Ok() {
        // Создаем тему для дальнейшего её удаления.
        createExamTheme(sTestNewTheme);
        // Долгое нажатие на объект-тема.
        onView(withText(sTestNewTheme))
                .perform(
                        longClick());
        // В появившемся диалоговом окне нажимаем кнопку "Удалить".
        onView(withId(R.id.btn_delete))
                .perform(
                        click());
        // В окне удаления подтверждаем удаление.
        onView(withText(R.string.btn_ok))
                .perform(
                        click());
        // Проверяем что тема удалена.
        onView(withText(sTestNewTheme))
                .check(doesNotExist());
    }

    /**
     * Создать тему с указанным именем.
     * @param examThemeName имя темы.
     */
    private void createExamTheme(String examThemeName) {
        try {
            // Переходим в режим редактирования.
            onView(withText(R.string.user_type_user))
                    .perform(
                            click());
        }
        catch (NoMatchingViewException ex) {
            // Если мы попали сюда, значит мы уже в режиме редактирования,
            // а он нам и нужен.
        }
        // Открываем окно создания экзамена.
        onView(withId(R.id.fab_createNewItem))
                .perform(
                        click());
        // Вводим название экзамена.
        onView(withId(R.id.et_themeName))
                .perform(
                        typeText(examThemeName))
                .perform(
                        closeSoftKeyboard());
        // Нажимаем кнопку ОК.
        onView(withId(R.id.btn_ok))
                .perform(
                        click());
    }
}