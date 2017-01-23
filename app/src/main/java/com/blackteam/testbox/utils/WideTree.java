package com.blackteam.testbox.utils;

import java.io.Serializable;
import java.util.ArrayList;
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
     * @param data данные корневого элемента.
     */
    public WideTree(E data) {
        createRootElement(data);
    }

    public WideTree.Node<E> getRootElement() {
        return mRoot;
    }

    /**
     * Создать корневой элемент.
     * @param data данные корневого элемента.
     */
    public void createRootElement(E data) {
        mRoot = new Node<>(data);
    }

    /**
     * Узел навигационного дерева. Имена узлов на одном уровне должны быть разными.
     */
    public static class Node<E> implements Serializable {
        private E mData;
        private List<Node<E>> mChildren = new ArrayList<>();

        /**
         * Создание навигационнго узла без детей.
         * @param data данные узла.
         */
        public Node(E data) {
            mData = data;
        }

        public E getData() { return mData; }

        public void setData(E data) { mData = data; }

        /**
         * Добавить подузел.
         * @param data данные узла.
         */
        public Node<E> addChild(E data) {
            Node<E> childNode = new Node(data);
            mChildren.add(childNode);
            return childNode;
        }

        /**
         * Получить узел по имени.
         * @param data данные требуемого узла.
         * @return требуемый узел, либо null - если не найден.
         */
        public Node<E> getChild(E data) {
            for (Node<E> child : mChildren) {
                if (child.getData().equals(data)) {
                    return child;
                }
            }
            return null;
        }

        /**
         * Добавить подузлы.
         * @param childrenData данные подузлов.
         */
        public void addChildren(List<E> childrenData) {
            for (E childData : childrenData) {
                mChildren.add(new Node<E>(childData));
            }
        }

        /**
         * Получить подузлы.
         * @return подузлы.
         */
        public List<Node<E>> getChildren() {
            return mChildren;
        }

        /**
         * Существуют ли подузлы данного узла?
         * @return true - если существуют.
         */
        public boolean hasChildren() {
            return mChildren.size() > 0;
        }

        /**
         * Содержит ли узел подузел с запрашиваемыми данными?
         * @param data данные подузла.
         * @return true - если содержит.
         */
        public boolean containsChild(E data) {
            return (getChild(data) != null);
        }

        /**
         * Удалить узел и все его подузлы.
         * @param data данные удаляемого подузла.
         */
        public void removeChild(E data) {
            removeChild(getChild(data));
        }

        /**
         * Удалить узел и все его подузлы.
         * @param deletingChild удаляемый подузел.
         */
        public void removeChild(Node<E> deletingChild) {
            removeChildren(deletingChild);
            mChildren.remove(deletingChild);
        }

        /**
         * Удалить подузлы указанного узла.
         * @param rootChild узёл, подузлы которого необходимо удалить.
         */
        public void removeChildren(Node<E> rootChild) {
            for (Node<E> child : rootChild.getChildren()) {
                child.removeChildren(child);
            }
            rootChild.getChildren().clear();
        }

        public List<E> getChildrenData() {
            List<E> childrenData = new ArrayList<>(mChildren.size());
            for (Node<E> child : mChildren) {
                childrenData.add(child.getData());
            }
            return childrenData;
        }

        @Override
        public String toString() {
            return mData.toString();
        }
    }
}
