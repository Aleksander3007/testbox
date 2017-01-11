package com.blackteam.testbox.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.blackteam.testbox.R;

/**
 * Диалоговое окно для создания вопроса.
 */
public class CreatingAnswerDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View creatingAnswerDialogView
                = inflater.inflate(R.layout.fragment_creating_answer_dialog, container, false);

        Button btnOk = (Button) creatingAnswerDialogView.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newAnswerEditText =
                        (EditText) creatingAnswerDialogView.findViewById(R.id.et_newAnswer);
                ((ExamTestQuestionActivity)getActivity())
                        .addNewAnswer(newAnswerEditText.getText().toString());
                dismiss();
            }
        });

        Button btnCancel = (Button) creatingAnswerDialogView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return creatingAnswerDialogView;
    }
}
