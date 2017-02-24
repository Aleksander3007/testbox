package com.blackteam.testbox.ui;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
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
import com.blackteam.testbox.utils.XmlLoaderInternal;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Страница с вопросами, которые разрешено редактировать.
 */
public class EditQuestionActivity extends BaseActivity
        implements EditAnswerDialogFragment.NoticeDialogListener {

    public static final String TAG = EditQuestionActivity.class.getSimpleName();

    public static final String EXTRA_EXAM_TEST = "com.testbox.extras.EXTRA_EXAM_TEST";

    @BindView(R.id.ll_answers) LinearLayout mAnswersLinearLayout;
    @BindView(R.id.et_question) EditText mQuestionEditText;
    @BindView(R.id.et_explanation) EditText mExplanationEditText;
    @BindView(R.id.fab_createNewItem) FloatingActionButton mCreateAnswerFab;
    @BindView(R.id.bottom_editing_bar) LinearLayout mBottomEditingBar;
    @BindView(R.id.btn_prevPage) ImageButton mPreviousQuestionBtn;

    @icepick.State ExamTest mExamTest;
    @icepick.State ListCursor<TestQuestion> mQuestionCursor;
    /** Отображаемый в текущий момент вопрос новый, т.е. еще не был добавлен в экзамен. тест. */
    @icepick.State boolean mIsNewQuestion;
    /** Редактируемый элемент из списка вопросов, его порядковый номер в списке. */
    @icepick.State int mEditingAnswerViewIndex;
    /** Редактируемый в текущий момент вопрос. */
    @icepick.State TestQuestion mEdtitingTestQuestion;

    /**
     * Событие, которые послужило завершению редактирования.
     */
    private enum EventEndEdit {
        ON_FINISH,
        ON_BACK
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);
        ButterKnife.bind(this);

        if (savedInstanceState == null)
            mExamTest = (ExamTest) getIntent().getExtras().getSerializable(EXTRA_EXAM_TEST);

        init(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Функция отображения сохраненного состояния View здесь, т.к. если переместить эту функции
        // в onCreate(), то получится:
        // в onCreate() мы заполняем всё View, в том числе и состояния CheckBox-ов;
        // в родном адроидовском onRestoreInstanceState() идет восстановление вида,
        // и почему то все CheckBox выставляются по последнему.
        if (mEdtitingTestQuestion != null) displayQuestion(mEdtitingTestQuestion);
    }

    @Override
    protected void onPause() {
        // Сохраняем редактируемый в данный момент вопрос.
        mEdtitingTestQuestion = packQuestionData();
        super.onPause();
    }

    @Override
    public void onBackPressed()
    {
        finishEditing(EventEndEdit.ON_BACK);
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
     * Нажатие на кнопку удаления вопроса.
     */
    @OnClick(R.id.btn_delete)
    public void onDeleteClick() {
        mQuestionCursor.removeCurrent();
        if (mQuestionCursor.hasPrevious())
            displayQuestion(mQuestionCursor.previous());
        // Если есть следующий.
        else if (!mQuestionCursor.isEmpty())
            // После удаления бывший предыдущий стал текущим следующим.
            displayQuestion(mQuestionCursor.getCurrent());
        else {
            mQuestionCursor.reset();
            displayEmptyTest();
        }
    }

    /**
     * Нажатие на кнопку "Завершить".
     * @param view нажатый элемент.
     */
    @OnClick(R.id.btn_finish)
    public void onFinishClick(View view) {
        finishEditing(EventEndEdit.ON_FINISH);
    }

    /**
     * Нажатие на кнопку "Предыдущий вопрос".
     * @param view нажатый элемент.
     */
    @OnClick(R.id.btn_prevPage)
    public void prevQuestionOnClick(View view) {
        // Если вызвана данная функция, значит кнопка перехода на предыдущий доступна.

        if (!mIsNewQuestion || !isDataEmpty()) {
            boolean success = makeExamTestChanges();
            if (success) {
                // Если предыдущий вопрос существует, то отображаем его.
                if (mQuestionCursor.hasPrevious()) displayQuestion(mQuestionCursor.previous());
            }

        }
        // Если новый вопрос и данные не были заполнены, то просто отображаем предыдущий.
        else {
            // ... который в списке текущий.
            displayQuestion(mQuestionCursor.getCurrent());
            mIsNewQuestion = false;
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
     * Добавить новый ответ в список.
     * @param answer текст ответа.
     */
    @Override
    public void addNewAnswer(String answer, boolean isRightAnswer) {
        try {
            addEditableAnswerView(new TestAnswer(answer, isRightAnswer));
        }
        catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

    /**
     * Изменить выбранный вариант ответа.
     * @param answerNewText новый текст вариант ответа.
     * @param isRight правильный ли это вариант?
     */
    @Override
    public void editAnswer(String answerNewText, boolean isRight) {
        View editingAnswerView = mAnswersLinearLayout.getChildAt(mEditingAnswerViewIndex);
        CheckBox answerCheckBox = (CheckBox) editingAnswerView.findViewById(R.id.cb_isRightAnswer);
        TextView answerTextView = (TextView) editingAnswerView.findViewById(R.id.tv_answerText);
        answerTextView.setText(answerNewText);
        answerCheckBox.setChecked(isRight);
    }

    /**
     * Удалить выбранный вариант ответа.
     */
    @Override
    public void deleteAnswer() {
        mAnswersLinearLayout.removeViewAt(mEditingAnswerViewIndex);
    }

    /**
     * Инициализация.
     */
    private void init() {
        init(null);
    }

    /**
     * Инициализация.
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState. <b>Note: Otherwise it is null.<b/>
     */
    private void init(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null)
            mQuestionCursor = new ListCursor<>(mExamTest.getAllQuestions());

        // Если в тесте еще нет вопросов.
        if (mQuestionCursor.isEmpty()) {
            displayEmptyTest();
        }
        else {
            mIsNewQuestion = false;

            if (savedInstanceState == null) {
                // Отображаем последний вопрос.
                while (mQuestionCursor.hasNext()) mQuestionCursor.next();
            }

            if (!mQuestionCursor.hasPrevious()) mPreviousQuestionBtn.setVisibility(View.INVISIBLE);
            if (savedInstanceState == null) displayQuestion(mQuestionCursor.getCurrent());
        }
    }

    /**
     * Устанавливаем результат для принимающей Activity.
     */
    private void setResult() {
        Intent intent = new Intent();
        // Необходимо передать созданные вопросы в предыдущую Activity,
        // т.к. там хранятся теперь старое состояние вопросов.
        intent.putExtra(EXTRA_EXAM_TEST, mExamTest);
        setResult(RESULT_OK, intent);
    }

    /**
     * Отображаем пустой тест (тест без вопросов).
     */
    private void displayEmptyTest() {
        mIsNewQuestion = true;
        clearDisplay();
        mPreviousQuestionBtn.setVisibility(View.INVISIBLE);
    }

    /**
     * Очищаем все редактируемые поля.
     */
    private void clearDisplay() {
        mQuestionEditText.setText("");
        mAnswersLinearLayout.removeAllViews();
        mQuestionEditText.setError(null);
    }

    /**
     * Внести изменения в экзамеционный тест.
     * @return true- если измения успешно внесены.
     */
    private boolean makeExamTestChanges() {

        TestQuestion question = packQuestionData();

        boolean success = validateTestQuestion(question);
        if (!success) return false;

        if (mIsNewQuestion) {
            mQuestionCursor.add(question);
            // Переходим к только что добавленному (он уже отображен на экране).
            mQuestionCursor.next();
            mIsNewQuestion = false;
        }
        else {
            mQuestionCursor.set(question);
        }

        return success;
    }

    /**
     * Отобразить указанный экзамеционный вопрос.
     * @param question экзамеционный вопрос, который необходимо отобразить.
     */
    private void displayQuestion(TestQuestion question) {
        clearDisplay();
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
     * Собрать введенные данные для экзамеционного вопроса.
     * @return экзамеционный вопрос, null - если были введены неккоректные данные.
     */
    private TestQuestion packQuestionData() {
        String questionText = mQuestionEditText.getText().toString();
        String explanationText = mExplanationEditText.getText().toString();

        List<TestAnswer> answers = new ArrayList<>();
        int nAnswerViews = mAnswersLinearLayout.getChildCount();
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

    /**
     * Проверить, что данные для вопроса заполнены правильно.
     * @return true - правильно.
     */
    private boolean validateTestQuestion(TestQuestion question) {
        if (question.getText().length() == 0) {
            mQuestionEditText.setError(getResources().getText(R.string.question_text_empty));
            return false;
        }
        if (question.getAnswers().size() == 0) {
            Toast.makeText(this, R.string.msg_zero_answers, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Пользователь не ввел никаких данных вполя.
     * @return true - если все поля пустые.
     */
    private boolean isDataEmpty() {
        return (mQuestionEditText.getText().length() == 0) &&
                (mExplanationEditText.getText().toString().length() == 0) &&
                (mAnswersLinearLayout.getChildCount() == 0);
    }

    /**
     * Добавить элемент, отображающий редактируемый возможный вариант ответа в Activity.
     * @param answer Текст ответа.
     */
    private void addEditableAnswerView(TestAnswer answer) {
        final ViewGroup answerView = (ViewGroup) getLayoutInflater().inflate(R.layout.listview_elem_edit_answer, null);
        final CheckBox answerCheckBox = (CheckBox) answerView.findViewById(R.id.cb_isRightAnswer);
        final TextView answerTextView = (TextView) answerView.findViewById(R.id.tv_answerText);
        answerCheckBox.setChecked(answer.isRight());
        answerTextView.setText(answer.getText());

        final int answerIndex = mAnswersLinearLayout.getChildCount();

        /**Обработка нажатия на вариант вопроса (Открывается меню редактирования). */
        answerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mEditingAnswerViewIndex = answerIndex;
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
     * Сохранить все вопросы для теста.
     */
    public void saveAllQuestions() {
        try {
            new XmlLoaderInternal().save(this, mExamTest.getFileName(), mExamTest);
            Toast.makeText(this, R.string.msg_success_saving, Toast.LENGTH_SHORT).show();
        } catch (IOException ioex) {
            Log.e(TAG, ioex.getMessage());
            ioex.printStackTrace();
            Toast.makeText(this, R.string.msg_fail_saving, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Откатить изменения до последнего.
     */
    private boolean rollbackChanges() {
        try {
            new XmlLoaderInternal().load(this, mExamTest.getFileName(), mExamTest);
            // Т.к. только загрузили, необходимо всё инициализовать по новой.
            init();
            return true;
        }
        catch (FileNotFoundException fnfex) {
            // Если файл не найден, значит тест новый, поэтому просто удаляем все только что добавленные вопросы.
            mExamTest.getAllQuestions().clear();
            init();
            return true;
        } catch (IOException | XmlPullParserException ex) {
            Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.msg_fail_loading_test, Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
    }

    /**
     * Завершить редактирование.
     * @param eventEndEdit событие которое послужило причиной завершения редактирования.
     */
    private void finishEditing(final EventEndEdit eventEndEdit) {
        boolean success = makeExamTestChanges();
        if (success) {
            AlertDialog.Builder confirmChangesDialog = new AlertDialog.Builder(this);
            confirmChangesDialog.setTitle(R.string.title_finish_editing)
                    .setMessage(R.string.msg_do_editing_save)
                    // Если сохранить изменения.
                    .setPositiveButton(R.string.btn_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveAllQuestions();
                            dialog.dismiss();
                            finishEditingCallback(eventEndEdit);
                        }
                    })
                    // В противном случае откат.
                    .setNegativeButton(R.string.btn_rollback, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (rollbackChanges()) finishEditingCallback(eventEndEdit);
                        }
                    }).show();
        }
    }

    /**
     * Callback после звершение редактирования.
     * @param eventEndEdit событие которое послужило причиной завершения редактирования.
     */
    private void finishEditingCallback(EventEndEdit eventEndEdit) {
        setResult();
        switch (eventEndEdit) {
            case ON_FINISH:
                setModeUser();
                finish();
                break;
            case ON_BACK:
                super.onBackPressed();
                break;
        }
    }
}
