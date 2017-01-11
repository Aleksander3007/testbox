package com.blackteam.testbox.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blackteam.testbox.R;

/**
 * Диалоговое окно для создания экзамеционной темы.
 */
public class CreatingThemeDialogFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View creatingThemeDialogView =
                inflater.inflate(R.layout.fragment_creating_theme_dialog, container, false);

        Button btnOk = (Button) creatingThemeDialogView.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newThemeNameEditText =
                        (EditText) creatingThemeDialogView.findViewById(R.id.et_newThemeName);
                ((ExamThemesActivity)getActivity()).
                        addNewExamTheme(newThemeNameEditText.getText().toString());
                dismiss();
            }
        });

        Button btnCancel = (Button) creatingThemeDialogView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return creatingThemeDialogView;
    }
}
