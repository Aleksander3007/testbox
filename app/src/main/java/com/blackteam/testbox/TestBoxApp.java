package com.blackteam.testbox;

import android.app.Application;

import com.blackteam.testbox.utils.NavigationTree;

/**
 * Главный класс приложения. Здесь хранятся общие для приложения настройки.
 */
public class TestBoxApp extends Application {

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

    private NavigationTree<String> mExamTree;

    public UserType getUserType() {
        return mUserType;
    }

    public void setsUserType(UserType userType) {
        this.mUserType = userType;
    }

    public NavigationTree<String> getExamTree() {
        return mExamTree;
    }

    public void setExamTree(NavigationTree<String> examTree) {
        this.mExamTree = examTree;
    }
}
