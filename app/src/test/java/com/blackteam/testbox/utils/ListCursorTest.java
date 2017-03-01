package com.blackteam.testbox.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Тестирование класса ListCursor.
 */
public class ListCursorTest {

    private List<Integer> mListStub = new ArrayList<>();

    @Before
    public void setUp() {
        mListStub.add(0);
        mListStub.add(1);
        mListStub.add(2);
    }

    @Test
    public void getCurrent() throws Exception {
        ListCursor<Integer> listCursor = new ListCursor<>(mListStub);
        Assert.assertEquals(0, listCursor.getCurrent().intValue());
    }

    @Test
    public void next() throws Exception {
        ListCursor<Integer> listCursor = new ListCursor<>(mListStub);
        Integer nextElem = listCursor.next();

        Assert.assertEquals(1, nextElem.intValue());
        Assert.assertEquals(1, listCursor.getCurrent().intValue());
    }

    @Test
    public void next_Double() throws Exception {
        ListCursor<Integer> listCursor = new ListCursor<>(mListStub);
        listCursor.next();
        Integer nextElem = listCursor.next();

        Assert.assertEquals(2, nextElem.intValue());
        Assert.assertEquals(2, listCursor.getCurrent().intValue());
    }

    @Test
    public void previous() throws Exception {
        ListCursor<Integer> listCursor = new ListCursor<>(mListStub);
        listCursor.next(); // go to 1.
        Integer prevElem = listCursor.previous(); // back to 0.

        Assert.assertEquals(0, prevElem.intValue());
        Assert.assertEquals(0, listCursor.getCurrent().intValue());
    }

    @Test
    public void hasNext_True() throws Exception {
        ListCursor<Integer> listCursor = new ListCursor<>(mListStub);
        Assert.assertTrue(listCursor.hasNext());
    }

    @Test
    public void hasNext_False() throws Exception {
        ListCursor<Integer> listCursor = new ListCursor<>(mListStub);
        listCursor.next();
        listCursor.next();
        Assert.assertFalse(listCursor.hasNext());
    }

    @Test
    public void hasPrevious_True() throws Exception {
        ListCursor<Integer> listCursor = new ListCursor<>(mListStub);
        listCursor.next();
        Assert.assertTrue(listCursor.hasPrevious());
    }

    @Test
    public void hasPrevious_False() throws Exception {
        ListCursor<Integer> listCursor = new ListCursor<>(mListStub);
        Assert.assertFalse(listCursor.hasPrevious());
    }

    @Test
    public void isEmpty_False() throws Exception {
        ListCursor<Integer> listCursor = new ListCursor<>(mListStub);
        Assert.assertFalse(listCursor.isEmpty());
    }

    @Test
    public void isEmpty_True() throws Exception {
        ListCursor<Integer> listCursor = new ListCursor<>(new ArrayList<Integer>());
        Assert.assertTrue(listCursor.isEmpty());
    }

    @Test
    public void add() throws Exception {
        ListCursor<Integer> listCursor = new ListCursor<>(mListStub);

        int addedElem = 100;
        listCursor.add(addedElem);
        int currentElem = listCursor.getCurrent(); // текущий элемент.
        int nextElem = listCursor.next(); // добавленный элемент.
        int nextNextElem = listCursor.next(); // ... и смотрим следующий за ним.

        Assert.assertEquals(0, currentElem);
        Assert.assertEquals(addedElem, nextElem);
        Assert.assertEquals(1, nextNextElem);
    }

    @Test
    public void set() throws Exception {
        ListCursor<Integer> listCursor = new ListCursor<>(mListStub);
        int setElem = 100;
        listCursor.set(setElem);
        Assert.assertEquals(setElem, listCursor.getCurrent().intValue());
        Assert.assertEquals(1, listCursor.next().intValue());
    }

}