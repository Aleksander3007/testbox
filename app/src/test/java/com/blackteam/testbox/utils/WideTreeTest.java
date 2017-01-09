package com.blackteam.testbox.utils;

import junit.framework.Assert;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class WideTreeTest {
    
    @Test
    public void testCreateTree() {
        WideTree<String> navigationTree = new WideTree<>("Root element");
        Assert.assertNotNull(navigationTree.getRootElement());

        navigationTree = new WideTree<>();
        Assert.assertNull(navigationTree.getRootElement());

        navigationTree.createRootElement("Root element");
        Assert.assertNotNull(navigationTree.getRootElement());
    }

    @Test
    public void testAddGetChild() {
        WideTree<String> navigationTree = new WideTree<>("Root element");

        WideTree.Node<String> root = navigationTree.getRootElement();
        WideTree.Node<String> node11 = root.addChild("node1.1");
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
        WideTree<String> navigationTree = new WideTree<>("Root element");
        WideTree.Node<String> root = navigationTree.getRootElement();

        // Проверка на добавление массива подузлов в mRoot-узел.
        List<String> nodes1 = new ArrayList<>();
        nodes1.add("node1.1");
        nodes1.add("node1.2");
        root.addChildren(nodes1);
        Assert.assertNotNull(root.getChild("node1.1"));
        Assert.assertNotNull(root.getChild("node1.2"));

        // Проверка на добавление массива подузлов в узел 1-го уровня.
        WideTree.Node<String> node11 = root.getChild("node1.1");
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
        WideTree<String> navigationTree = new WideTree<>("Root element");
        WideTree.Node<String> root = navigationTree.getRootElement();

        List<String> nodes1 = new ArrayList<>();
        nodes1.add("node1.1");
        nodes1.add("node1.2");
        root.addChildren(nodes1);
        Assert.assertNotNull(root.getChild("node1.1"));
        Assert.assertNotNull(root.getChild("node1.2"));

        Assert.assertNotNull(root.getChildren());
        for(WideTree.Node<String> children : root.getChildren()) {
            Assert.assertFalse(children.getData().equals(""));
        }
    }

    @Test
    public void testGetChildrenNames() {
        WideTree<String> navigationTree = new WideTree<>("Root element");
        WideTree.Node<String> root = navigationTree.getRootElement();

        List<String> nodes1 = new ArrayList<>();
        nodes1.add("node1.1");
        nodes1.add("node1.2");
        root.addChildren(nodes1);

        List<String> childrenNames = root.getChildrenData();
        Assert.assertNotNull(childrenNames);
        for(String childName : childrenNames) {
            Assert.assertFalse(childName.equals(""));
        }
    }

    @Test
    public void testSerialize() {
        WideTree<String> navigationTree = new WideTree<>("Root element");
        WideTree.Node<String> root = navigationTree.getRootElement();

        root.addChild("node1.1");
        root.addChild("node1.2");

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("testSerialize.test"));
            oos.writeObject(navigationTree);

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("testSerialize.test"));
            WideTree<String> readWideTree = (WideTree<String>)ois.readObject();

            Assert.assertNotNull(readWideTree.getRootElement());
            Assert.assertNotNull(readWideTree.getRootElement().getChild("node1.1"));
        }
        catch (java.io.IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}