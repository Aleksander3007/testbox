package com.blackteam.testbox.ui;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.blackteam.testbox.ExamThemeData;
import com.blackteam.testbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Диалоговое окно для редактирования экзамеционной темы.
 */

public class EditingThemeDialogFragment extends DialogFragment{

    private static final String sExamThemeNameArg = "ExamThemeName";
    private static final String sContainsTestArg = "ContainsTest";

    @BindView(R.id.et_themeName) EditText mThemeNameEditText;
    @BindView(R.id.cb_contains_test) CheckBox mContainsTestCheckBox;
    private Unbinder unbinder;

    /**
     * Создание экземпляра {@link EditingThemeDialogFragment}.
     * @param editingExamTheme Редактируемая тема экзамена.
     */
    public static EditingThemeDialogFragment newInstance(ExamThemeData editingExamTheme) {
        EditingThemeDialogFragment fragment = new EditingThemeDialogFragment();
        Bundle args = new Bundle();
        args.putString(sExamThemeNameArg, editingExamTheme.getName());
        args.putBoolean(sContainsTestArg, editingExamTheme.containsTest());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View editingThemeDialogView =
                inflater.inflate(R.layout.fragment_editing_theme_dialog, container, false);
        unbinder = ButterKnife.bind(this, editingThemeDialogView);

        mThemeNameEditText.setText(getArguments().getString(sExamThemeNameArg));
        mContainsTestCheckBox.setChecked(getArguments().getBoolean(sContainsTestArg));
        return editingThemeDialogView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Нажатие на кнопку подтверждения введенных данных.
     * @param view
     */
    @OnClick(R.id.btn_ok)
    public void confirmOnClick(View view) {
        ((ExamThemesActivity)getActivity())
                .editExamTheme(mThemeNameEditText.getText().toString(),
                        mContainsTestCheckBox.isChecked());
        dismiss();
    }

    /**
     * Нажатие на кнопку отмены введенных данных.
     * @param view
     */
    @OnClick(R.id.btn_cancel)
    public void cancelOnClick(View view) {
        dismiss();
    }

    /**
     * Нажатие на кнопку удаления темы.
     * @param view
     */
    @OnClick(R.id.btn_delete)
    public void deleteOnClcik(View view) {
        ((ExamThemesActivity)getActivity()).deleteExamTheme();
        dismiss();
    }
}
