package com.blackteam.testbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.blackteam.testbox.ui.TestQuestionFragment;

import java.util.ArrayList;

/**
 * Адаптер для ViewPager, который отображает вопросы в тесте.
 */
public class TestQuestionAdapter extends FragmentStatePagerAdapter {

    private ArrayList<TestQuestion> mQuestions;

    public TestQuestionAdapter(FragmentManager fragmentManager, ArrayList<TestQuestion> questions) {
        super(fragmentManager);
        mQuestions= questions;
    }

    @Override
    public Fragment getItem(int position) {
        TestQuestionFragment fragment = new TestQuestionFragment();

        Bundle args = new Bundle();
        args.putSerializable(TestQuestionFragment.ARG_TEST_QUESTION, mQuestions.get(position));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return mQuestions.size();
    }
}
