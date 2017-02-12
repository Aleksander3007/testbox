package com.blackteam.testbox.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestAnswer;
import com.blackteam.testbox.TestQuestion;
import com.blackteam.testbox.utils.ListCursor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Страница с вопросами, которые разрешено редактировать.
 */
public class EditQuestionActivity extends BaseActivity {

    @BindView(R.id.ll_answers) LinearLayout mAnswersLinearLayout;
    @BindView(R.id.et_question) EditText mQuestionEditText;
    @BindView(R.id.et_explanation) EditText mExplanationEditText;
    @BindView(R.id.fab_createNewItem) FloatingActionButton mCreateAnswerFab;
    @BindView(R.id.bottom_editing_bar) LinearLayout mBottomEditingBar;
    @BindView(R.id.btn_prevPage) ImageButton mPreviousQuestionBtn;

    private ExamTest mExamTest;
    private ListCursor<TestQuestion> mQuestionCursor;
    /** Отображаемый в текущий момент вопрос новый, т.е. еще не был добавлен в экзамен. тест. */
    private boolean mIsNewQuestion;

    /** Редактируемый элемент из списка вопросов. */
    private View mEditingAnswerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
        ButterKnife.bind(this);

        mExamTest = (ExamTest) getIntent().getExtras().getSerializable("ExamTest");

        mQuestionCursor = new ListCursor<>(mExamTest.getAllQuestions());

        if (!mQuestionCursor.isEmpty()) displayQuestion(mQuestionCursor.getCurrent());
        if (!mQuestionCursor.hasPrevious()) mPreviousQuestionBtn.setVisibility(View.INVISIBLE);
        /** Если в тесте нет ни одного вопроса, то первый отображаемый вопрос новый. */
        mIsNewQuestion = mQuestionCursor.isEmpty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // INFO: onCreateOptionsMenu() вызывается после Activity.onResume().

        boolean menuDisplayed = super.onCreateOptionsMenu(menu);
        if (menuDisplayed) {
            // Скрываем кнопку перехода между режимами.
            mUserTypeMenuItem.setVisible(false);
        }

        return menuDisplayed;
    }

    @Override
    protected void onStop() {
        // Сохраняем состояние потом востанавливаем.
        super.onStop();
    }

    /**
     * Обработка нажатия на кнопку создать новый ответ.
     * @param view нажатый элемент.
     */
    @OnClick(R.id.fab_createNewItem)
    public void createNewAnswerOnClick(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        EditAnswerDialogFragment creatingAnswerDialog = EditAnswerDialogFragment.newInstance();
        creatingAnswerDialog.show(fragmentManager, "creatingAnswerDialog");
    }

    /**
     * Нажатие на кнопку "Сохранить".
     * @param view нажатый элемент.
     */
    @OnClick(R.id.btn_save)
    public void saveOnClick(View view) {
        boolean success = makeExamTestChanges();
        if (success) saveAllQuestions();
    }

    /**
     * Нажатие на кнопку "Завершить".
     * @param view нажатый элемент.
     */
    @OnClick(R.id.btn_finish)
    public void finishOnClick(View view) {
        // TODO: Здесь необходимо спрашивать, сохранить ли изменения, если они были.
    }

    /**
     * Нажатие на кнопку "Предыдущий вопрос".
     * @param view нажатый элемент.
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
     * @param view нажатый элемент.
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
            if (success) {
                // Переходим к только что добавленному (он уже отображен на экране).
                mQuestionCursor.next();
            }
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
        mExplanationEditText.setText(question.getExplanation());
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
        for (TestAnswer answer : answers) addEditableAnswerView(answer);
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

        String explanationText = mExplanationEditText.getText().toString();

        List<TestAnswer> answers = new ArrayList<>();
        int nAnswerViews = mAnswersLinearLayout.getChildCount();
        if (nAnswerViews > 0) {
            // Считываем возможные ответы.
            for (int iAnswerView = 0; iAnswerView < nAnswerViews; iAnswerView++) {
                final View answerView = mAnswersLinearLayout.getChildAt(iAnswerView);
                TextView answerTextView = (TextView) answerView.findViewById(R.id.tv_answerText);
                CheckBox isRightAnswerCheckBox = (CheckBox) answerView.findViewById(R.id.cb_isRightAnswer);
                answers.add(new TestAnswer(answerTextView.getText().toString(),
                        isRightAnswerCheckBox.isChecked()));
            }
            return new TestQuestion(questionText, answers, explanationText);
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
        return mQuestionEditText.getText().length() != 0;
    }

    private boolean isAnswerValid(TextView answerTextView) {
        return answerTextView.getText().length() != 0;
    }
    /**
     * Добавить элемент, отображающий редактируемый возможный вариант ответа в Activity.
     * @param answer Текст ответа.
     */
    private void addEditableAnswerView(TestAnswer answer) {
        final View answerView = getLayoutInflater().inflate(R.layout.listview_elem_edit_answer, null);
        final CheckBox answerCheckBox = (CheckBox) answerView.findViewById(R.id.cb_isRightAnswer);
        final TextView answerTextView = (TextView) answerView.findViewById(R.id.tv_answerText);
        answerCheckBox.setChecked(answer.isRight());
        answerTextView.setText(answer.getText());

        final int answerIndex = mAnswersLinearLayout.getChildCount();

        /**Обработка нажатия на вариант вопроса (Открывается меню редактирования). */
        answerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mEditingAnswerView = answerView;
                view.setSelected(true);
                EditAnswerDialogFragment editAnswerDialog = EditAnswerDialogFragment.newInstance(
                        answerTextView.getText().toString(),
                        answerCheckBox.isChecked(),
                        false);
                editAnswerDialog.show(getFragmentManager(), "editAnswerDialogFragment");
                return true;
            }
        });

        mAnswersLinearLayout.addView(answerView);
    }

    /**
     * Добавить новый ответ в список.
     * @param answer текст ответа.
     */
    public void addNewAnswer(String answer, boolean isRightAnswer) {
        try {
            addEditableAnswerView(new TestAnswer(answer, isRightAnswer));
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

    /**
     * Изменить выбранный вариант ответа.
     * @param answerNewText новый текст вариант ответа.
     * @param isRight правильный ли это вариант?
     */
    public void editAnswer(String answerNewText, boolean isRight) {
        CheckBox answerCheckBox = (CheckBox) mEditingAnswerView.findViewById(R.id.cb_isRightAnswer);
        TextView answerTextView = (TextView) mEditingAnswerView.findViewById(R.id.tv_answerText);
        answerTextView.setText(answerNewText);
        answerCheckBox.setChecked(isRight);
    }

    /**
     * Удалить выбранный вариант ответа.
     */
    public void deleteAnswer() {
        mAnswersLinearLayout.removeView(mEditingAnswerView);
    }
}
