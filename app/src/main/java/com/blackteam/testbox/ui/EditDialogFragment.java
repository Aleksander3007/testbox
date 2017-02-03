package com.blackteam.testbox.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.blackteam.testbox.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Диалоговое окно редактирования.
 * Activity, которое использует данное окно должен реализовать интерфейс {@link EditableByDialog}.
 */
public class EditDialogFragment extends DialogFragment {

    private static final String sMainTextArg = "MainText";
    private static final String sMainTextHintArg = "MainTextHint";
    private static final String sCheckItemTextArg = "CheckItemText";
    private static final String sIsCheckedArg = "IsChecked";

    @BindView(R.id.et_mainText) TextInputEditText mMainTextEditText;
    @BindView(R.id.til_mainText) TextInputLayout mMainTextInputLayout;
    @BindView(R.id.cb_checkItem) CheckBox mCheckItemCheckBox;
    private Unbinder unbinder;

    /**
     * Создание экземпляра {@link EditDialogFragment}.
     * @param mainText текст, который необходимо вывести в поле редактирования.
     * @param mainTextHint подсказка, выводимая в поле для главного текста.
     * @param checkItemText текст для checkBox.
     * @param isChecked состояние checkBox.
     */
    public static EditDialogFragment newInstance(String mainText, String mainTextHint,
                                                 String checkItemText, boolean isChecked) {

        EditDialogFragment editDialogFragment = new EditDialogFragment();

        Bundle args = new Bundle();
        args.putString(sMainTextArg, mainText);
        args.putString(sMainTextHintArg, mainTextHint);
        args.putString(sCheckItemTextArg, checkItemText);
        args.putBoolean(sIsCheckedArg, isChecked);
        editDialogFragment.setArguments(args);

        return editDialogFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View editDialogView =
                inflater.inflate(R.layout.fragment_edit_dialog, container, false);
        unbinder = ButterKnife.bind(this, editDialogView);

        mMainTextEditText.setText(getArguments().getString(sMainTextArg));
        mMainTextInputLayout.setHint(getArguments().getString(sMainTextHintArg));
        mCheckItemCheckBox.setText(getArguments().getString(sCheckItemTextArg));
        mCheckItemCheckBox.setChecked(getArguments().getBoolean(sIsCheckedArg));

        return editDialogView;
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
        ((EditableByDialog)getActivity())
                .editElement(mMainTextEditText.getText().toString(),
                        mCheckItemCheckBox.isChecked());
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
     * Нажатие на кнопку удаления редактируемого элемента.
     * @param view
     */
    @OnClick(R.id.btn_delete)
    public void deleteOnClcik(View view) {
        ((EditableByDialog)getActivity()).deleteElement();
        dismiss();
    }
}
