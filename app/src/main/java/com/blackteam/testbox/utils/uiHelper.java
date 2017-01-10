package com.blackteam.testbox.utils;

import android.graphics.Color;
import android.widget.EditText;

/**
 * Абстрактный класс, содержащий вспомогательные функции для пользовательского интерфейса.
 */
public abstract class UIHelper {
    /**
     * Запретить редактирование в EditText.
     * @param editText EditText, в котором необходимо запретить редактирование.
     */
    public static void disableEditText(EditText editText) {
        editText.setFocusableInTouchMode(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
    }

    /**
     * Разрешить редактировие в EditText.
     * @param editText EditText, в котором необходимо разрешить редактирование.
     */
    public static void enableEditText(EditText editText) {
        editText.setFocusableInTouchMode(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
    }
}
