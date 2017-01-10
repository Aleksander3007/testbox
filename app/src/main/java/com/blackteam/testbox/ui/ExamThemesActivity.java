package com.blackteam.testbox.ui;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.blackteam.testbox.ExamThemeData;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.utils.NavigationTree;
import com.blackteam.testbox.utils.WideTree;

public class ExamThemesActivity extends BaseActivity {

    private ListView mExamThemesListView;
    private FloatingActionButton mCreateExamThemeBtn;
    private FloatingActionButton mCreateTestBtn;

    private NavigationTree.Node<ExamThemeData> mExamTheme;
    private ArrayAdapter<WideTree.Node<ExamThemeData>> mExamThemesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_themes);

        mExamThemesListView = (ListView) findViewById(R.id.lv_exam_themes);
        mCreateExamThemeBtn = (FloatingActionButton) findViewById(R.id.fab_createNewExamTheme);
        mCreateTestBtn = (FloatingActionButton) findViewById(R.id.fab_createNewTest);

        mExamTheme = ((TestBoxApp)getApplicationContext()).getExamTree().getCurElem();

        mExamThemesListAdapter =
                new ArrayAdapter<>(this,
                        R.layout.support_simple_spinner_dropdown_item,
                        mExamTheme.getChildren()
                );

        /** Добавляем слушателя нажатий на list. */
        mExamThemesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {

                // Определяем выбранную экз. тему.
                String theme = ((TextView) itemClicked).getText().toString();
                ExamThemeData examThemeData = new ExamThemeData(theme);

                NavigationTree<ExamThemeData>  examTree =
                        ((TestBoxApp)getApplicationContext()).getExamTree();
                examTree.next(examThemeData);

                if (examTree.getCurElem().hasChildren()) {
                    Intent examThemesActivity =
                            new Intent(getApplicationContext(), ExamThemesActivity.class);
                    startActivity(examThemesActivity);
                }
                else {
                    Intent examTestStartActivity =
                            new Intent(getApplicationContext(), ExamTestStartActivity.class);
                    startActivity(examTestStartActivity);
                }
            }
        });

        mExamThemesListView.setAdapter(mExamThemesListAdapter);
    }

    @Override
    protected void onStop() {
        // При остановки Activity возращаем её в режим пользователя.
        setModeUser();
        super.onStop();
    }

    @Override
    protected void setModeUser() {
        super.setModeUser();
        mCreateExamThemeBtn.hide();
        mCreateTestBtn.hide();
    }

    @Override
    protected void setModeEditor() {
        super.setModeEditor();
        mCreateExamThemeBtn.show();
        if (mExamTheme.hasChildren())
            mCreateTestBtn.hide();
        else
            mCreateTestBtn.show();
    }

    /**
     * Обработка нажатия на кнопку создания новой темы экзамена.
     * @param view
     */
    public void createNewExamThemeOnClick(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        CreatingThemeDialogFragment creatingThemeDialogFragment =
                new CreatingThemeDialogFragment();
        creatingThemeDialogFragment.show(fragmentManager, "creatingThemeDialog");
    }

    /**
     * Добавить новую тему экзамена.
     * @param newExamThemeName Имя новой темы экзамена.
     */
    public void addNewExamTheme(String newExamThemeName) {
        mExamTheme.addChild(new ExamThemeData(newExamThemeName, generateExamThemeId()));
        mExamThemesListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        ((TestBoxApp)getApplicationContext()).getExamTree().prev();
        super.onBackPressed();
    }

    private String generateExamThemeId() {
        return  mExamTheme.getData().getId() + mExamTheme.getChildren().size();
    }
}
