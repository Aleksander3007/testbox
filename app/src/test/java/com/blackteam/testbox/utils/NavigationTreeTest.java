package com.blackteam.testbox.utils;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Тестирование навигационного дерева и его узлов.
 */
public class NavigationTreeTest {
    @Test
    public void testNavigation() {
        NavigationTree<String> navigationTree = new NavigationTree<>("root");
        NavigationTree.Node<String> root = navigationTree.getRootElement();

        NavigationTree.Node<String> node1 = root.addChild("node1");
        root.addChild("node2");
        node1.addChild("node1.1");
        node1.addChild("node1.2");

        // Спускаемся на один уровень вниз.
        testNext(navigationTree, "node1");
        // Спускаемся еще на один уровень вниз.
        testNext(navigationTree, "node1.2");

        // Поднимаеся на уровень вверх.
        testPrev(navigationTree, "node1");
        // Поднимаеся еще на один уровень вверх.
        testPrev(navigationTree, "root");

        // Проверяем next() из конечного.
        navigationTree = new NavigationTree<>("root");
        try {
            NavigationTree.Node<String> nextNode = navigationTree.next("nodeNull");
            Assert.fail("Должно было вызваться исключение");
        }
        catch (IllegalArgumentException iaex) {
            Assert.assertEquals("root", navigationTree.getCurElem().getData());
        }
        // Проверяем prev() из root.
        NavigationTree.Node<String> prevNode = navigationTree.prev();
        Assert.assertNull(prevNode);
        Assert.assertEquals("root", navigationTree.getCurElem().getData());
    }

    public void testNext(NavigationTree<String> navigationTree, String nextNodeName) {
        NavigationTree.Node<String> currentNode =  navigationTree.next(nextNodeName);
        Assert.assertNotNull("Проверка навигации вперед (от root)", currentNode);
        Assert.assertEquals(nextNodeName, currentNode.getData());
        currentNode =  navigationTree.getCurElem();
        Assert.assertNotNull("Проверка получения текущего элемента после next()", currentNode);
        Assert.assertEquals(nextNodeName, currentNode.getData());
    }

    public void testPrev(NavigationTree<String> navigationTree, String prevNodeName) {
        NavigationTree.Node<String> currentNode =  navigationTree.prev();
        Assert.assertNotNull("Проверка навигации назад (к root)", currentNode);
        Assert.assertEquals(prevNodeName, currentNode.getData());
        currentNode =  navigationTree.getCurElem();
        Assert.assertNotNull("Проверка получения текущего элемента после prev()", currentNode);
        Assert.assertEquals(prevNodeName, currentNode.getData());
    }
}