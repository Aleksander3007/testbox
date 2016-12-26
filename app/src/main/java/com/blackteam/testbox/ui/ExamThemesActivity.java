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

import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.utils.NavigationTree;

public class ExamThemesActivity extends BaseActivity {

    private ListView mExamThemesListView;
    private FloatingActionButton mCreateExamThemeBtn;

    private NavigationTree.Node<String> mExamThemes;
    private ArrayAdapter<String> mExamThemesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_themes);

        mExamThemesListView = (ListView) findViewById(R.id.lv_exam_themes);
        mCreateExamThemeBtn = (FloatingActionButton) findViewById(R.id.fab_createNewExamTheme);

        mExamThemes = (NavigationTree.Node<String>) getIntent().getSerializableExtra("EXAM_DATA");

        mExamThemesListAdapter =
                new ArrayAdapter<String>(this,
                        R.layout.support_simple_spinner_dropdown_item,
                        mExamThemes.getChildrenNames()
                );

        mExamThemesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                Toast.makeText(getApplicationContext(), ((TextView) itemClicked).getText(),
                        Toast.LENGTH_LONG).show();

                Intent examThemesActivity = new Intent(getApplicationContext(), ExamThemesActivity.class);
                examThemesActivity.putExtra("EXAM_DATA",
                        mExamThemes.getChild((String) ((TextView) itemClicked).getText()));
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

    public void createNewExamThemeOnClick(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        CreatingThemeDialogFragment creatingThemeDialogFragment =
                new CreatingThemeDialogFragment();
        creatingThemeDialogFragment.show(fragmentManager, "creatingThemeDialog");
    }

    public void addNewExamTheme(String newExamThemeName) {
        mExamThemes.addChild(newExamThemeName);
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
}
