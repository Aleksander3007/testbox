package com.blackteam.testbox.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.blackteam.testbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Диалоговое окно для создания вопроса.
 */
public class CreatingAnswerDialogFragment extends DialogFragment {

    @BindView(R.id.et_newAnswer) EditText newAnswerEditText;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View creatingAnswerDialogView
                = inflater.inflate(R.layout.fragment_creating_answer_dialog, container, false);
        unbinder = ButterKnife.bind(this, creatingAnswerDialogView);
        return creatingAnswerDialogView;
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
        ((EditableQuestionActivity)getActivity())
                .addNewAnswer(newAnswerEditText.getText().toString());
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
