package com.blackteam.testbox.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackteam.testbox.R;
import com.blackteam.testbox.TestAnswer;
import com.blackteam.testbox.TestQuestion;

import junit.framework.Test;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Фрагмент, для отображения вопроса в тесте.
 */

public class TestQuestionFragment extends Fragment {

    public static final String ARG_TEST_QUESTION = "testQuestion";

    @BindView(R.id.tv_question) TextView mQuestionTextView;
    @BindView(R.id.ll_answers) LinearLayout mAnswersLinearLayout;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.exam_test_question, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        TestQuestion question = (TestQuestion) getArguments().getSerializable(ARG_TEST_QUESTION);
        displayQuestion(question);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void displayQuestion(TestQuestion question) {
        mQuestionTextView.setText(question.getText());
        for (TestAnswer answer : question.getAnswers()) addAnswerView(answer);
    }

    /**
     * Добавить элемент, отображающий возможный вариант ответа в Activity.
     * @param answer Текст ответа.
     */
    private void addAnswerView(TestAnswer answer) {
        final View answerView = getLayoutInflater(null).inflate(R.layout.listview_elem_answer, null);
        TextView answerTextView = (TextView) answerView.findViewById(R.id.tv_answerText);
        answerTextView.setText(answer.getText());
        mAnswersLinearLayout.addView(answerView);
    }
}
