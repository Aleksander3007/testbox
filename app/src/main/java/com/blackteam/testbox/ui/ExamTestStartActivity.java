package com.blackteam.testbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blackteam.testbox.ExamThemeData;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.utils.NavigationTree;
import com.blackteam.testbox.utils.UIHelper;

/**
 * Стартовая страница для экзамеционного текста.
 */
public class ExamTestStartActivity extends BaseActivity {

    private TextView mTestNameTextView;
    private EditText mTestDescriptionEditText;
    private Button mStartTestButton;
    private Button mCreateQuestionsButton;

    private NavigationTree.Node<ExamThemeData> mExamTheme;

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
    }

    @Override
    protected void setModeUser() {
        super.setModeUser();
        UIHelper.disableEditText(mTestDescriptionEditText);
        mStartTestButton.setVisibility(View.VISIBLE);
        mCreateQuestionsButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setModeEditor() {
        super.setModeEditor();
        UIHelper.enableEditText(mTestDescriptionEditText);
        mStartTestButton.setVisibility(View.INVISIBLE);
        mCreateQuestionsButton.setVisibility(View.VISIBLE);
    }

    public void createQuestionsOnClick(View v) {
        // TODO: Обработка нажатия на кнопку содать вопросы.
    }
}
