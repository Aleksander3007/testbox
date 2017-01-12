package com.blackteam.testbox.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.ExamThemeData;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.TestQuestion;
import com.blackteam.testbox.utils.UIHelper;
import com.blackteam.testbox.utils.WideTree;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Сделать возможность менять уже сущетсвующие вопросы.

/**
 * Страница с экзамеционными вопросами.
 */
public class ExamTestQuestionActivity extends BaseActivity {

    private LinearLayout mAnswersLinearLayout;
    private EditText mQuestionEditText;
    private FloatingActionButton mCreateAnswerFab;

    private ExamTest examTest;
    private List<String> answers = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_test_question);

        mAnswersLinearLayout = (LinearLayout) findViewById(R.id.ll_answers);
        mQuestionEditText = (EditText) findViewById(R.id.et_question);
        mCreateAnswerFab = (FloatingActionButton) findViewById(R.id.fab_createNewAnswer);

        WideTree.Node<ExamThemeData> examTheme =
                ((TestBoxApp) getApplicationContext()).getExamTree().getCurElem();

        //
        // int testId = mExamTheme.getData().getId();
        // Грузим файл по адресу "et" + String.valueOf(testId) + ".xml";
        // Из него и считываем данные.
        // Перемешиваем ответы.

        answers.addAll(Arrays.asList("Android", "iPhone", "WindowsMobile"));

        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            case USER:
                UIHelper.disableEditText(mQuestionEditText);
                for (String answer : answers) {
                    addAnswerView(answer);
                }
                break;
            case EDITOR:
                UIHelper.enableEditText(mQuestionEditText);
                for (String answer : answers) {
                    addEditableAnswerView(answer);
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        // Сохраняем данные в файл.
        super.onStop();
    }

    @Override
    protected void setModeUser() {
        // TODO: Спрашивать: Завершить редактирование тестов?
        super.setModeUser();
        mCreateAnswerFab.hide();
    }

    @Override
    protected void setModeEditor() {
        // TODO: Спрашивать: Завершить тест и перейти к редактированию?
        super.setModeEditor();
        mCreateAnswerFab.show();
    }

    /**
     * Обработка нажатия на кнопку создать новый ответ.
     * @param view
     */
    public void createNewAnswerOnClick(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        CreatingAnswerDialogFragment creatingAnswerDialogFragment =
                new CreatingAnswerDialogFragment();
        creatingAnswerDialogFragment.show(fragmentManager, "creatingAnswerDialog");
    }

    /**
     * Обработка нажатия на кнопку "завершить".
     * @param view
     */
    public void finishOnClick(View view) {
        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            case USER:
                // TODO: Закончить тестирование и вывести результаты.
                break;
            case EDITOR:
                // TODO: Закончить редактирование и перейти в меню (какое-то).
                break;
        }
    }

    /**
     * Обработка нажатия на кнопку "следующий вопрос".
     * @param view
     */
    public void nextQuestionOnClick(View view) {
        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            case USER:
                // TODO: Перейти на следующий вопрос.
                break;
            case EDITOR:
                // TODO: Перейти к созданию следующего вопроса.
                // 1. Проверяем введен ли вопрос? Pattern RegEx, как выводить собщение над EditText.
                // 2. Если нет, то сообщаем об этом и не переходим.
                // 3. Если да, то

                // Проверяем, что правильно заполнены все необходимые поля.
                if (mQuestionEditText.getText().length() == 0) {
                    Toast.makeText(this, getResources().getText(R.string.question_text_empty),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    addQuestion();
                }

                break;
        }
    }

    /**
     * Добавить вопрос в список.
     */
    private void addQuestion() {
        String questionText = mQuestionEditText.getText().toString();

        Map<String, Boolean> answers = new HashMap<>();
        // Считываем возможные ответы.
        int nAnswerViews = mAnswersLinearLayout.getChildCount();
        for (int iAnswerView = 0; iAnswerView < nAnswerViews; iAnswerView++) {
            final View answerView = mAnswersLinearLayout.getChildAt(iAnswerView);
            TextView answerTextView = (TextView) answerView.findViewById(R.id.et_answerText);
            CheckBox isRightAnswerCheckBox = (CheckBox) answerView.findViewById(R.id.cb_isRightAnswer);
            answers.put(answerTextView.getText().toString(), isRightAnswerCheckBox.isChecked());
        }
        // TODO: Заглушка null вместо ответов.
        examTest.addQuestion(new TestQuestion(questionText, null));
    }

    /**
     * Проверка, что данные для вопроса заполнены правильно.
     * @return true - правильно.
     */
    private boolean isValidNewQuestion() {
        if (mQuestionEditText.getText().length() == 0) return false;

        return true;
    }

    /**
     * Добавить элемент, отображающий редактируемый возможный вариант ответа в Activity.
     * @param answer Текст ответа.
     */
    private void addEditableAnswerView(String answer) {
        final View answerView = getLayoutInflater().inflate(R.layout.listview_elem_edit_answer, null);
        EditText answerEditText = (EditText) answerView.findViewById(R.id.et_answerText);
        answerEditText.setText(answer);
        mAnswersLinearLayout.addView(answerView);
    }

    /**
     * Добавить элемент, отображающий возможный вариант ответа в Activity.
     * @param answer Текст ответа.
     */
    private void addAnswerView(String answer) {
        final View answerView = getLayoutInflater().inflate(R.layout.listview_elem_answer, null);
        TextView answerTextView = (TextView) answerView.findViewById(R.id.tv_answerText);
        answerTextView.setText(answer);
        mAnswersLinearLayout.addView(answerView);
    }

    public void addNewAnswer(String answer) {
        try {
            addEditableAnswerView(answer);
        }
        catch (Exception ex) {
            Log.d("ExamTestQuestionActiv", ex.getMessage());
        }
    }
}
