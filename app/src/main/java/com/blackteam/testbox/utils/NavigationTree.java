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
 * и неограниченным количеством детей. На основе ArrayList.
 * Имена узлов на одном уровне должны быть разными.
 */
public class NavigationTree<E> implements Serializable {

    /**
     * Корневой элемент.
     */
    Node<E> mRoot;

    /**
     * Цепочка узлов от текущего элемента до корня.
     */
    Deque<Node<E>> mBreadCrumbs = new ArrayDeque<>();

    /**
     * Создать пустое дерево.
     */
    public NavigationTree() {
        mRoot = null;
    }

    /**
     * Создать дерево с одним элементом.
     * @param name данные элемента.
     */
    public NavigationTree(E name) {
        createRootElement(name);
    }

    public NavigationTree.Node<E> getRootElement() {
        return mRoot;
    }

    /**
     * Создать корневой элемент.
     * @param name Имя корневого элемента.
     */
    public void createRootElement(E name) {
        mRoot = new Node<>(name);
        mBreadCrumbs.push(mRoot);
    }

    /**
     * Сделать следующий узел текущим.
     * @param name Имя узла (на следующем уровне).
     * @return следующий узел, null - если  дерево пустое.
     * @throws IllegalArgumentException - указанный узел не существует (возможно текущий узел - конечный).
     */
    public NavigationTree.Node<E> next(E name) throws IllegalArgumentException  {
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
    public NavigationTree.Node<E> prev() {
        if ((mRoot == null) || (mBreadCrumbs.size() == 1))
            return null;

        mBreadCrumbs.pop();
        Node<E> prevNode = mBreadCrumbs.peek();

        mBreadCrumbs.add(prevNode);

        return prevNode;
    }

    /**
     * Получить текущий узел.
     * @return текущий узел.
     */
    public NavigationTree.Node<E> getCurElem() {
        return mBreadCrumbs.peek();
    }

    /**
     * Узел навигационного дерева. Имена узлов на одном уровне должны быть разными.
     */
    public static class Node<E> implements Serializable {
        private E mName;
        private List<Node> mChildren = new ArrayList<>();

        /**
         * Создание навигационнго узла без детей.
         * @param name Имя узла.
         */
        public Node(E name) {
            mName = name;
        }

        public E getName() { return mName; }

        /**
         * Добавить подузел.
         * @param name имя узла.
         */
        public Node<E> addChild(E name) {
            Node<E> childNode = new Node(name);
            mChildren.add(childNode);
            return childNode;
        }

        /**
         * Получить узел по имени.
         * @param name имя требуемого узла.
         * @return требуемый узел, либо null - если не найден.
         */
        public Node<E> getChild(E name) {
            for (Node<E> child : mChildren) {
                if (child.getName().equals(name)) {
                    return child;
                }
            }
            return null;
        }

        /**
         * Добавить подузлы.
         * @param childrenName имена подузлов.
         */
        public void addChildren(List<E> childrenName) {
            for (E childName : childrenName) {
                mChildren.add(new Node<E>(childName));
            }
        }

        /**
         * Получить подузлы.
         * @return подузлы.
         */
        public List<Node> getChildren() {
            return mChildren;
        }

        public List<E> getChildrenNames() {
            List<E> childrenNames = new ArrayList<>(mChildren.size());
            for (Node<E> child : mChildren) {
                childrenNames.add(child.getName());
            }
            return childrenNames;
        }
    }
}
