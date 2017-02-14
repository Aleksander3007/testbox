package com.blackteam.testbox;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.blackteam.testbox.utils.ExamLoader;
import com.blackteam.testbox.utils.ExceptionHandler;
import com.blackteam.testbox.utils.NavigationTree;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Главный класс приложения. Здесь хранятся общие для приложения настройки.
 */
public class TestBoxApp extends Application {

    // Параметры для root элемента дерева, содержащего экзамен.
    private static final String sExamRootStr = "Экзамен";
    private static final String sExamRootId = "0";
    private static final boolean sIsExamRootTest = false;

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

    private NavigationTree<ExamThemeData> mExamTree;

    public UserType getUserType() {
        return mUserType;
    }

    public void setsUserType(UserType userType) {
        this.mUserType = userType;
    }

    public NavigationTree<ExamThemeData> getExamTree() {
        return mExamTree;
    }

    public void setExamTree(NavigationTree<ExamThemeData> examTree) {
        mExamTree = examTree;
    }

    public boolean loadExamTree() {
        try {
            NavigationTree<ExamThemeData> examTree =
                    ExamLoader.loadExam(getApplicationContext());

            if (examTree != null)
                this.mExamTree = examTree;
                // В случае, если приложение запускается впервые.
            else
                clearExamTree();

            return true;

        } catch (IOException ioex) {
            Toast.makeText(this, R.string.msg_error_loading_exam_themes, Toast.LENGTH_LONG).show();
            Log.e("TestBoxApp", ioex.getMessage());
            ioex.printStackTrace();
            return false;
        } catch (XmlPullParserException xppex) {
            Toast.makeText(this, R.string.msg_error_loading_exam_themes, Toast.LENGTH_LONG).show();
            Log.e("TestBoxApp", xppex.getMessage());
            xppex.printStackTrace();
            return false;
        }
    }

    public boolean saveExamTree() {
        if (mExamTree == null) clearExamTree();
        try {
            ExamLoader.saveExam(getApplicationContext(), mExamTree);
            return true;
        } catch (IOException ioex) {
            Toast.makeText(this, R.string.msg_error_saving, Toast.LENGTH_LONG).show();
            ioex.printStackTrace();
            return false;
        }
    }

    /**
     * Очищаем экзам. дерево до первоначального состония.
     */
    private void clearExamTree() {
        mExamTree = new NavigationTree<>();
        mExamTree.createRootElement(
                new ExamThemeData(sExamRootStr, sExamRootId, sIsExamRootTest));
    }

    @Override
    public void onCreate() {
        // Обработка неотловленных исключений.
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        super.onCreate();
    }
}
