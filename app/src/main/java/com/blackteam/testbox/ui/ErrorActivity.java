package com.blackteam.testbox.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.blackteam.testbox.R;

/**
 * Для вывода исключений (ошибок приложения).
 */
public class ErrorActivity extends Activity {

    public static final String EXTRA_ERROR_DATA = "com.testbox.extras.EXTRA_ERROR_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.error_layout);
        String error_text = getIntent().getExtras().getString(EXTRA_ERROR_DATA);
        ((TextView) findViewById(R.id.tv_exception_text)).setText(error_text);
    }
}
