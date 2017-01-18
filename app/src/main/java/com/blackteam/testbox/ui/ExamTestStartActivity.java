package com.blackteam.testbox.ui;

import android.app.FragmentManager;
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

/**
 * Стартовая страница для экзамеционного текста.
 */
public class ExamTestStartActivity extends BaseActivity {

    private TextView mTestNameTextView;
    private EditText mTestDescriptionEditText;
    private TextView mTestDescriptionTextView;
    private Button mStartTestButton;
    private LinearLayout mUserViewLinearLayout;
    private LinearLayout mEditorViewLinearLayout;

    private NavigationTree.Node<ExamThemeData> mExamTheme;
    private ExamTest examTest;

    /** Существует ли тест вообще (Был ли он создан ранее). */
    private boolean mIsExistedTest = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_test_start);

        mExamTheme = ((TestBoxApp)getApplicationContext()).getExamTree().getCurElem();

        mTestNameTextView = (TextView) findViewById(R.id.tv_testName);
        mTestDescriptionEditText = (EditText) findViewById(R.id.et_testDescription);
        mTestDescriptionTextView = (TextView) findViewById(R.id.tv_testDescription);
        mStartTestButton = (Button) findViewById(R.id.btn_startTest);
        mUserViewLinearLayout = (LinearLayout) findViewById(R.id.ll_exam_test_start_user);
        mEditorViewLinearLayout = (LinearLayout) findViewById(R.id.ll_exam_test_start_editor);

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
    public void startTestOnClick(View view) {
        if (mIsExistedTest)
            startTestQuestionActivity();
        else
            Toast.makeText(this, R.string.test_isnt_existed, Toast.LENGTH_SHORT).show();
    }

    /**
     * Обработка нажатия на кнопку "Создать вопрос".
     * @param view
     */
    public void createQuestionsOnClick(View view) {
        startTestQuestionActivity();
    }

    /**
     * Обработка нажатия на кнопку сохранить.
     * @param view
     */
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
        Intent examTestQuestionAcitivity =
                new Intent(getApplicationContext(), ExamTestQuestionActivity.class);
        startActivity(examTestQuestionAcitivity);
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
