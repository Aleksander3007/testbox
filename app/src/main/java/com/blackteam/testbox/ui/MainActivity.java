package com.blackteam.testbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.ui.BaseActivity;
import com.blackteam.testbox.ui.ExamThemesActivity;
import com.blackteam.testbox.utils.NavigationTree;

import java.io.Serializable;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Открыть окно с доступными темами для экзамена.
     * @param view
     */
    public void examThemesOpenOnClick(View view) {

        // TODO: Заглушка на доступные темы экзамена.
        NavigationTree<String> examThemes = new NavigationTree<>("Root element");
        NavigationTree.Node<String> examThemesRoot = examThemes.getRootElement();
        NavigationTree.Node<String> node1 = examThemesRoot.addChild("node1");
        NavigationTree.Node<String> node2 = examThemesRoot.addChild("node2");
        examThemesRoot.addChild("node3");
        examThemesRoot.addChild("node4");
        node1.addChild("node1.1");
        node1.addChild("node1.2");
        node2.addChild("node2.1");
        node2.addChild("node2.2");

        ((TestBoxApp)getApplicationContext()).setExamTree(examThemes);

        Intent examThemesActivity = new Intent(this, ExamThemesActivity.class);
        startActivity(examThemesActivity);
    }
}
