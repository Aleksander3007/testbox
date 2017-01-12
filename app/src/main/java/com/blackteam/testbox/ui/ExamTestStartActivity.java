package com.blackteam.testbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    private Button mStartTestButton;
    private Button mCreateQuestionsButton;

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
        mStartTestButton = (Button) findViewById(R.id.btn_startTest);
        mCreateQuestionsButton = (Button) findViewById(R.id.btn_createQuestions);

        mTestNameTextView.setText(mExamTheme.getData().getName());

        // Загружаем данные о тесте.
        examTest = new ExamTest(mExamTheme.getData().getId());
        try {
            examTest.load(getApplicationContext());
            mTestDescriptionEditText.setText(examTest.getDescription());
            mIsExistedTest = true;

        } catch (FileNotFoundException fnfex) {
            // Если файл не найден значит он еще не был создан.
            mTestDescriptionEditText.setText(R.string.test_isnt_existed);
            mStartTestButton.setVisibility(View.INVISIBLE);
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
        UIHelper.disableEditText(mTestDescriptionEditText);
        if (mIsExistedTest) mStartTestButton.setVisibility(View.VISIBLE);
        mCreateQuestionsButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setModeEditor() {
        super.setModeEditor();
        UIHelper.enableEditText(mTestDescriptionEditText);
        mStartTestButton.setVisibility(View.INVISIBLE);
        mCreateQuestionsButton.setVisibility(View.VISIBLE);
    }

    public void startTestOnClick(View v) {
        startTestQuestionActivity();
    }

    public void createQuestionsOnClick(View v) {
        startTestQuestionActivity();
    }

    private void startTestQuestionActivity() {
        Intent examTestQuestionAcitivity =
                new Intent(getApplicationContext(), ExamTestQuestionActivity.class);
        startActivity(examTestQuestionAcitivity);
    }
}
