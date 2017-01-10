package com.blackteam.testbox.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.utils.UIHelper;

// TODO: Сделать возможность менять уже сущетсвующие вопросы.

/**
 * Страница с экзамеционными вопросами.
 */
public class ExamTestQuestionActivity extends BaseActivity {

    private ListView mAnswersListView;
    private EditText mQuestionEditText;

    private ArrayAdapter<String> mAnswersListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_test_question);

        mAnswersListView = (ListView) findViewById(R.id.lv_answers);
        mQuestionEditText = (EditText) findViewById(R.id.et_question);

        // TODO: Загружаем данные из файла.
        // mExamTheme = ((TestBoxApp)getApplicationContext()).getExamTree().getCurElem();
        // int testId = mExamTheme.getData().getId();
        // Грузим файл по адресу "et" + String.valueOf(testId) + ".xml";
        // Из него и считываем данные.
        // Перемешиваем ответы.

        // TODO: Теста не существует.
        // Если пустой текст то необходимо выводить сообщение об этом.
        // setContentView(R.layout.activity_test_doesnt_exist);
        // А если перейдём на другой режим, то необходимо поставлять.

        String[] values = new String[] { "Android", "iPhone", "WindowsMobile" };

        switch (((TestBoxApp)getApplicationContext()).getUserType()) {
            case USER:
                UIHelper.disableEditText(mQuestionEditText);
                mAnswersListAdapter = new ArrayAdapter<>(this, R.layout.listview_elem_answer,
                        R.id.tv_answerText, values);
                break;
            case EDITOR:
                UIHelper.enableEditText(mQuestionEditText);
                mAnswersListAdapter = new ArrayAdapter<>(this, R.layout.listview_elem_edit_answer,
                        R.id.et_answerText, values);
                break;
        }

        mAnswersListView.setAdapter(mAnswersListAdapter);
    }

    @Override
    protected void setModeUser() {
        // TODO: Спрашивать: Завершить редактирование тестов?
    }

    @Override
    protected void setModeEditor() {
        // TODO: Спрашивать: Завершить тест и перейти к редактированию?
    }
}
