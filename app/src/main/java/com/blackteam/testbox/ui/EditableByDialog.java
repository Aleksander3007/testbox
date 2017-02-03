package com.blackteam.testbox.ui;

/**
 * Класс, который реализует данный интерфейс, содержит элементы для редактирования через
 * диалоговое окно редактирования.
 */
public interface EditableByDialog {
    void editElement(String mainText, boolean isChecked);
    void deleteElement();
}
