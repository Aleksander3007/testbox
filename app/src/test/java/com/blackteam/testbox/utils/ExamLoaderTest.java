package com.blackteam.testbox.utils;

import android.content.Context;
import android.test.mock.MockContext;


import com.blackteam.testbox.ExamThemeData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExamLoaderTest {

    @Mock
    Context mMockContext;

    @Test
    public void testLoadExam() throws Exception {

    }

    @Test
    public void testSaveExam() throws Exception {
        ExamLoader.saveExam(mMockContext, createExamTree());

        NavigationTree<ExamThemeData> examTree = ExamLoader.loadExam(mMockContext);
    }

    private NavigationTree<ExamThemeData> createExamTree() {
        NavigationTree<ExamThemeData> examTree = new NavigationTree<>(new ExamThemeData("root"));
        NavigationTree.Node<ExamThemeData> root = examTree.getRootElement();

        NavigationTree.Node<ExamThemeData> node1 = root.addChild(new ExamThemeData("node1"));
        root.addChild(new ExamThemeData("node2"));
        node1.addChild(new ExamThemeData("node1.1"));
        node1.addChild(new ExamThemeData("node1.2"));

        return examTree;
    }
}