package com.blackteam.testbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.blackteam.testbox.ExamThemeData;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.ui.BaseActivity;
import com.blackteam.testbox.ui.ExamThemesActivity;
import com.blackteam.testbox.utils.ExamLoader;
import com.blackteam.testbox.utils.NavigationTree;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private static final String sExamRootStr = "Экзамен";
    private static final String sExamRootId = "0";
    private static final boolean sIsExamRootTest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            NavigationTree<ExamThemeData> examThemes =
                    ExamLoader.loadExam(getApplicationContext());

            // В случае, если приложение запускается впервые.
            if (examThemes == null) {
                examThemes = new NavigationTree<>();
            }

            if (examThemes.getRootElement() == null) {
                examThemes.createRootElement(
                        new ExamThemeData(sExamRootStr, sExamRootId, sIsExamRootTest));
            }

            ((TestBoxApp)getApplicationContext()).setExamTree(examThemes);

        } catch (IOException ioex) {
            Log.e("MainActivity", ioex.getMessage());
            ioex.printStackTrace();
        } catch (XmlPullParserException xppex) {
            Log.e("MainActivity", xppex.getMessage());
            xppex.printStackTrace();
        }
    }

    /**
     * Открыть окно с доступными темами для экзамена.
     * @param view
     */
    @OnClick(R.id.btn_examOpen)
    public void examOpenOnClick(View view) {
        Intent examThemesActivity = new Intent(this, ExamThemesActivity.class);
        startActivity(examThemesActivity);
    }
}
