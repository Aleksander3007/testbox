package com.blackteam.testbox.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestAnswer;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.TestQuestion;
import com.blackteam.testbox.utils.ListCursor;
import com.blackteam.testbox.utils.UIHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.fab_createNewItem) FloatingActionButton mCreateAnswerFab;
    @BindView(R.id.bottom_editing_bar) LinearLayout mBottomEditingBar;
    @BindView(R.id.btn_prevPage) Button mPreviousQuestionBtn;

    private ExamTest mExamTest;
    private ListCursor<TestQuestion> mQuestionCursor;
    /** Отображаемый в текущий момент вопрос новый, т.е. еще не был добавлен в экзамен. тест. */
    private boolean mIsNewQuestion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_test_question);
        ButterKnife.bind(this);

        mExamTest = (ExamTest) getIntent().getExtras().getSerializable("ExamTest");

        mQuestionCursor = new ListCursor<>(mExamTest.getQuestions());

        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            case USER:
                UIHelper.disableEditText(mQuestionEditText);
                break;
            case EDITOR:
                UIHelper.enableEditText(mQuestionEditText);
                if (!mQuestionCursor.isEmpty()) displayQuestion(mQuestionCursor.getCurrent());
                if (!mQuestionCursor.hasPrevious()) mPreviousQuestionBtn.setVisibility(View.INVISIBLE);
                mIsNewQuestion = mQuestionCursor.isEmpty();
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
        mBottomEditingBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setModeEditor() {
        super.setModeEditor();
        mBottomEditingBar.setVisibility(View.VISIBLE);
    }

    /**
     * Обработка нажатия на кнопку создать новый ответ.
     * @param view
     */
    @OnClick(R.id.fab_createNewItem)
    public void createNewAnswerOnClick(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        CreatingAnswerDialogFragment creatingAnswerDialogFragment =
                new CreatingAnswerDialogFragment();
        creatingAnswerDialogFragment.show(fragmentManager, "creatingAnswerDialog");
    }

    /**
     * Нажатие на кнопку "Сохранить".
     * @param view
     */
    @OnClick(R.id.btn_save)
    public void saveOnClick(View view) {
        boolean success = makeExamTestChanges();
        if (success) saveAllQuestions();
    }

    /**
     * Нажатие на кнопку "Завершить".
     * @param view
     */
    @OnClick(R.id.btn_finish)
    public void finishOnClick(View view) {
        // TODO: Здесь необходимо спрашивать, сохранить ли изменения, если они были.
    }

    /**
     * Нажатие на кнопку "Предыдущий вопрос".
     * @param view
     */
    @OnClick(R.id.btn_prevPage)
    public void prevQuestionOnClick(View view) {
        // Если вызвана данная функция, значит кнопка перехода на предыдущий доступна.

        boolean success = makeExamTestChanges();
        // Если предыдущий вопрос существует, то отображаем его.
        if (mQuestionCursor.hasPrevious()) {
            if (success) displayQuestion(mQuestionCursor.previous());
        }
    }

    /**
     * Нажатие на кнопку "Следующий вопрос".
     * @param view
     */
    @OnClick(R.id.btn_nextPage)
    public void nextQuestionOnClick(View view) {
        boolean success = makeExamTestChanges();
        if (!success) return;

        // Если этот вопрос не последний, то ...
        if (mQuestionCursor.hasNext()) {
            // ... то переходим к следующему.
            displayQuestion(mQuestionCursor.next());
            mPreviousQuestionBtn.setVisibility(View.VISIBLE);
        }
        else {
            clearDisplay();
            mPreviousQuestionBtn.setVisibility(View.VISIBLE);
            mIsNewQuestion = true;
        }
    }

    /**
     * Очищаем все редактируемые поля.
     */
    private void clearDisplay() {
        mQuestionEditText.setText("");
        mAnswersLinearLayout.removeAllViews();
    }

    /**
     * Внести изменения в экзамеционный тест.
     * @return true- если измения успешно внесены.
     */
    private boolean makeExamTestChanges() {
        boolean success;
        if (mIsNewQuestion) {
            success = addQuestion();
            // Переходим к только что добавленному (он уже отображен на экране).
            mQuestionCursor.next();
        }
        else {
            success = editCurrentQuestion();
        }

        mIsNewQuestion = false;

        return success;
    }

    /**
     * Отобразить указанный экзамеционный вопрос.
     * @param question экзамеционный вопрос, который необходимо отобразить.
     */
    private void displayQuestion(TestQuestion question) {
        mQuestionEditText.setText(question.getText());
        displayAnswer(question.getAnswers());
        // не отображать кнопка "предыдущий вопрос", если его не существует.
        if (!mQuestionCursor.hasPrevious())
            mPreviousQuestionBtn.setVisibility(View.INVISIBLE);
    }

    /**
     * Отобразить указанные ответы.
     * @param answers ответы, которые необходимо отобразить.
     */
    private void displayAnswer(List<TestAnswer> answers) {
        mAnswersLinearLayout.removeAllViews();
        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            case USER:
                for (TestAnswer answer : answers) addAnswerView(answer);
                break;
            case EDITOR:
                for (TestAnswer answer : answers) addEditableAnswerView(answer);
                break;
        }

    }

    /**
     * Внести изменения в текущей вопрос.
     * @return true - если успешно отредактирован.
     */
    private boolean editCurrentQuestion() {
        TestQuestion question = packQuestionData();
        if (question != null)
            mQuestionCursor.set(question);
        return (question != null);
    }

    /**
     * Добавить вопрос в список.
     * @return true - если успешно добавлен.
     */
    private boolean addQuestion() {
        TestQuestion question = packQuestionData();
        if (question != null)
            mQuestionCursor.add(question);
        return (question != null);
    }

    /**
     * Собрать введенные данные для экзамеционного вопроса.
     * @return экзамеционный вопрос, null - если были введены неккоректные данные.
     */
    private TestQuestion packQuestionData() {
        String questionText = mQuestionEditText.getText().toString();
        if (!isQuestionTextValid()) {
            mQuestionEditText.setError(getResources().getText(R.string.question_text_empty));
            return null;
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
            return new TestQuestion(questionText, answers);
        }
        else {
            Toast.makeText(this, R.string.msg_zero_answers, Toast.LENGTH_SHORT).show();
            return null;
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
    private void addEditableAnswerView(TestAnswer answer) {
        final View answerView = getLayoutInflater().inflate(R.layout.listview_elem_edit_answer, null);
        EditText answerEditText = (EditText) answerView.findViewById(R.id.et_answerText);
        CheckBox answerCheckBox = (CheckBox) answerView.findViewById(R.id.cb_isRightAnswer);
        answerEditText.setText(answer.getText());
        answerCheckBox.setChecked(answer.isRight());
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
            addEditableAnswerView(new TestAnswer(answer, false));
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
            mExamTest.save(getApplicationContext());
            Toast.makeText(this, R.string.msg_successful_saving, Toast.LENGTH_SHORT).show();
        } catch (IOException ioex) {
            Log.e("ExamTestQuestionA", ioex.getMessage());
            ioex.printStackTrace();
            Toast.makeText(this, R.string.msg_error_saving, Toast.LENGTH_SHORT).show();
        }
    }
}
