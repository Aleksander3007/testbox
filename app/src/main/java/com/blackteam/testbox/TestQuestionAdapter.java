package com.blackteam.testbox;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.blackteam.testbox.ui.TestQuestionFragment;

import java.util.ArrayList;

/**
 * Адаптер для ViewPager, который отображает вопросы в тесте.
 * Манипулирует экземплярами TestQuestionFragment для каждого экзам. вопроса.
 */
public class TestQuestionAdapter extends FragmentStatePagerAdapter {

    private ArrayList<TestQuestion> mQuestions;

    public TestQuestionAdapter(FragmentManager fragmentManager, ArrayList<TestQuestion> questions) {
        super(fragmentManager);
        mQuestions= questions;
    }

    @Override
    public Fragment getItem(int position) {
        return TestQuestionFragment.newInstance(mQuestions.get(position));
    }

    @Override
    public int getCount() {
        return mQuestions.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "№" + String.valueOf(position + 1);
    }
}
