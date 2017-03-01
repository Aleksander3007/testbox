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
 * Диалоговое окно для редактирования вопроса.
 */
public class EditAnswerDialogFragment extends DialogFragment {

    public static final String ARG_ANSWER_TEXT = "ARG_ANSWER_TEXT";
    public static final String ARG_IS_RIGHT_ANSWER = "ARG_IS_RIGHT_ANSWER";
    public static final String ARG_IS_NEW_ANSWER = "ARG_IS_NEW_ANSWER";

    /**
     * Activity, которое создаст данное диалоговое окно должна реализовать
     * этот интерфейс (чтобы получать и обрабатывать callback-и диалогового окна).
     */
    public interface NoticeDialogListener {
        void addNewAnswer(String answer, boolean isRightAnswer);
        void editAnswer(String answerNewText, boolean isRight);
        void deleteAnswer();
    }
    // Для отправки callback-ов Activity.
    NoticeDialogListener mListener;

    @BindView(R.id.et_answerText) EditText mAnswerEditText;
    @BindView(R.id.cb_isRightAnswer) CheckBox mIsRightAnswerCheckBox;
    @BindView(R.id.btn_delete)
    Button mDeleteButton;
    private Unbinder unbinder;

    /** Диалог открыт для создания нового вопроса? */
    private boolean mIsNewAnswer;

    /**
     * Создание экземпляра класса {@link EditAnswerDialogFragment}.
     */
    public static EditAnswerDialogFragment newInstance() {

        EditAnswerDialogFragment dialogFragment = new EditAnswerDialogFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_NEW_ANSWER, true);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    /**
     * Создание экземпляра класса {@link EditThemeDialogFragment}.
     * @param answerText текст вопроса.
     * @param isRightAnswer является ли ответ правильным.
     * @param isNewAnswer является ли ответ новым.
     */
    public static EditAnswerDialogFragment newInstance(String answerText, boolean isRightAnswer,
                                                      boolean isNewAnswer) {

        EditAnswerDialogFragment dialog = new EditAnswerDialogFragment();

        Bundle args = new Bundle();
        args.putString(ARG_ANSWER_TEXT, answerText);
        args.putBoolean(ARG_IS_RIGHT_ANSWER, isRightAnswer);
        args.putBoolean(ARG_IS_NEW_ANSWER, isNewAnswer);
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
        final View editDialogView
                = inflater.inflate(R.layout.fragment_edit_answer_dialog, container, false);
        unbinder = ButterKnife.bind(this, editDialogView);

        String answerText = getArguments().getString(ARG_ANSWER_TEXT);
        boolean isRightAnswer = getArguments().getBoolean(ARG_IS_RIGHT_ANSWER);
        mIsNewAnswer = getArguments().getBoolean(ARG_IS_NEW_ANSWER);

        if (answerText != null) mAnswerEditText.setText(answerText);

        mIsRightAnswerCheckBox.setChecked(isRightAnswer);
        if (mIsNewAnswer) mDeleteButton.setVisibility(View.GONE);

        // Запрос фокуса и отображение soft keyboard принудительно.
        mAnswerEditText.requestFocus();
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

        if (mIsNewAnswer)
            createAnswer();
        else
            editAnswer();

        dismiss();
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
    public void onDeleteClcik() {
        mListener.deleteAnswer();
        dismiss();
    }

    private void editAnswer()
    {
        mListener.editAnswer(mAnswerEditText.getText().toString(),
                        mIsRightAnswerCheckBox.isChecked());
    }

    private void createAnswer()
    {
        mListener.addNewAnswer(mAnswerEditText.getText().toString(),
                        mIsRightAnswerCheckBox.isChecked());
    }
}
