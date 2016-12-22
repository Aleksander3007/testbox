package com.blackteam.testbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.blackteam.testbox.R;
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
        NavigationTree.Node<String> node11 = examThemesRoot.addChild("node1.1");
        NavigationTree.Node<String> node12 = examThemesRoot.addChild("node1.2");
        examThemesRoot.addChild("node1.3");
        examThemesRoot.addChild("node1.4");
        node11.addChild("node2.1");
        node11.addChild("node2.2");
        node12.addChild("node2.3");
        node12.addChild("node2.4");

        Intent examThemesActivity = new Intent(this, ExamThemesActivity.class);
        examThemesActivity.putExtra("EXAM_DATA", examThemes.getRootElement());
        startActivity(examThemesActivity);
    }
}
