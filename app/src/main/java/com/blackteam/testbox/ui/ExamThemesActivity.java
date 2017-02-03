package com.blackteam.testbox.ui;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.ExamThemeData;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.utils.ExamLoader;
import com.blackteam.testbox.utils.NavigationTree;
import com.blackteam.testbox.utils.WideTree;

import java.io.IOException;
import java.util.Deque;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Страница с экзамеционными темами.
 * Навигация по экзамеционным темам происходит в рамках одной активити.
 */
public class ExamThemesActivity extends BaseActivity
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    @BindView(R.id.lv_exam_themes) ListView mExamThemesListView;
    @BindView(R.id.bottom_editing_bar) LinearLayout mBottomEditingBar;
    @BindView(R.id.btn_prevPage) Button mPrevPageButton;
    @BindView(R.id.btn_nextPage) Button mNextPageButton;

    /** Имитируем анимацию перехода между активити, т.к. у нас одно активити, а переходы между
     * подтемами есть. */
    private Animation mFlipinAnimation;
    private Animation mFlipoutAnimation;

    private NavigationTree.Node<ExamThemeData> mExamTheme;
    private ArrayAdapter<WideTree.Node<ExamThemeData>> mExamThemesListAdapter;

    /** Были изменения в экзамеционных темах. */
    private boolean hasExamThemeChanged = false;
    /** Редактируемая экзамеционная тема. */
    private ExamThemeData editingExamTheme;
    /** Сохраняем путь откуда было начато редактирование. */
    private Deque<ExamThemeData> mStartPathEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_themes);
        ButterKnife.bind(this);

        // Для того, чтобы вернутся есть стандартная кнопка Android (Back).
        mPrevPageButton.setVisibility(View.INVISIBLE);
        // Если отображено несколько тем, для кого из них отображать детей по нажатию кнопки.
        mNextPageButton.setVisibility(View.INVISIBLE);

        mExamTheme = ((TestBoxApp)getApplicationContext()).getExamTree().getRootElement();
        updateView();

        /** Добавляем слушателя нажатий на list. */
        mExamThemesListView.setOnItemClickListener(this);
        mExamThemesListView.setOnItemLongClickListener(this);

        mFlipinAnimation = AnimationUtils.loadAnimation(this, R.anim.flipin);
        mFlipoutAnimation = AnimationUtils.loadAnimation(this, R.anim.flipout);
    }

    @Override
    public void onBackPressed() {
        mExamTheme = goToParent();
        if (mExamTheme != null) {
            updateView();
            mExamThemesListView.startAnimation(mFlipoutAnimation);
        }
        // Предыдущий активити как раз корневой узел.
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void setModeUser() {
        finishEditing();
        super.setModeUser();
        mBottomEditingBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setModeEditor() {
        super.setModeEditor();
        mBottomEditingBar.setVisibility(View.VISIBLE);
        mStartPathEdit = ((TestBoxApp)getApplicationContext()).getExamTree().getPath();
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
     * Обработка нажатия на кнопку завершения редактирования.
     * @param view
     */
    @OnClick(R.id.btn_finish)
    public void finishEditingOnClick(View view) {
        finishEditing();
        setModeUser();
    }

    /**
     * Сохранить изменения.
     * @param view
     */
    @OnClick(R.id.btn_save)
    public void saveOnClick(View view) {
        saveExamThemes();
    }

    /**
     * Нажатие на элемент списка (т.е. на экзамеционную тему).
     * @param parent
     * @param itemClicked объект, который был нажат.
     * @param position позиция нажатого объекта в списке.
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
        // Определяем выбранную экз. тему.
        String theme = ((TextView) itemClicked).getText().toString();
        ExamThemeData examThemeData = new ExamThemeData(theme);

        NavigationTree<ExamThemeData>  examTree =
                ((TestBoxApp)getApplicationContext()).getExamTree();
        mExamTheme = examTree.next(examThemeData);

        if (!examTree.getCurElem().getData().containsTest()) {
            mExamThemesListView.startAnimation(mFlipinAnimation);
            updateView();
        }
        // Если данная тема содержит тест, то переход на стартовую страницу теста.
        else {
            Intent examTestStartActivity =
                    new Intent(getApplicationContext(), ExamTestStartActivity.class);
            startActivity(examTestStartActivity);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View itemClicked, int position, long id) {
        // Работает только в режиме "Редактор".
        if (((TestBoxApp)getApplicationContext()).getUserType() == TestBoxApp.UserType.EDITOR) {
            // Определяем выбранную экз. тему.
            String theme = ((TextView) itemClicked).getText().toString();
            editingExamTheme = mExamTheme.getChild(new ExamThemeData(theme)).getData();

            EditingThemeDialogFragment editingThemeDialogFragment =
                    EditingThemeDialogFragment.newInstance(editingExamTheme);
            editingThemeDialogFragment.show(getFragmentManager(), "editingThemeDialog");

            return true;
        }
        return false;
    }

    /**
     * Завершить редактирование.
     */
    private void finishEditing() {
        if (hasExamThemeChanged) {
            AlertDialog.Builder confirmChangesDialog = new AlertDialog.Builder(this);
            confirmChangesDialog.setTitle(R.string.title_finish_editing)
                    .setMessage(R.string.msg_do_editing_save)
                    // Если сохранить изменения.
                    .setPositiveButton(R.string.btn_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveExamThemes();
                            dialog.cancel();
                        }
                    })
                    // В противном случае откат.
                    .setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            rollbackChanges();
                            dialog.cancel();
                        }
                    });
            confirmChangesDialog.create().show();
        }
    }

    /**
     * Переход к родителю текущих подтем.
     * @return
     */
    private WideTree.Node<ExamThemeData> goToParent() {
        return ((TestBoxApp)getApplicationContext()).getExamTree().prev();
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

    /**
     * Изменить выбранную тему экзамена.
     * @param examThemeNewName новое имя темы экзамена.
     * @param isTest содержит ли данная тема тест.
     */
    public void editExamTheme(String examThemeNewName, boolean isTest) {
        ExamThemeData newExamThemeData =
                new ExamThemeData(examThemeNewName, editingExamTheme.getId(), isTest);
        mExamTheme.getChild(editingExamTheme).setData(newExamThemeData);
        mExamThemesListAdapter.notifyDataSetChanged();
        hasExamThemeChanged = true;
    }

    /**
     * Удалить выбранную тему и все его подтемы.
     */
    public void deleteExamTheme() {
        AlertDialog.Builder confirmDeletionDialog = new AlertDialog.Builder(this);
        confirmDeletionDialog.setTitle(R.string.title_delete_exam_theme)
                .setMessage(R.string.msg_delete_exam_theme)
                .setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // Удаляем тему и все его подтемы.
                        deleteExamTheme(editingExamTheme);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // Открываем окно редактирования по новой, т.к. оно закрылось.
                        EditingThemeDialogFragment editingThemeDialogFragment =
                                EditingThemeDialogFragment.newInstance(editingExamTheme);
                        editingThemeDialogFragment.show(getFragmentManager(), "editingThemeDialog");
                        dialog.cancel();
                    }
                });
        confirmDeletionDialog.create().show();
    }

    /**
     * Удаление указанной экзам. темы и её подтем.
     * @param examTheme экзам. тема, которая надо удалить.
     */
    private void deleteExamTheme(ExamThemeData examTheme) {
        deleteSubExamThemes(mExamTheme.getChild(examTheme));
        mExamTheme.removeChild(examTheme);
        deleteExamTest(examTheme);
        mExamThemesListAdapter.notifyDataSetChanged();
        hasExamThemeChanged = true;
    }

    /**
     * Удаление подтем указанной экзам. темы.
     * @param examTheme экзам. тема, подтемы которой надо удалить.
     */
    private void deleteSubExamThemes(WideTree.Node<ExamThemeData> examTheme) {
        for (WideTree.Node<ExamThemeData> child : examTheme.getChildren()) {
            child.removeChildren(child);
            deleteExamTest(child.getData());
        }
        examTheme.getChildren().clear();
    }

    /**
     * Удалить экзамеционный тест.
     * @param examTheme экзам. тест, который необходимо удалить.
     */
    private void deleteExamTest(ExamThemeData examTheme) {
        // Если подтема содержала тест то необходимо его удалить.
        if (examTheme.containsTest()) {
            ExamTest examTest = new ExamTest(examTheme.getId());
            boolean successDelete = examTest.delete(getApplicationContext());
            if (!successDelete) {
                Toast.makeText(getApplicationContext(),
                        String.format(getString(R.string.msg_fail_delete_exam_theme), examTheme.getName()),
                        Toast.LENGTH_SHORT);
            }
        }
    }

    private String generateExamThemeId() {
        return  mExamTheme.getData().getId() + mExamTheme.getChildren().size();
    }

    /**
     * Сохранить все экзам. темы.
     * @return true - изменения успешно сохранены.
     */
    private boolean saveExamThemes() {
        try {
            ExamLoader.saveExam(getApplicationContext(),
                    ((TestBoxApp)getApplicationContext()).getExamTree());
            Toast.makeText(this, R.string.msg_successful_saving, Toast.LENGTH_SHORT).show();
            hasExamThemeChanged = false;
            return true;
        } catch (IOException ioex) {
            Log.e("ExamThemesA", ioex.getMessage());
            ioex.printStackTrace();
            Toast.makeText(this, R.string.msg_error_saving, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Откат изменений до сохраненных.
     * @return true - откат успешно завершен.
     */
    private boolean rollbackChanges() {
        // Загружаем последнюю информацию до текущих изменений (откатываемся).
        boolean isLoaded = ((TestBoxApp)getApplicationContext()).loadExamTree();
        if (isLoaded) {
            // Возращаемся на место, откуда было начато редактирование.
            ((TestBoxApp)getApplicationContext()).getExamTree().setPath(mStartPathEdit);
            mExamTheme = ((TestBoxApp)getApplicationContext()).getExamTree().getCurElem();
            // Обновляем отображение.
            updateView();
            hasExamThemeChanged = false;
        }
        return  isLoaded;
    }

    /**
     * Обновляем отображение.
     */
    private void updateView() {
        mExamThemesListAdapter =
                new ArrayAdapter<>(this,
                        R.layout.support_simple_spinner_dropdown_item,
                        mExamTheme.getChildren()
                );
        mExamThemesListView.setAdapter(mExamThemesListAdapter);
    }
}
