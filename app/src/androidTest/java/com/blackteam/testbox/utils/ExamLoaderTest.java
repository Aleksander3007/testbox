package com.blackteam.testbox.utils;

import android.app.Activity;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.AndroidJUnitRunner;
import android.test.ActivityInstrumentationTestCase;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

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

        examTree.next(new ExamThemeData("node1"));
        Assert.assertEquals("node1", examTree.getCurElem().getData().getName());

        examTree.next(new ExamThemeData("node1.1", "source1.1"));
        Assert.assertEquals("node1.1", examTree.getCurElem().getData().getName());
        Assert.assertEquals("source1.1", examTree.getCurElem().getData().getSource());

        examTree.prev();
        examTree.next(new ExamThemeData("node1.2", "source1.2"));
        Assert.assertEquals("node1.2", examTree.getCurElem().getData().getName());
        Assert.assertEquals("source1.2", examTree.getCurElem().getData().getSource());
    }

    private NavigationTree<ExamThemeData> createExamTree() {
        NavigationTree<ExamThemeData> examTree = new NavigationTree<>(new ExamThemeData("root"));
        NavigationTree.Node<ExamThemeData> root = examTree.getRootElement();

        NavigationTree.Node<ExamThemeData> node1 = root.addChild(new ExamThemeData("node1"));
        root.addChild(new ExamThemeData("node2", "source2"));
        node1.addChild(new ExamThemeData("node1.1", "source1.1"));
        node1.addChild(new ExamThemeData("node1.2", "source1.2"));

        return examTree;
    }
}