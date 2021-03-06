package com.blackteam.testbox.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.ExamThemeData;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.utils.NavigationTree;
import com.blackteam.testbox.utils.XmlLoaderExternal;
import com.blackteam.testbox.utils.XmlLoaderInternal;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Окно настроек приложения.
 */
public class SettingsActivity extends Activity {

    public static final int PERMISSION_REQUEST_WRITE_FILE = 0;
    public static final int PERMISSION_REQUEST_READ_FILE = 1;

    private final XmlLoaderInternal mXmlLoaderInternal = new XmlLoaderInternal();
    private final XmlLoaderExternal mXmlLoaderExternal = new XmlLoaderExternal(TestBoxApp.DEFAULT_EXTERNAL_DIR);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
    }

    /**
     * Нажатие на кнопку резервного копирования.
     */
    @OnClick(R.id.btn_backup)
    public void onBackupClick() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    SettingsActivity.PERMISSION_REQUEST_WRITE_FILE);
        }
        else {
            backup();
        }
    }

    /**
     * Нажатие на кнопку восстановления.
     */
    @OnClick(R.id.btn_recovery)
    public void onRecoveryClick() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    SettingsActivity.PERMISSION_REQUEST_READ_FILE);
        }
        else {
            recovery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_FILE:
                // Если запрос был отменен, то grantResults будет пустым.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    backup();

                } else {
                    Toast.makeText(this,
                            R.string.msg_fail_permission_backup, Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_READ_FILE:
                // Если запрос был отменен, то grantResults будет пустым.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    recovery();

                } else {
                    Toast.makeText(this,
                            R.string.msg_fail_permission_recovery, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Резервное копирование.
     */
    private void backup() {
        try {
            boolean success = ((TestBoxApp)getApplicationContext()).backupExam();
            if (success) success = backupTests(((TestBoxApp)getApplicationContext())
                    .getExamTree().getRootElement());
            if (success) {
                Toast.makeText(this, R.string.msg_success_backup, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, R.string.msg_backup_sd_is_not_found, Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException | XmlPullParserException ex) {
            Toast.makeText(this, R.string.msg_fail_backup, Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    /**
     * Востановление из резервой копии.
     */
    private void recovery() {
        AlertDialog.Builder confirmRecoveryDialog = new AlertDialog.Builder(this);
        confirmRecoveryDialog.setTitle(R.string.title_recovery)
                .setMessage(R.string.msg_recovery_data_will_remove)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            boolean success = ((TestBoxApp)getApplicationContext()).recoveryExam();
                            if (success) success = recoveryTests(((TestBoxApp)getApplicationContext()).
                                    getExamTree().getRootElement());
                            if (success)
                                Toast.makeText(getApplicationContext(),
                                    R.string.msg_success_recovery, Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(),
                                        R.string.msg_backup_sd_is_not_found, Toast.LENGTH_SHORT).show();
                        }
                        // Если файл не найден, то значит никогда не делалось резервной копии.
                        catch (FileNotFoundException fnfex) {
                            ((TestBoxApp)getApplicationContext()).setExamTree(null);
                            ((TestBoxApp)getApplicationContext()).saveExam();
                            Toast.makeText(getApplicationContext(),
                                    R.string.msg_recovery_file_is_not_existed, Toast.LENGTH_SHORT).show();
                        }
                        catch (IOException | XmlPullParserException ioex) {
                            Toast.makeText(getApplicationContext(),
                                    R.string.msg_fail_recovery, Toast.LENGTH_SHORT).show();
                            ioex.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * Резервное копирование всех тестов. (Рекурсия!)
     * @param parentExamTheme главная тема, с которой необходимо начать сохранения тестов.
     * @return true - если резервное копирование тестов успешно завершено.
     * @throws IOException
     */
    private boolean backupTests(NavigationTree.Node<ExamThemeData> parentExamTheme)
            throws IOException, XmlPullParserException {
        boolean success = true;
        for (NavigationTree.Node<ExamThemeData> examTheme : parentExamTheme.getChildren()) {

            success = backupTests(examTheme);
            if (!success) return false;

            if (examTheme.getData().containsTest()) {
                ExamTest examTest = new ExamTest(examTheme.getData().getId());
                try {
                    success = mXmlLoaderInternal.load(this, examTest.getFileName(), examTest);
                    if (!success) return false;
                    success =  mXmlLoaderExternal.save(this, examTest.getFileName(), examTest);
                    if (!success) return false;
                }
                catch (FileNotFoundException fnfex) {
                    /** Значит файл с данными для теста еще не был создан,
                     * поэтому просто не делаем его backup (игнорируем данное исключение). */
                }
            }
        }
        return success;
    }

    /**
     * Восстановление всех тестов из резервных копий. (Рекурсия!)
     * @param parentExamTheme главная тема, с которой необходимо начать загрузку тестов.
     * @return true - если успешно восстановлены тесты.
     * @throws IOException
     */
    private boolean recoveryTests(NavigationTree.Node<ExamThemeData> parentExamTheme)
            throws IOException, XmlPullParserException {
        boolean success = true;
        for (NavigationTree.Node<ExamThemeData> examTheme : parentExamTheme.getChildren()) {

            success = recoveryTests(examTheme);
            if (!success) return false;

            if (examTheme.getData().containsTest()) {
                ExamTest examTest = new ExamTest(examTheme.getData().getId());
                try {
                    success = mXmlLoaderExternal.load(this, examTest.getFileName(), examTest);
                    if (!success) return false;
                    mXmlLoaderInternal.save(this, examTest.getFileName(), examTest);
                    if (!success) return false;
                }
                catch (FileNotFoundException fnfex) {
                    /** Значит файл с данными для теста не существовал во время последнего backup,
                     * поэтому просто не делаем его востановление (игнорируем данное исключение). */
                }
            }
        }
        return success;
    }
}
