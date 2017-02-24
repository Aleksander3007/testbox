package com.blackteam.testbox.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.ExamThemeData;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.utils.NavigationTree;
import com.blackteam.testbox.utils.UIHelper;
import com.blackteam.testbox.utils.XmlLoaderInternal;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Стартовая страница для экзамеционного текста.
 */
public class ExamTestStartActivity extends BaseActivity {

    private static final String TAG = ExamTestStartActivity.class.getSimpleName();

    public static final int REQUEST_CODE_EXAM_TEST = 1;

    @BindView(R.id.tv_testName) TextView mTestNameTextView;
    @BindView(R.id.et_testDescription) EditText mTestDescriptionEditText;
    @BindView(R.id.tv_testDescription) TextView mTestDescriptionTextView;
    @BindView(R.id.ll_exam_test_start_user) LinearLayout mUserViewLinearLayout;
    @BindView(R.id.ll_exam_test_start_editor) LinearLayout mEditorViewLinearLayout;
    @BindView(R.id.esb_num_questions) EditableSeekBar mNumQuestionsSeekBar;
    @BindView(R.id.rb_testing) AppCompatRadioButton mTestingRadioButton;
    @BindView(R.id.rb_training) AppCompatRadioButton mTrainingRadioButton;
    @BindView(R.id.ll_training_settings) View mTrainingSettingsView;
    @BindView(R.id.esb_num_training_questions) EditableSeekBar mNumTrainingQuestionsSeekBar;
    @BindView(R.id.ll_test_time) View mTestTimeView;
    @BindView(R.id.et_test_time_hours) EditText mTestTimeHoursEditText;
    @BindView(R.id.et_test_time_minutes) EditText mTestTimeMinutesEditText;
    @BindView(R.id.et_test_time_seconds) EditText mTestTimeSecondsEditText;

    private NavigationTree.Node<ExamThemeData> mExamTheme;
    private ExamTest examTest;

    /** Существует ли тест вообще (Был ли он создан ранее). */
    private boolean mIsExistedTest = false;

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
        setContentView(R.layout.activity_exam_test_start);
        ButterKnife.bind(this);

        mExamTheme = ((TestBoxApp)getApplicationContext()).getExamTree().getCurElem();

        mTestNameTextView.setText(mExamTheme.getData().getName());

        // Загружаем данные о тесте.
        examTest = new ExamTest(mExamTheme.getData().getId());
        try {
            new XmlLoaderInternal().load(this, examTest.getFileName(), examTest);
            displayDescription(examTest.getDescription());
            mIsExistedTest = true;

        } catch (FileNotFoundException fnfex) {
            // Если файл не найден значит он еще не был создан.
            mIsExistedTest = false;
        } catch (IOException ioex) {
            Log.e(TAG, ioex.getMessage());
            ioex.printStackTrace();
        } catch (XmlPullParserException xppex) {
            Log.e(TAG, xppex.getMessage());
            xppex.printStackTrace();
        }

