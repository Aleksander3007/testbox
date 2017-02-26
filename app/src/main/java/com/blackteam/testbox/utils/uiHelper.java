package com.blackteam.testbox.utils;

import android.graphics.Color;
import android.widget.EditText;
import android.widget.ImageButton;

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

    /**
     * Disable an imageButton.
     */
    public static void disableImageButton(ImageButton imageButton) {
        imageButton.setEnabled(false);
        imageButton.setAlpha(0.3f);
    }

    /**
     * Enable an imageButton.
     */
    public static void enableImageButton(ImageButton imageButton) {
        imageButton.setEnabled(true);
        imageButton.setAlpha(1.0f);
    }
}
