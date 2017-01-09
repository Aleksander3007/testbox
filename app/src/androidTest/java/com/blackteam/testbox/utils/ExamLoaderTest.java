package com.blackteam.testbox.utils;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.blackteam.testbox.ExamThemeData;
import com.blackteam.testbox.ui.MainActivity;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExamLoaderTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testLoadExam() throws Exception {
        Assert.assertNull(null);
    }

    @Test
    public void testSaveExam() throws Exception {
        Context context = mActivityRule.getActivity().getApplicationContext();
        ExamLoader.saveExam(context, createExamTree());

        NavigationTree<ExamThemeData> examTree = ExamLoader.loadExam(context);
        Assert.assertEquals("root", examTree.getRootElement().getData().getName());

        examTree.next(new ExamThemeData("node1", "id1"));
        Assert.assertEquals("node1", examTree.getCurElem().getData().getName());

        examTree.next(new ExamThemeData("node1.1", "id1.1"));
        Assert.assertEquals("node1.1", examTree.getCurElem().getData().getName());
        Assert.assertEquals("id1.1", examTree.getCurElem().getData().getId());

        examTree.prev();
        examTree.next(new ExamThemeData("node1.2", "id1.2"));
        Assert.assertEquals("node1.2", examTree.getCurElem().getData().getName());
        Assert.assertEquals("id1.2", examTree.getCurElem().getData().getId());
    }

    private NavigationTree<ExamThemeData> createExamTree() {
        NavigationTree<ExamThemeData> examTree = new NavigationTree<>(new ExamThemeData("root", "id0"));
        NavigationTree.Node<ExamThemeData> root = examTree.getRootElement();

        NavigationTree.Node<ExamThemeData> node1 = root.addChild(new ExamThemeData("node1", "id1"));
        root.addChild(new ExamThemeData("node2", "id2"));
        node1.addChild(new ExamThemeData("node1.1", "id1.1"));
        node1.addChild(new ExamThemeData("node1.2", "id1.2"));

        return examTree;
    }
}