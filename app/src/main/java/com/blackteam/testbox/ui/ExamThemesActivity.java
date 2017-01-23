package com.blackteam.testbox.ui;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackteam.testbox.ExamThemeData;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.utils.ExamLoader;
import com.blackteam.testbox.utils.NavigationTree;
import com.blackteam.testbox.utils.WideTree;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExamThemesActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.lv_exam_themes) ListView mExamThemesListView;
    @BindView(R.id.bottom_editing_bar) LinearLayout mBottomEditingBar;
    @BindView(R.id.btn_prevPage) Button mPrevPageButton;
    @BindView(R.id.btn_nextPage) Button mNextPageButton;

    private NavigationTree.Node<ExamThemeData> mExamTheme;
    private ArrayAdapter<WideTree.Node<ExamThemeData>> mExamThemesListAdapter;

    /** Были изменения в экзамеционных темах. */
    private boolean hasExamThemeChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_themes);
        ButterKnife.bind(this);

        // Для того, чтобы вернутся есть стандартная кнопка Android (Back).
        mPrevPageButton.setVisibility(View.INVISIBLE);
        // Если отображено несколько тем, для кого из них отображать детей по нажатию кнопки.
        mNextPageButton.setVisibility(View.INVISIBLE);

        mExamTheme = ((TestBoxApp)getApplicationContext()).getExamTree().getCurElem();

        mExamThemesListAdapter =
                new ArrayAdapter<>(this,
                        R.layout.support_simple_spinner_dropdown_item,
                        mExamTheme.getChildren()
                );

        /** Добавляем слушателя нажатий на list. */
        mExamThemesListView.setOnItemClickListener(this);

        mExamThemesListView.setAdapter(mExamThemesListAdapter);
    }

    @Override
    protected void onStop() {
        // При остановки Activity возращаем её в режим пользователя.
        setModeUser();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        goToParent();
        super.onBackPressed();
    }

    @Override
    protected void setModeUser() {
        super.setModeUser();
        mBottomEditingBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setModeEditor() {
        super.setModeEditor();
        mBottomEditingBar.setVisibility(View.VISIBLE);
    }

    /**
     * Обработка нажатия на кнопку создания новой темы экзамена.
     * @param view
     */
    @OnClick(R.id.fab_createNewItem)
    public void createNewExamThemeOnClick(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        CreatingThemeDialogFragment creatingThemeDialogFragment =
                new CreatingThemeDialogFragment();
        creatingThemeDialogFragment.show(fragmentManager, "creatingThemeDialog");
    }

    /**
     * Завершить редактирование.
     * @param view
     */
    @OnClick(R.id.btn_finish)
    public void finishEditingOnClick(View view) {
        if (hasExamThemeChanged) {
            // TODO: Спрашиваем, сохранить ли изменения.
            // Если нет, то надо загружать из файла. и возратить всё на начальную позицию.
        }
        setModeUser();
    }

    /**
     * Сохранить изменения.
     * @param view
     */
    @OnClick(R.id.btn_save)
    public void saveOnClick(View view) {
        try {
            ExamLoader.saveExam(getApplicationContext(),
                    ((TestBoxApp)getApplicationContext()).getExamTree());
            Toast.makeText(this, R.string.msg_successful_saving, Toast.LENGTH_SHORT).show();
        } catch (IOException ioex) {
            Log.e("ExamThemesA", ioex.getMessage());
            ioex.printStackTrace();
            Toast.makeText(this, R.string.msg_error_saving, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
        // Определяем выбранную экз. тему.
        String theme = ((TextView) itemClicked).getText().toString();
        ExamThemeData examThemeData = new ExamThemeData(theme);

        NavigationTree<ExamThemeData>  examTree =
                ((TestBoxApp)getApplicationContext()).getExamTree();
        examTree.next(examThemeData);

        if (!examTree.getCurElem().getData().containsTest()) {
            Intent examThemesActivity =
                    new Intent(getApplicationContext(), ExamThemesActivity.class);
            startActivity(examThemesActivity);
        }
        // Если данная тема содержит тест, то переход на стартовую страницу теста.
        else {
            Intent examTestStartActivity =
                    new Intent(getApplicationContext(), ExamTestStartActivity.class);
            startActivity(examTestStartActivity);
        }
    }

    private void goToParent() {
        ((TestBoxApp)getApplicationContext()).getExamTree().prev();
    }

    /**
     * Добавить новую тему экзамена.
     * @param newExamThemeName имя новой темы экзамена.
     * @param isTest содержит ли данная тема тест.
     * @return удалось ли добавить новую тему (true - да, удалось).
     */
    public boolean addNewExamTheme(String newExamThemeName, boolean isTest) {
        ExamThemeData newExamThemeData =
                new ExamThemeData(newExamThemeName, generateExamThemeId(), isTest);
        // Если темы с текущем именем не существует, то добавляем.
        if (!mExamTheme.containsChild(newExamThemeData)) {
            mExamTheme.addChild(newExamThemeData);
            mExamThemesListAdapter.notifyDataSetChanged();
            hasExamThemeChanged = true;
            return true;
        }
        return false;
    }

    private String generateExamThemeId() {
        return  mExamTheme.getData().getId() + mExamTheme.getChildren().size();
    }
}
