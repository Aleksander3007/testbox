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
import android.widget.Toast;

import com.blackteam.testbox.ExamThemeData;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.utils.NavigationTree;
import com.blackteam.testbox.utils.WideTree;

public class ExamThemesActivity extends BaseActivity {

    private ListView mExamThemesListView;
    private FloatingActionButton mCreateExamThemeBtn;

    private NavigationTree.Node<ExamThemeData> mExamThemes;
    private ArrayAdapter<WideTree.Node<ExamThemeData>> mExamThemesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_themes);

        mExamThemesListView = (ListView) findViewById(R.id.lv_exam_themes);
        mCreateExamThemeBtn = (FloatingActionButton) findViewById(R.id.fab_createNewExamTheme);

        mExamThemes = ((TestBoxApp)getApplicationContext()).getExamTree().getCurElem();

        mExamThemesListAdapter =
                new ArrayAdapter<>(this,
                        R.layout.support_simple_spinner_dropdown_item,
                        mExamThemes.getChildren()
                );

        /** Добавляем слушателя нажатий на list. */
        mExamThemesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {

                String theme = ((TextView) itemClicked).getText().toString();

                /** Навигация вперед. */
                ((TestBoxApp)getApplicationContext()).getExamTree().next(new ExamThemeData(theme));

                Intent examThemesActivity = new Intent(getApplicationContext(), ExamThemesActivity.class);
                startActivity(examThemesActivity);
            }
        });

        mExamThemesListView.setAdapter(mExamThemesListAdapter);

        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            case USER:
                setModeUser();
                break;
            case EDITOR:
                setModeEditor();
                break;
        }
    }

    private void setModeUser() {
        mCreateExamThemeBtn.setVisibility(View.INVISIBLE);
    }

    private void setModeEditor() {
        mCreateExamThemeBtn.setVisibility(View.VISIBLE);
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
        mExamThemes.addChild(new ExamThemeData(newExamThemeName, generateExamThemeId()));
        mExamThemesListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mi_userType) {
            switch (((TestBoxApp)getApplicationContext()).getUserType()) {
                case USER:
                    setModeEditor();
                    break;
                case EDITOR:
                    setModeUser();
                    break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ((TestBoxApp)getApplicationContext()).getExamTree().prev();
        super.onBackPressed();
    }

    private String generateExamThemeId() {
        return  mExamThemes.getData().getId() + mExamThemes.getChildren().size();
    }
}