        displayNumTestQuestions();
        displayNumTrainingQuestionsView();
        displayTestTime();
    }

    @Override
    public void onBackPressed() {
        finishEditing(EventEndEdit.ON_BACK);
    }

    @Override
    protected void onModeEditorClick() {
        // Проверям были сделаны изменения, нужно ли сохрать и т.п.
        finishEditing(EventEndEdit.ON_FINISH);
    }

    @Override
    protected void setModeEditor() {
        super.setModeEditor();
        // При переходе в режим "Редактор", должно быть доступно редактируемое поле.
        UIHelper.enableEditText(mTestDescriptionEditText);
        mUserViewLinearLayout.setVisibility(View.INVISIBLE);
        mEditorViewLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setModeUser() {
        super.setModeUser();
        // При переходе из режима "Редактор", остается фокус на редактируемом поле (хотя его уже не видно).
        UIHelper.disableEditText(mTestDescriptionEditText);
        mUserViewLinearLayout.setVisibility(View.VISIBLE);
        mEditorViewLinearLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * Обработка нажатия на кнопку "Начать".
     */
    @OnClick(R.id.btn_start)
    public void startOnClick() {
        if (mTestingRadioButton.isChecked())
            startTest();
        else
            startTraining();
    }

    /**
     * Обработка нажатия на кнопку создания и редактирования вопросов.
     * @param view
     */
    @OnClick(R.id.btn_createQuestions)
    public void onCreateQuestionsClick(View view) {
        packExamTest();
        startTestQuestionActivity();
    }

    /**
     * Обработка нажатия на кнопку сохранить.
     * @param view нажатый элемент.
     */
    @OnClick(R.id.btn_save)
    public void saveOnClick(View view) {
        saveExamTest();
    }

    @OnCheckedChanged(R.id.rb_testing)
    public void onTestingModeCheckedChanged(boolean checked) {
        if (checked)
            mTrainingSettingsView.setVisibility(View.GONE);
    }

    @OnCheckedChanged(R.id.rb_training)
    public void onTrainingModeCheckedChanged(boolean checked) {
        if (checked)
            mTrainingSettingsView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE_EXAM_TEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    examTest = (ExamTest) data.getSerializableExtra(EditQuestionActivity.EXTRA_EXAM_TEST);
                    displayNumTestQuestions();
                    displayNumTrainingQuestionsView();
                }
            }
        }
    }

    public void startTest() {
        if (mIsExistedTest)
            startTestQuestionActivity();
        else
            Toast.makeText(this, R.string.msg_test_isnt_existed, Toast.LENGTH_SHORT).show();
    }

    public void startTraining() {
        if (mIsExistedTest) {
            examTest.setActualNumQuestions(mNumTrainingQuestionsSeekBar.getValue());
            Intent trainingIntent =
                    new Intent(this, TrainingQuestionActivity.class);
            trainingIntent.putExtra(TrainingQuestionActivity.EXTRA_EXAM_TEST, examTest);
            startActivity(trainingIntent);
        }
        else
            Toast.makeText(this, R.string.msg_test_isnt_existed, Toast.LENGTH_SHORT).show();
    }

    private void startTestQuestionActivity() {
        Intent examTestQuestionIntent = null;
        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            case USER:
                examTestQuestionIntent =
                        new Intent(getApplicationContext(), TestQuestionActivity.class);
                examTestQuestionIntent.putExtra(TestQuestionActivity.EXTRA_EXAM_TEST, examTest);
                startActivity(examTestQuestionIntent);
                break;
            case EDITOR:
                examTestQuestionIntent =
                        new Intent(getApplicationContext(), EditQuestionActivity.class);
                examTestQuestionIntent.putExtra(EditQuestionActivity.EXTRA_EXAM_TEST, examTest);
                startActivityForResult(examTestQuestionIntent, REQUEST_CODE_EXAM_TEST);
                break;
        }
    }

    /**
     * Отображение описания теста на экране.
     * @param description Описание теста.
     */
    private void displayDescription(String description) {
        mTestDescriptionEditText.setText(description);
        mTestDescriptionTextView.setText(description);
    }

    /**
     * Отображение настройки количества вопросов для теста.
     */
    private void displayNumTestQuestions() {
        mNumQuestionsSeekBar.setRange(0, examTest.getAllQuestions().size());
        mNumQuestionsSeekBar.setValue(examTest.getNumTestQuestions());
    }

    private void displayTestTime() {

        int minutes = (examTest.getTimeLimit() / 60);
        mTestTimeHoursEditText.setText(String.format("%02d", examTest.getTimeLimit() / 3600));
        mTestTimeMinutesEditText.setText(String.format("%02d", minutes % 60));
        mTestTimeSecondsEditText.setText(String.format("%02d", examTest.getTimeLimit() % 60));

        mTestTimeHoursEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    s.append("00");
                    mTestTimeHoursEditText.setSelection(0, s.toString().length());
                }

            }
        });

        mTestTimeMinutesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    int nMinutes = Integer.parseInt(s.toString());
                    if (nMinutes > 59) s.replace(0, s.toString().length(), "59");
                }
                else {
                    s.append("00");
                    mTestTimeMinutesEditText.setSelection(0, s.toString().length());
                }
            }
        });

        mTestTimeSecondsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    int nSeconds = Integer.parseInt(s.toString());
                    if (nSeconds > 59) s.replace(0, s.toString().length(), "59");
                }
                else {
                    s.append("00");
                    mTestTimeSecondsEditText.setSelection(0, s.toString().length());
                }
            }
        });

        mTestTimeView.setVisibility(View.VISIBLE);
    }

    /**
     * Обновляем отображение настройки количества вопросов в тренировки.
     */
    private void displayNumTrainingQuestionsView() {
        mNumTrainingQuestionsSeekBar.setRange(0, examTest.getAllQuestions().size());
        mNumTrainingQuestionsSeekBar.setValue(examTest.getAllQuestions().size());
    }

    private void saveExamTest() {
        try {
            packExamTest();
            new XmlLoaderInternal().save(this, examTest.getFileName(), examTest);
            displayDescription(examTest.getDescription());
            Toast.makeText(this, R.string.msg_success_saving, Toast.LENGTH_SHORT).show();
        } catch (IOException ioex) {
            Log.e(TAG, ioex.getMessage());
            ioex.printStackTrace();
            Toast.makeText(this, R.string.msg_fail_saving, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Собрать введенные данные для теста.
     */
    private void packExamTest() {
        examTest.setDescription(mTestDescriptionEditText.getText().toString());
        examTest.setNumTestQuestions(mNumQuestionsSeekBar.getValue());
        examTest.setTimeLimit(getTestTimeSeconds());
    }

    /**
     * Изменились ли данные для теста.
     * @return true - изменились.
     */
    private boolean haveChangedTestData() {
        String newDesctiption = mTestDescriptionEditText.getText().toString();
        int newNumTestQuestions = mNumQuestionsSeekBar.getValue();
        int newTestTimeLimit = getTestTimeSeconds();
        if (examTest.getDescription() != null) {
            return (!newDesctiption.equals(examTest.getDescription())) ||
                    (newNumTestQuestions != examTest.getNumTestQuestions()) ||
                    (newTestTimeLimit != examTest.getTimeLimit());
        }
        else {
            // Если описание экзамена равно null, а новое описание не пустое, то было изменение.
            return !newDesctiption.equals("");
        }
    }

    /**
     * Получить время в секудах.
     * @return
     */
    private int getTestTimeSeconds() {
        int nHours = Integer.parseInt(mTestTimeHoursEditText.getText().toString());;
        int nMinutes = Integer.parseInt(mTestTimeMinutesEditText.getText().toString());
        int nSeconds = Integer.parseInt(mTestTimeSecondsEditText.getText().toString());
        int testTimeLimit = nHours * 3600 + nMinutes * 60 + nSeconds;
        return testTimeLimit;
    }

    /**
     * Завершить редактирование.
     * @param eventEndEdit событие которое послужило причиной завершения редактирования.
     */
    private void finishEditing(final EventEndEdit eventEndEdit) {
        if (haveChangedTestData()) {
            AlertDialog.Builder confirmChangesDialog = new AlertDialog.Builder(this);
            confirmChangesDialog.setTitle(R.string.title_finish_editing)
                    .setMessage(R.string.msg_do_editing_save)
                    // Если сохранить изменения.
                    .setPositiveButton(R.string.btn_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveExamTest();
                            dialog.dismiss();
                            finishEditingCallback(eventEndEdit);
                        }
                    })
                    // В противном случае ничего не делаем.
                    .setNegativeButton(R.string.btn_rollback, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finishEditingCallback(eventEndEdit);
                        }
                    }).show();
        }
        else {
            finishEditingCallback(eventEndEdit);
        }
    }

    /**
     * Callback после звершение редактирования.
     * @param eventEndEdit событие которое послужило причиной завершения редактирования.
     */
    private void finishEditingCallback(EventEndEdit eventEndEdit) {
        switch (eventEndEdit) {
            case ON_FINISH:
                setModeUser();
                break;
            case ON_BACK:
                ((TestBoxApp)getApplicationContext()).getExamTree().prev();
                super.onBackPressed();
                break;
        }
    }
}
