package com.blackteam.testbox;

/**
 * Абстрактный класс для хранения общих настроек приложения.
 */
public abstract class AppState {

    /**
     * Режимы пользователя, которые имеют те или иные возможности в приложении.
     */
    public enum UserType {
        /** Простой пользователь, экзаменуемый. */
        USER,
        /** Редактор, тот кто может изменять состав доступных тестов. */
        EDITOR
    }
    public static UserType sUserType = UserType.USER;
}
