package com.blackteam.testbox.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.ExamThemeData;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestBoxApp;
import com.blackteam.testbox.utils.ExamLoader;
import com.blackteam.testbox.utils.NavigationTree;

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
                            R.string.msg_fail_permission_backup, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Резервное копирование.
     */
    private void backup() {
        try {
            NavigationTree<ExamThemeData> examTree = ((TestBoxApp)getApplicationContext()).getExamTree();
            boolean success = ExamLoader.saveToSdCard(examTree);
            if (success) success = backupTests(examTree.getRootElement());
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
                .setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            NavigationTree<ExamThemeData> examTree = ExamLoader.loadFromSdCard();
                            ((TestBoxApp)getApplicationContext()).setExamTree(examTree);
                            ((TestBoxApp)getApplicationContext()).saveExamTree();
                            recoveryTests(examTree.getRootElement());
                            Toast.makeText(getApplicationContext(),
                                    R.string.msg_success_recovery, Toast.LENGTH_SHORT).show();
                        }
                        // Если файл не найден, то значит никогда не делалось резервной копии.
                        catch (FileNotFoundException fnfex) {
                            ((TestBoxApp)getApplicationContext()).setExamTree(null);
                            ((TestBoxApp)getApplicationContext()).saveExamTree();
                            Toast.makeText(getApplicationContext(),
                                    R.string.msg_recovery_file_is_not_existed, Toast.LENGTH_SHORT).show();
                        }
                        catch (IOException | XmlPullParserException ioex) {
                            Toast.makeText(getApplicationContext(),
                                    R.string.msg_fail_recovery, Toast.LENGTH_SHORT).show();
                            ioex.printStackTrace();
                        }
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    /**
     * Резервное копирование всех тестов. (Рекурсия!)
     * @param parentExamTheme главная тема, с которой необходимо начать сохранения тестов.
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
                examTest.load(this); // загружаем данные из локального файла.
                success = examTest.saveToSdCard();
                if (!success) return false;
            }
        }
        return success;
    }

    /**
     * Восстановление всех тестов из резервных копий. (Рекурсия!)
     * @param parentExamTheme главная тема, с которой необходимо начать загрузку тестов.
     * @return
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
                success = examTest.loadFromSdCard();
                if (success) {
                    examTest.save(this);
                }
                return success;
            }
        }
        return success;
    }
}
