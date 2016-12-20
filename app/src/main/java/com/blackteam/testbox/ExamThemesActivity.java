package com.blackteam.testbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ExamThemesActivity extends BaseActivity {

    private ListView mExamThemesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_themes);

        mExamThemesListView = (ListView) findViewById(R.id.lv_exam_themes);

        final String[] examThemes = new String[] {
                "Java",
                "Git",
                "Android SDK"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, examThemes);

        mExamThemesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                Toast.makeText(getApplicationContext(), ((TextView) itemClicked).getText(),
                        Toast.LENGTH_LONG).show();
                Log.i("ExamThemesActivity", "hello.");
            }
        });

        mExamThemesListView.setAdapter(adapter);
    }

    public void createNewExamThemeOnClick(View view) {

    }
}
