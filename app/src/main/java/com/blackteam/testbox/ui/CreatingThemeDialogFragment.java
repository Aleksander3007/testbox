package com.blackteam.testbox.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.blackteam.testbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Диалоговое окно для создания экзамеционной темы.
 */
public class CreatingThemeDialogFragment extends DialogFragment {

    @BindView(R.id.et_newThemeName) EditText newThemeNameEditText;
    @BindView(R.id.cb_contains_test) CheckBox mContainsTestCheckBox;
    private Unbinder binder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View creatingThemeDialogView =
                inflater.inflate(R.layout.fragment_creating_theme_dialog, container, false);
        binder = ButterKnife.bind(this, creatingThemeDialogView);
        return creatingThemeDialogView;
    }

    /**
     * Нажатие на кнопку подтверждения введенных данных.
     * @param view
     */
    @OnClick(R.id.btn_ok)
    public void confirmOnClick(View view) {
        ((ExamThemesActivity)getActivity())
                .addNewExamTheme(newThemeNameEditText.getText().toString(),
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
}
