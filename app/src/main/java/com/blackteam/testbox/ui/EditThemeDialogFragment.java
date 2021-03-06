package com.blackteam.testbox.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

    public static final String ARG_THEME_NAME = "ARG_THEME_NAME";
    public static final String ARG_CONTAINS_TEST = "ARG_CONTAINS_TEST";
    public static final String ARG_HAS_SUBTHEMES = "ARG_HAS_SUBTHEMES";
    public static final String ARG_IS_NEW_THEME = "ARG_IS_NEW_THEME";

    /**
     * Activity, которое создаст данное диалоговое окно должна реализовать
     * этот интерфейс (чтобы получать и обрабатывать callback-и диалогового окна).
     */
    public interface NoticeDialogListener {
        boolean addNewExamTheme(String newExamThemeName, boolean isTest);
        boolean editTheme(String examThemeNewName, boolean isTest);
        void deleteTheme();
    }
    // Для отправки callback-ов Activity.
    NoticeDialogListener mListener;

    @BindView(R.id.et_themeName) EditText mThemeNameEditText;
    @BindView(R.id.cb_containsTest) CheckBox mContainsTestCheckBox;
    @BindView(R.id.btn_delete) Button mDeleteButton;
    private Unbinder unbinder;

    /** Диалог открыт для создания новой темы? */
    private boolean mIsNewTheme;
    /** Содержит ли тема тест. */
    private boolean mСontainsTest;

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

    // Проблема заключалась в том, что onAttach(Context context) в версиях API < 23,
    // не вызывается. А вызывается onAttach(Activity activity), который является Deprecated,
    // но используется внутри super.onAttach(context);
    // Issue android framework track: https://code.google.com/p/android/issues/detail?id=183358

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // не убраем вызов onAttachToContext() здесь для наглядности и понимания, а также если
        // в случае когда данный метод вызвал НЕ activity.
        // хотя по идеи super.onAttach(Activity activity) вызывает внутри super.onAttach(context).
        onAttachToContext(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Для версий > 23(marshmallow) вызывается onAttach(Context context),
        // внутри которого вызывается onAttach(Activity activity), поэтому
        // здесь проверяем версию, чтобы не было двойного вызова onAttachToContext().
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    protected void onAttachToContext(Context context) {
        try {
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // По умолчанию в ранних версиях Android, DialogFragment выводится с заголовком.

        // Запрос window без заголовка.
        if (dialog.getWindow() != null)
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View editDialogView =
                inflater.inflate(R.layout.fragment_edit_theme_dialog, container, false);
        unbinder = ButterKnife.bind(this, editDialogView);

        String themeName = getArguments().getString(ARG_THEME_NAME);
        mСontainsTest = getArguments().getBoolean(ARG_CONTAINS_TEST);
        boolean hasSubThemes = getArguments().getBoolean(ARG_HAS_SUBTHEMES);
        mIsNewTheme = getArguments().getBoolean(ARG_IS_NEW_THEME);

        if (themeName != null) mThemeNameEditText.setText(themeName);

        if (!hasSubThemes) {
            mContainsTestCheckBox.setChecked(mСontainsTest);
            /** Нельзя тест перевести в разряд темы. */
            mContainsTestCheckBox.setClickable(!mСontainsTest);
        }
        else
            mContainsTestCheckBox.setVisibility(View.GONE);

        if (mIsNewTheme) mDeleteButton.setVisibility(View.GONE);

        // Запрос фокуса и отображение soft keyboard принудительно.
        mThemeNameEditText.requestFocus();
        /* getWindow() возращает null в случае, если activity невидна;
         *  если activity невидна, просто не отображаем клавиатуру.
         */
        if (getDialog().getWindow() != null) getDialog().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return editDialogView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Нажатие на кнопку подтверждения введенных данных.
     */
    @OnClick(R.id.btn_ok)
    public void onConfirmClick() {
        boolean success = (mIsNewTheme) ? createTheme() : editTheme();
        if (success) dismiss();
    }
    /**
     * Нажатие на кнопку отмены введенных данных.
     */
    @OnClick(R.id.btn_cancel)
    public void onCancelClick() {
        dismiss();
    }

    /**
     * Нажатие на кнопку удаления редактируемого элемента.
     */
    @OnClick(R.id.btn_delete)
    public void onDeleteClick() {
        mListener.deleteTheme();
        dismiss();
    }

    private boolean editTheme()
    {
        boolean success = mListener.editTheme(mThemeNameEditText.getText().toString(),
                        mContainsTestCheckBox.isChecked());
        // Если не удалось переименовать тему.
        if (!success) {
            mThemeNameEditText.setError(
                    getResources().getString(R.string.msg_fail_edit_exam_theme));
        }

        return success;
    }

    private boolean createTheme()
    {
        boolean success = mListener.addNewExamTheme(mThemeNameEditText.getText().toString(),
                        mContainsTestCheckBox.isChecked());
        // Если не удалось создать тему.
        if (!success) {
            mThemeNameEditText.setError(
                    getResources().getString(R.string.msg_fail_add_new_exam_theme));
        }

        return success;
    }
}
