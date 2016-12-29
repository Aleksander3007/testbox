package com.blackteam.testbox.utils;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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
     * @param name данные элемента.
     */
    public NavigationTree(E name) {
        createRootElement(name);
    }

    /**
     * Создать корневой элемент.
     * @param name Имя корневого элемента.
     */
    public void createRootElement(E name) {
        super.createRootElement(name);
        mBreadCrumbs.push(mRoot);
    }

    /**
     * Сделать следующий узел текущим.
     * @param name Имя узла (на следующем уровне).
     * @return следующий узел, null - если  дерево пустое.
     * @throws IllegalArgumentException - указанный узел не существует (возможно текущий узел - конечный).
     */
    public WideTree.Node<E> next(E name) throws IllegalArgumentException  {
        if (mRoot == null)
            return null;

        Node<E> currentNode = mBreadCrumbs.peek();
        Node<E> nextNode = mBreadCrumbs.peek().getChild(name);
        if (nextNode == null)
            throw new IllegalArgumentException("Node '" + name.toString() +  "' doesn't exist.");

        mBreadCrumbs.push(nextNode);

        return nextNode;
    }

    /**
     * Сделать предыдущий узел текущим.
     * @return узел, null - если текущий узел root или дерево пустое.
     */
    public WideTree.Node<E> prev() {
        if ((mRoot == null) || (mBreadCrumbs.size() == 1))
            return null;

        mBreadCrumbs.pop();
        WideTree.Node<E> prevNode = mBreadCrumbs.peek();

        mBreadCrumbs.add(prevNode);

        return prevNode;
    }

    /**
     * Получить текущий узел.
     * @return текущий узел.
     */
    public WideTree.Node<E> getCurElem() {
        return mBreadCrumbs.peek();
    }
}
