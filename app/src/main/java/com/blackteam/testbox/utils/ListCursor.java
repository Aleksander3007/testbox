package com.blackteam.testbox.utils;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Курсор позволяющий делать проход по списку в прямом и обратном направлении.
 * А также предоставляет доступ к текущему элементу.
 */

public class ListCursor<E> {

    private List<E> mList;
    /** Текущая позиция в списке. */
    private int mCursor;

    public ListCursor(List<E> list) {
        mList = list;
        mCursor = 0;
    }

    /**
     * Получить текущий элемент.
     * @return текущий элемент.
     */
    public E getCurrent() {
        return mList.get(mCursor);
    }

    /**
     * Переход на следующий элемент.
     * @return следующий элемент.
     */
    public E next() {
        if (!hasNext())
            throw new NoSuchElementException("Next element is not existed.");

        return mList.get(++mCursor);
    }

    /**
     * Переход на предыдущий элемент.
     * @return предыдущий элемент.
     */
    public E previous() {
        if (!hasPrevious())
            throw new NoSuchElementException("Previous element is not existed.");

        return mList.get(--mCursor);
    }

    /**
     * Существует ли следующий элемент.
     * @return true - если существует.
     */
    public boolean hasNext() {
        return (!isEmpty() && mCursor < mList.size() - 1);
    }

    /**
     * Существует ли предыдущий элемент.
     * @return true - если существует.
     */
    public boolean hasPrevious() {
        return (!isEmpty() && mCursor > 0);
    }

    /**
     * Пустой ли список.
     * @return true - если пустой.
     */
    public boolean isEmpty() {
        return mList.isEmpty();
    }

    /**
     * Добавить элемент в список.
     * @param element элемент, который необходимо добавить.
     */
    public void add(E element) {
        if (!isEmpty()) {
            mList.add(mCursor + 1, element);
        }
        else
            mList.add(0, element);
    }

    /**
     * Установить значение для текущего элемента.
     * @param element значение которое необходимо установить.
     */
    public void set(E element) {
        mList.set(mCursor, element);
    }
}
