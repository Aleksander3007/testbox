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
import com.blackteam.testbox.TestAnswer;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// TODO: Сделать возможность менять уже сущетсвующие вопросы.

/**
 * Страница с экзамеционными вопросами.
 */
public class ExamTestQuestionActivity extends BaseActivity {

    @BindView(R.id.ll_answers) LinearLayout mAnswersLinearLayout;
    @BindView(R.id.et_question) EditText mQuestionEditText;
    @BindView(R.id.fab_createNewAnswer) FloatingActionButton mCreateAnswerFab;

    private ExamTest examTest;
    private Iterator<TestQuestion> currentQuestion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_test_question);
        ButterKnife.bind(this);

        examTest = (ExamTest) getIntent().getExtras().getSerializable("ExamTest");

        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            case USER:
                UIHelper.disableEditText(mQuestionEditText);
                currentQuestion = examTest.getQuestions().listIterator();
                for (TestAnswer answer : currentQuestion.next().getAnswers()) {
                    addAnswerView(answer);
                }
                break;
            case EDITOR:
                UIHelper.enableEditText(mQuestionEditText);
                break;
        }
    }

    @Override
    protected void onStop() {
        // Сохраняем состояние потом востанавливаем.
        super.onStop();
    }

    @Override
    protected void setModeUser() {
        super.setModeUser();
        mCreateAnswerFab.hide();
    }

    @Override
    protected void setModeEditor() {
        super.setModeEditor();
        mCreateAnswerFab.show();
    }

    /**
     * Обработка нажатия на кнопку создать новый ответ.
     * @param view
     */
    @OnClick(R.id.fab_createNewAnswer)
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
    @OnClick(R.id.btn_finish)
    public void finishOnClick(View view) {
        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            case USER:
                // TODO: Закончить тестирование и вывести результаты.
                break;
            case EDITOR:
                // Добавляем последний созданный вопрос и всё сохраняем.
                if (addQuestion()) saveAllQuestions();
        }
    }

    /**
     * Обработка нажатия на кнопку "следующий вопрос".
     * @param view
     */
    @OnClick(R.id.btn_nextQuestion)
    public void nextQuestionOnClick(View view) {
        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            case USER:
                // TODO: Перейти на следующий вопрос.
                break;
            case EDITOR:
                boolean hasQuestionBeenAdded = addQuestion();
                if (hasQuestionBeenAdded) {
                    mQuestionEditText.setText("");
                    mAnswersLinearLayout.removeAllViews();
                }
                break;
        }
    }

    /**
     * Добавить вопрос в список.
     */
    private boolean addQuestion() {
        String questionText = mQuestionEditText.getText().toString();
        if (!isQuestionTextValid()) {
            mQuestionEditText.setError(getResources().getText(R.string.question_text_empty));
            return false;
        }

        List<TestAnswer> answers = new ArrayList<>();

        int nAnswerViews = mAnswersLinearLayout.getChildCount();
        if (nAnswerViews > 0) {
            // Считываем возможные ответы.
            for (int iAnswerView = 0; iAnswerView < nAnswerViews; iAnswerView++) {
                final View answerView = mAnswersLinearLayout.getChildAt(iAnswerView);
                TextView answerTextView = (TextView) answerView.findViewById(R.id.et_answerText);
                CheckBox isRightAnswerCheckBox = (CheckBox) answerView.findViewById(R.id.cb_isRightAnswer);
                answers.add(new TestAnswer(answerTextView.getText().toString(),
                        isRightAnswerCheckBox.isChecked()));
            }
            examTest.addQuestion(new TestQuestion(questionText, answers));
            return true;
        }
        else {
            Toast.makeText(this, R.string.msg_zero_answers, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Проверка, что данные для вопроса заполнены правильно.
     * @return true - правильно.
     */
    private boolean isQuestionTextValid() {
        if (mQuestionEditText.getText().length() == 0) return false;
        return true;
    }

    private boolean isAnswerValid(TextView answerTextView) {
        if (answerTextView.getText().length() == 0) return false;
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
    private void addAnswerView(TestAnswer answer) {
        final View answerView = getLayoutInflater().inflate(R.layout.listview_elem_answer, null);
        TextView answerTextView = (TextView) answerView.findViewById(R.id.tv_answerText);
        CheckBox answerCheckBox = (CheckBox) answerView.findViewById(R.id.cb_isRightAnswer);
        answerTextView.setText(answer.getText());
        answerCheckBox.setChecked(answer.isRight());
        mAnswersLinearLayout.addView(answerView);
    }

    /**
     * Добавить новый ответ в список.
     * @param answer текст ответа.
     */
    public void addNewAnswer(String answer) {
        try {
            addEditableAnswerView(answer);
        }
        catch (Exception ex) {
            Log.d("ExamTestQuestionActiv", ex.getMessage());
        }
    }

    /**
     * Сохранить все вопросы для теста.
     */
    public void saveAllQuestions() {
        try {
            examTest.save(getApplicationContext());
            Toast.makeText(this, R.string.msg_successful_saving, Toast.LENGTH_SHORT).show();
        } catch (IOException ioex) {
            Log.e("ExamTestQuestionA", ioex.getMessage());
            ioex.printStackTrace();
            Toast.makeText(this, R.string.msg_error_saving, Toast.LENGTH_SHORT).show();
        }
    }
}
