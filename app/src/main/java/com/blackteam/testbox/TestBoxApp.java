package com.blackteam.testbox;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.blackteam.testbox.utils.ExceptionHandler;
import com.blackteam.testbox.utils.NavigationTree;
import com.blackteam.testbox.utils.XmlLoaderExternal;
import com.blackteam.testbox.utils.XmlLoaderInternal;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Главный класс приложения. Здесь хранятся общие для приложения настройки.
 */
public class TestBoxApp extends Application {

    public static final String DEFAULT_EXTERNAL_DIR = "testbox";

    /**
     * Режимы пользователя, которые имеют те или иные возможности в приложении.
     */
    public enum UserType {
        /** Простой пользователь, экзаменуемый. */
        USER,
        /** Редактор, тот кто может изменять состав доступных тестов. */
        EDITOR
    }
    private UserType mUserType = UserType.USER;

    private Exam mExam = new Exam();

    public UserType getUserType() {
        return mUserType;
    }

    public void setsUserType(UserType userType) {
        this.mUserType = userType;
    }

    public NavigationTree<ExamThemeData> getExamTree() {
        return mExam.getData();
    }

    public void setExamTree(NavigationTree<ExamThemeData> examTree) { mExam = new Exam(examTree); }

    public boolean loadExam() {
        try {
            new XmlLoaderInternal().load(getApplicationContext(), mExam.getFileName(), mExam);
            return true;

        }
        // Приложение заупущено впервые или еще ни разу не сохранялись данные.
        catch (FileNotFoundException fnfex) {
            mExam.init();
            return true;
        }
        catch (IOException ioex) {
            Toast.makeText(this, R.string.msg_fail_loading_exam_themes, Toast.LENGTH_LONG).show();
            Log.e("TestBoxApp", ioex.getMessage());
            ioex.printStackTrace();
            return false;
        } catch (XmlPullParserException xppex) {
            Toast.makeText(this, R.string.msg_fail_loading_exam_themes, Toast.LENGTH_LONG).show();
            Log.e("TestBoxApp", xppex.getMessage());
            xppex.printStackTrace();
            return false;
        }
    }

    public boolean saveExam() {
        if (mExam.getData() == null) mExam.init();
        try {
            new XmlLoaderInternal().save(getApplicationContext(), mExam.getFileName(), mExam);
            return true;
        } catch (IOException ioex) {
            Toast.makeText(this, R.string.msg_fail_saving, Toast.LENGTH_LONG).show();
            ioex.printStackTrace();
            return false;
        }
    }

    public boolean recoveryExam() {
        try {
            boolean isFileLoaded = new XmlLoaderExternal(DEFAULT_EXTERNAL_DIR)
                    .load(getApplicationContext(), mExam.getFileName(), mExam);
            if (isFileLoaded) saveExam();
            return isFileLoaded;
        }
        // Если файл не найден, то значит никогда не делалось резервной копии.
        catch (FileNotFoundException fnfex) {
            Toast.makeText(getApplicationContext(),
                    R.string.msg_recovery_file_is_not_existed, Toast.LENGTH_SHORT).show();
            return false;
        }
        catch (IOException ioex) {
            ioex.printStackTrace();
            return false;
        } catch (XmlPullParserException xppex) {
            xppex.printStackTrace();
            return false;
        }
    }

    public boolean backupExam() {
        try {
            boolean isFileSaved = new XmlLoaderExternal(DEFAULT_EXTERNAL_DIR)
                    .save(getApplicationContext(), mExam.getFileName(), mExam);
            return isFileSaved;
        } catch (IOException ioex) {
            ioex.printStackTrace();
            return false;
        }
    }

    @Override
    public void onCreate() {
        // Обработка неотловленных исключений.
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        super.onCreate();
    }
}
