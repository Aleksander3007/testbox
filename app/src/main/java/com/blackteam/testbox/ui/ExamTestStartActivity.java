package com.blackteam.testbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Стартовая страница для экзамеционного текста.
 */
public class ExamTestStartActivity extends BaseActivity {

    @BindView(R.id.tv_testName) TextView mTestNameTextView;
    @BindView(R.id.et_testDescription) EditText mTestDescriptionEditText;
    @BindView(R.id.tv_testDescription) TextView mTestDescriptionTextView;
    @BindView(R.id.btn_startTest) Button mStartTestButton;
    @BindView(R.id.ll_exam_test_start_user) LinearLayout mUserViewLinearLayout;
    @BindView(R.id.ll_exam_test_start_editor) LinearLayout mEditorViewLinearLayout;

    private NavigationTree.Node<ExamThemeData> mExamTheme;
    private ExamTest examTest;

    /** Существует ли тест вообще (Был ли он создан ранее). */
    private boolean mIsExistedTest = false;

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
            examTest.load(getApplicationContext());
            displayDescription(examTest.getDescription());
            mIsExistedTest = true;

        } catch (FileNotFoundException fnfex) {
            // Если файл не найден значит он еще не был создан.
            mIsExistedTest = false;
        } catch (IOException ioex) {
            Log.e("ExamTestQuestionA", ioex.getMessage());
            ioex.printStackTrace();
        } catch (XmlPullParserException xppex) {
            Log.e("ExamTestQuestionA", xppex.getMessage());
            xppex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        ((TestBoxApp)getApplicationContext()).getExamTree().prev();
        super.onBackPressed();
    }

    @Override
    protected void setModeUser() {
        super.setModeUser();
        // При переходе из режима "Редактор", остается фокус на редактируемом поле (хотя его уже не видно).
        UIHelper.disableEditText(mTestDescriptionEditText);
        mUserViewLinearLayout.setVisibility(View.VISIBLE);
        mEditorViewLinearLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setModeEditor() {
        super.setModeEditor();
        // При переходе в режим "Редактор", должно быть доступно редактируемое поле.
        UIHelper.enableEditText(mTestDescriptionEditText);
        mUserViewLinearLayout.setVisibility(View.INVISIBLE);
        mEditorViewLinearLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Обработка нажатия на кнопку "Начать тест".
     * @param view
     */
    @OnClick(R.id.btn_startTest)
    public void startTestOnClick(View view) {
        if (mIsExistedTest)
            startTestQuestionActivity();
        else
            Toast.makeText(this, R.string.msg_test_isnt_existed, Toast.LENGTH_SHORT).show();
    }

    /**
     * Обработка нажатия на кнопку "Создать вопрос".
     * @param view
     */
    @OnClick(R.id.btn_createQuestions)
    public void createQuestionsOnClick(View view) {
        startTestQuestionActivity();
    }

    /**
     * Обработка нажатия на кнопку сохранить.
     * @param view
     */
    @OnClick(R.id.btn_save)
    public void saveOnClick(View view) {
        try {
            examTest.setDescription(mTestDescriptionEditText.getText().toString());
            examTest.save(getApplicationContext());
            displayDescription(examTest.getDescription());
            Toast.makeText(this, R.string.msg_successful_saving, Toast.LENGTH_SHORT).show();
        } catch (IOException ioex) {
            Log.e("ExamTestQuestionA", ioex.getMessage());
            ioex.printStackTrace();
            Toast.makeText(this, R.string.msg_error_saving, Toast.LENGTH_SHORT).show();
        }
    }

    private void startTestQuestionActivity() {
        Intent examTestQuestionIntent = null;
        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            case USER:
                examTestQuestionIntent =
                        new Intent(getApplicationContext(), TestQuestionActivity.class);
                break;
            case EDITOR:
                examTestQuestionIntent =
                        new Intent(getApplicationContext(), EditableQuestionActivity.class);

                break;
        }

        examTestQuestionIntent.putExtra("ExamTest", examTest);
        startActivity(examTestQuestionIntent);
    }

    /**
     * Отображение описания теста на экране.
     * @param description Описание теста.
     */
    private void displayDescription(String description) {
        mTestDescriptionEditText.setText(description);
        mTestDescriptionTextView.setText(description);
    }
}
