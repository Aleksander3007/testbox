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
 * Диалоговое окно для редактирования вопроса.
 */
public class EditAnswerDialogFragment extends DialogFragment {

    public static final String ARG_ANSWER_TEXT = "ARG_ANSWER_TEXT";
    public static final String ARG_IS_RIGHT_ANSWER = "ARG_IS_RIGHT_ANSWER";
    public static final String ARG_IS_NEW_ANSWER = "ARG_IS_NEW_ANSWER";

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

        EditAnswerDialogFragment dialog = new EditAnswerDialogFragment();

        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_NEW_ANSWER, true);
        dialog.setArguments(args);

        return dialog;
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

        if (mIsNewAnswer)
            createAnswer();
        else
            editAnswer();

        dismiss();
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
        ((EditQuestionActivity)getActivity()).deleteAnswer();
        dismiss();
    }

    private void editAnswer()
    {
        ((EditQuestionActivity)getActivity())
                .editAnswer(mAnswerEditText.getText().toString(),
                        mIsRightAnswerCheckBox.isChecked());
    }

    private void createAnswer()
    {
        ((EditQuestionActivity)getActivity())
                .addNewAnswer(mAnswerEditText.getText().toString(),
                        mIsRightAnswerCheckBox.isChecked());
    }
}
