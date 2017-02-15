package com.blackteam.testbox.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.blackteam.testbox.ui.EditQuestionActivity;
import com.blackteam.testbox.ui.ErrorActivity;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = EditQuestionActivity.class.getSimpleName();

    private static final String LINE_SEPARATOR = "\n";

    private final Context mContext;
    private Thread.UncaughtExceptionHandler mOldHandler;

    public ExceptionHandler(Context context) {
        mContext = context;
        // сохраним ранее установленный обработчик
        mOldHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();

        errorReport.append("CAUSE OF ERROR:\n\n");
        errorReport.append(stackTrace.toString());

        errorReport.append("\nDEVICE INFORMATION:\n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Id: ");
        errorReport.append(Build.ID);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: ");
        errorReport.append(Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);

        errorReport.append("\nFIRMWARE:\n");
        errorReport.append("SDK: ");
        errorReport.append(Build.VERSION.SDK_INT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: ");
        errorReport.append(Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Incremental: ");
        errorReport.append(Build.VERSION.INCREMENTAL);
        errorReport.append(LINE_SEPARATOR);

        Intent intent = new Intent(mContext, ErrorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(ErrorActivity.EXTRA_ERROR_DATA, errorReport.toString());
        mContext.startActivity(intent);

        Log.e(TAG, errorReport.toString());

        if (mOldHandler != null) // если есть ранее установленный...
            mOldHandler.uncaughtException(thread, exception); // ...вызовем его
    }

}
