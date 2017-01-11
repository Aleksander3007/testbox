package com.blackteam.testbox.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.utils.UIHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: Сделать возможность менять уже сущетсвующие вопросы.

/**
 * Страница с экзамеционными вопросами.
 */
public class ExamTestQuestionActivity extends BaseActivity {

    private ListView mAnswersListView;
    private EditText mQuestionEditText;
    private FloatingActionButton mCreateAnswerFab;

    private List<String> answers = new ArrayList<>();
    private ArrayAdapter<String> mAnswersListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_test_question);

        mAnswersListView = (ListView) findViewById(R.id.lv_answers);
        mQuestionEditText = (EditText) findViewById(R.id.et_question);
        mCreateAnswerFab = (FloatingActionButton) findViewById(R.id.fab_createNewAnswer);

        // TODO: Загружаем данные из файла.
        // mExamTheme = ((TestBoxApp)getApplicationContext()).getExamTree().getCurElem();
        // int testId = mExamTheme.getData().getId();
        // Грузим файл по адресу "et" + String.valueOf(testId) + ".xml";
        // Из него и считываем данные.
        // Перемешиваем ответы.

        answers.addAll(Arrays.asList("Android", "iPhone", "WindowsMobile"));

        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            case USER:
                UIHelper.disableEditText(mQuestionEditText);
                mAnswersListAdapter = new ArrayAdapter<>(this, R.layout.listview_elem_answer,
                        R.id.tv_answerText, answers);
                break;
            case EDITOR:
                UIHelper.enableEditText(mQuestionEditText);
                mAnswersListAdapter = new ArrayAdapter<>(this, R.layout.listview_elem_edit_answer,
                        R.id.et_answerText, answers);
                break;
        }

        mAnswersListView.setAdapter(mAnswersListAdapter);
    }

    @Override
    protected void setModeUser() {
        // TODO: Спрашивать: Завершить редактирование тестов?
        super.setModeUser();
        mCreateAnswerFab.hide();
    }

    @Override
    protected void setModeEditor() {
        // TODO: Спрашивать: Завершить тест и перейти к редактированию?
        super.setModeEditor();
        mCreateAnswerFab.show();
    }

    /**
     * Обработка нажатия на кнопку создать новый ответ.
     * @param view
     */
    public void createNewAnswerOnClick(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        CreatingAnswerDialogFragment creatingAnswerDialogFragment =
                new CreatingAnswerDialogFragment();
        creatingAnswerDialogFragment.show(fragmentManager, "creatingAnswerDialog");
    }

    public void addNewAnswer(String answer) {
        try {
            answers.add(answer);
            mAnswersListAdapter.notifyDataSetChanged();
        }
        catch (Exception ex) {
            Log.d("ExamTestQuestionActiv", ex.getMessage());
        }
    }
}
