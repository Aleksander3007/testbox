package com.blackteam.testbox.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.blackteam.testbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Диалоговое окно для создания и редактирования экзамеционной темы.
 */
public class EditThemeDialogFragment extends DialogFragment {

    public static final String ARG_THEME_NAME = "ThemeName";
    public static final String ARG_CONTAINS_TEST = "ContainsTest";
    public static final String ARG_HAS_SUBTHEMES = "HasSubThemes";
    public static final String ARG_IS_NEW_THEME = "IsNewTheme";

    @BindView(R.id.et_themeName) EditText mThemeNameEditText;
    @BindView(R.id.cb_containsTest) CheckBox mContainsTestCheckBox;
    @BindView(R.id.btn_delete) Button mDeleteButton;
    private Unbinder unbinder;

    /** Диалог открыт для создания новой темы? */
    private boolean mIsNewTheme;

    /**
     * Создание экземпляра класса {@link EditThemeDialogFragment}.
     */
    public static EditThemeDialogFragment newInstance() {

        EditThemeDialogFragment dialog = new EditThemeDialogFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_NEW_THEME, true);
        dialog.setArguments(args);

        return dialog;
    }

    /**
     * Создание экземпляра класса {@link EditThemeDialogFragment}.
     * @param themeName название темы.
     * @param containsTest содержит ли тест.
     * @param hasSubThemes имеет ли подтемы.
     * @param isNewTheme тема новая?
     */
    public static EditThemeDialogFragment newInstance(String themeName, boolean containsTest,
                                               boolean hasSubThemes, boolean isNewTheme) {

        EditThemeDialogFragment dialog = new EditThemeDialogFragment();

        Bundle args = new Bundle();
        args.putString(ARG_THEME_NAME, themeName);
        args.putBoolean(ARG_CONTAINS_TEST, containsTest);
        args.putBoolean(ARG_HAS_SUBTHEMES, hasSubThemes);
        args.putBoolean(ARG_IS_NEW_THEME, isNewTheme);
        dialog.setArguments(args);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View editDialogView =
                inflater.inflate(R.layout.fragment_edit_theme_dialog, container, false);
        unbinder = ButterKnife.bind(this, editDialogView);

        String themeName = getArguments().getString(ARG_THEME_NAME);
        boolean containsTest = getArguments().getBoolean(ARG_CONTAINS_TEST);
        boolean hasSubThemes = getArguments().getBoolean(ARG_HAS_SUBTHEMES);
        mIsNewTheme = getArguments().getBoolean(ARG_IS_NEW_THEME);

        if (themeName != null) mThemeNameEditText.setText(themeName);

        if (!hasSubThemes)
            mContainsTestCheckBox.setChecked(containsTest);
        else
            mContainsTestCheckBox.setVisibility(View.GONE);

        if (mIsNewTheme) mDeleteButton.setVisibility(View.GONE);

        // Запрос фокуса и отображение soft keyboard принудительно.
        mThemeNameEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return editDialogView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Нажатие на кнопку подтверждения введенных данных.
     * @param view нажатый элемент.
     */
    @OnClick(R.id.btn_ok)
    public void confirmOnClick(View view) {
        boolean success = (mIsNewTheme) ? createTheme() : editTheme();
        if (success) dismiss();
    }
    /**
     * Нажатие на кнопку отмены введенных данных.
     * @param view нажатый элемент.
     */
    @OnClick(R.id.btn_cancel)
    public void cancelOnClick(View view) {
        dismiss();
    }

    /**
     * Нажатие на кнопку удаления редактируемого элемента.
     * @param view нажатый элемент.
     */
    @OnClick(R.id.btn_delete)
    public void deleteOnClcik(View view) {
        ((ExamThemesActivity)getActivity()).deleteTheme();
        dismiss();
    }

    private boolean editTheme()
    {
        boolean success = ((ExamThemesActivity)getActivity())
                .editTheme(mThemeNameEditText.getText().toString(),
                        mContainsTestCheckBox.isChecked());
        // Если не удалось переименовать тему.
        if (!success) {
            mThemeNameEditText.setError(
                    getResources().getString(R.string.msg_error_edit_exam_theme));
        }

        return success;
    }

    private boolean createTheme()
    {
        boolean success = ((ExamThemesActivity)getActivity())
                .addNewExamTheme(mThemeNameEditText.getText().toString(),
                        mContainsTestCheckBox.isChecked());
        // Если не удалось создать тему.
        if (!success) {
            mThemeNameEditText.setError(
                    getResources().getString(R.string.msg_eror_add_new_exam_theme));
        }

        return success;
    }
}
