package com.blackteam.testbox.utils;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Дерево навигации. Позволяет выстраивать иерархичное дерево с одним родителем,
 * и неограниченным количеством детей. На основе массива.
 * Имена узлов на одном уровне должны быть разными.
 */
public class NavigationTree<E> extends WideTree<E> implements Serializable {

    /**
     * Цепочка узлов от текущего элемента до корня.
     */
    Deque<WideTree.Node<E>> mBreadCrumbs = new ArrayDeque<>();

    /**
     * Создать пустое дерево.
     */
    public NavigationTree() {
        super();
    }

    /**
     * Создать дерево с одним элементом.
     * @param data данные элемента.
     */
    public NavigationTree(E data) {
        createRootElement(data);
    }

    /**
     * Создать корневой элемент.
     * @param data Имя корневого элемента.
     */
    public void createRootElement(E data) {
        super.createRootElement(data);
        mBreadCrumbs.push(mRoot);
    }

    /**
     * Сделать следующий узел текущим.
     * @param data Имя узла (на следующем уровне).
     * @return следующий узел, null - если  дерево пустое.
     * @throws IllegalArgumentException - указанный узел не существует (возможно текущий узел - конечный).
     */
    public WideTree.Node<E> next(E data) throws IllegalArgumentException  {
        if (mRoot == null)
            return null;

        Node<E> nextNode = mBreadCrumbs.peek().getChild(data);
        if (nextNode == null)
            throw new IllegalArgumentException("Node '" + data.toString() +  "' doesn't exist.");

        mBreadCrumbs.push(nextNode);

        return nextNode;
    }

    /**
     * Сделать предыдущий узел текущим.
     * @return предыдущий узел, null - если текущий узел root или дерево пустое.
     */
    public WideTree.Node<E> prev() {
        if ((mRoot == null) || (mBreadCrumbs.size() == 1))
            return null;

        mBreadCrumbs.pop();

        return mBreadCrumbs.peek();
    }

    /**
     * Получить текущий узел.
     * @return текущий узел.
     */
    public WideTree.Node<E> getCurElem() {
        return mBreadCrumbs.peek();
    }

    /**
     * Проход по указанному пути.
     * Если в пути будут содержаться неккоректные данные, то будет выдано исключение.
     * @return указанный путь.
     * @throws IllegalArgumentException - указанный узел не существует (возможно текущий узел - конечный).
     */
    public void setPath(Deque<E> path) throws IllegalArgumentException {
        mBreadCrumbs.clear();
        // В mBreadCrumbs должен всегда содержаться один элемент (root).
        mBreadCrumbs.push(mRoot);

        Iterator<E> dataIterator = path.descendingIterator();
        dataIterator.next(); // пропускаем root элемент, т.к. он уже добавлен.
        while (dataIterator.hasNext()) next(dataIterator.next());
    }

    /**
     * Получить путь от текущего до корневого узла.
     * @return путь.
     */
    public Deque<E> getPath() {
        Deque<E> path = new ArrayDeque<E>();
        for (WideTree.Node<E> node : mBreadCrumbs) {
            path.add(node.getData());
        }
        return path;
    }
}
