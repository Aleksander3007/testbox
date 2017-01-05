package com.blackteam.testbox.utils;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Представляет иерархичное дерево с одним родителем и неограниченном количеством детей.
 * На основе массива.
 * Имена узлов на одном уровне должны быть разными.
 */
public class WideTree<E>  implements Serializable {
    /**
     * Корневой элемент.
     */
    protected Node<E> mRoot;

    /**
     * Создать пустое дерево.
     */
    public WideTree() {
        mRoot = null;
    }

    /**
     * Создать дерево с одним элементом.
     * @param name данные элемента.
     */
    public WideTree(E name) {
        createRootElement(name);
    }

    public WideTree.Node<E> getRootElement() {
        return mRoot;
    }

    /**
     * Создать корневой элемент.
     * @param name Имя корневого элемента.
     */
    public void createRootElement(E name) {
        mRoot = new Node<>(name);
    }

    /**
     * Узел навигационного дерева. Имена узлов на одном уровне должны быть разными.
     */
    public static class Node<E> implements Serializable {
        private E mName;
        private List<Node<E>> mChildren = new ArrayList<>();

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
        public List<Node<E>> getChildren() {
            return mChildren;
        }

        public List<E> getChildrenNames() {
            List<E> childrenNames = new ArrayList<>(mChildren.size());
            for (Node<E> child : mChildren) {
                childrenNames.add(child.getName());
            }
            return childrenNames;
        }

        @Override
        public String toString() {
            return mName.toString();
        }
    }
}
