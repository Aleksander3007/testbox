package com.blackteam.testbox.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

import com.blackteam.testbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Диалоговое окно для создания экзамеционной темы.
 */
public class CreatingThemeDialogFragment extends DialogFragment {

    @BindView(R.id.et_newThemeName) EditText mNewThemeNameEditText;
    @BindView(R.id.cb_contains_test) CheckBox mContainsTestCheckBox;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View creatingThemeDialogView =
                inflater.inflate(R.layout.fragment_creating_theme_dialog, container, false);
        unbinder = ButterKnife.bind(this, creatingThemeDialogView);

        // Request focus and show soft keyboard automatically
        // Запрос фокуса и отображение soft keyboard принудительно.
        mNewThemeNameEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return creatingThemeDialogView;
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
        boolean isExamThemeCreated = ((ExamThemesActivity)getActivity())
                .addNewExamTheme(mNewThemeNameEditText.getText().toString(),
                        mContainsTestCheckBox.isChecked());
        if (isExamThemeCreated) {
            dismiss();
        }
        // Если не удалось создать тему.
        else {
            mNewThemeNameEditText.setError(
                    getResources().getString(R.string.msg_eror_add_new_exam_theme));
        }
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
