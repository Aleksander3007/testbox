package com.blackteam.testbox.utils;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Тестирование навигационного дерева и его узлов.
 */
public class NavigationTreeTest {

    @Test
    public void testCreateTree() {
        NavigationTree<String> navigationTree = new NavigationTree<>("Root element");
        Assert.assertNotNull(navigationTree.getRootElement());

        navigationTree = new NavigationTree<>();
        Assert.assertNull(navigationTree.getRootElement());

        navigationTree.createRootElement("Root element");
        Assert.assertNotNull(navigationTree.getRootElement());
    }

    @Test
    public void testAddGetChild() {
        NavigationTree<String> navigationTree = new NavigationTree<>("Root element");

        NavigationTree.Node<String> root = navigationTree.getRootElement();
        NavigationTree.Node<String> node11 = root.addChild("node1.1");
        Assert.assertNotNull(node11);
        Assert.assertNotNull(root.getChild("node1.1"));

        node11.addChild("node2.1");
        node11.addChild("node2.2");
        Assert.assertNotNull(root.getChild("node1.1"));
        Assert.assertNull(root.getChild("node2.1"));
        Assert.assertNotNull(node11.getChild("node2.1"));
        Assert.assertNotNull(node11.getChild("node2.2"));
    }

    @Test
    public void testAddChildren() {
        NavigationTree<String> navigationTree = new NavigationTree<>("Root element");
        NavigationTree.Node<String> root = navigationTree.getRootElement();

        // Проверка на добавление массива подузлов в root-узел.
        List<String> nodes1 = new ArrayList<>();
        nodes1.add("node1.1");
        nodes1.add("node1.2");
        root.addChildren(nodes1);
        Assert.assertNotNull(root.getChild("node1.1"));
        Assert.assertNotNull(root.getChild("node1.2"));

        // Проверка на добавление массива подузлов в узел 1-го уровня.
        NavigationTree.Node<String> node11 = root.getChild("node1.1");
        List<String> nodes2 = new ArrayList<>();
        nodes2.add("node2.1");
        nodes2.add("node2.2");
        nodes2.add("node2.3");
        node11.addChildren(nodes2);
        Assert.assertNotNull(node11.getChild("node2.1"));
        Assert.assertNotNull(node11.getChild("node2.2"));
        Assert.assertNotNull(node11.getChild("node2.3"));

        // Проверка на добавление массива подузлов в непустой узел 1-го уровня.
        List<String> nodes2a = new ArrayList<>();
        nodes2a.add("node2.1a");
        nodes2a.add("node2.2a");
        nodes2a.add("node2.3a");
        node11.addChildren(nodes2a);
        Assert.assertNotNull(node11.getChild("node2.1a"));
        Assert.assertNotNull(node11.getChild("node2.2a"));
        Assert.assertNotNull(node11.getChild("node2.3a"));
    }

    @Test
    public void testGetChildren() {
        NavigationTree<String> navigationTree = new NavigationTree<>("Root element");
        NavigationTree.Node<String> root = navigationTree.getRootElement();

        List<String> nodes1 = new ArrayList<>();
        nodes1.add("node1.1");
        nodes1.add("node1.2");
        root.addChildren(nodes1);
        Assert.assertNotNull(root.getChild("node1.1"));
        Assert.assertNotNull(root.getChild("node1.2"));

        Assert.assertNotNull(root.getChildren());
        for(NavigationTree.Node<String> children : root.getChildren()) {
            Assert.assertFalse(children.getName().equals(""));
        }
    }

    @Test
    public void testGetChildrenNames() {
        NavigationTree<String> navigationTree = new NavigationTree<>("Root element");
        NavigationTree.Node<String> root = navigationTree.getRootElement();

        List<String> nodes1 = new ArrayList<>();
        nodes1.add("node1.1");
        nodes1.add("node1.2");
        root.addChildren(nodes1);

        List<String> childrenNames = root.getChildrenNames();
        Assert.assertNotNull(childrenNames);
        for(String childName : childrenNames) {
            Assert.assertFalse(childName.equals(""));
        }
    }

    @Test
    public void testSerialize() {
        NavigationTree<String> navigationTree = new NavigationTree<>("Root element");
        NavigationTree.Node<String> root = navigationTree.getRootElement();

        root.addChild("node1.1");
        root.addChild("node1.2");

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("testSerialize.test"));
            oos.writeObject(navigationTree);

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("testSerialize.test"));
            NavigationTree<String> readNavigationTree = (NavigationTree<String>)ois.readObject();

            Assert.assertNotNull(readNavigationTree.getRootElement());
            Assert.assertNotNull(readNavigationTree.getRootElement().getChild("node1.1"));
        }
        catch (java.io.IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}