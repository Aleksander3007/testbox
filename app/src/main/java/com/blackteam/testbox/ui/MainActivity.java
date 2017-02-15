package com.blackteam.testbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private int mValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((TestBoxApp)getApplicationContext()).loadExam();
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

    /**
     * Нажатие на кнопку настроек.
     */
    @OnClick(R.id.btn_settingsOpen)
    public void onExamSettingsClick(View view) {
        SettingsActivity settingsActivity = new SettingsActivity();
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
}
