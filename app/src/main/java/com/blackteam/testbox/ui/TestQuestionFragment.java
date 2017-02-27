package com.blackteam.testbox.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackteam.testbox.R;
import com.blackteam.testbox.TestAnswer;
import com.blackteam.testbox.TestQuestion;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Фрагмент, для отображения вопроса в тесте.
 */
public class TestQuestionFragment extends Fragment {

    public static final String ARG_TEST_QUESTION = "ARG_TEST_QUESTION";

    @BindView(R.id.tv_question) TextView mQuestionTextView;
    @BindView(R.id.ll_answers) LinearLayout mAnswersLinearLayout;
    @BindView(R.id.tv_explanation) TextView mExpalantionTextView;

    private Unbinder unbinder;

    private TestQuestion mQuestion;

    /**
     * Создание экземпляра {@link TestQuestionFragment}
     * @param question экзамеционный вопрос, который необходимо отобразить.
     */
    public static TestQuestionFragment newInstance(TestQuestion question) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TEST_QUESTION, question);
        TestQuestionFragment fragment = new TestQuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test_question, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mQuestion = (TestQuestion) getArguments().getSerializable(ARG_TEST_QUESTION);
        displayQuestion(mQuestion);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Нажатие на кнопку завершения тестирования.
     * @param view нажатый элемент.
     */
    @OnClick(R.id.btn_finish_test)
    public void finishTestOnClick(View view) {
        ((TestQuestionActivity)getActivity()).finishTest();
    }

    private void displayQuestion(TestQuestion question) {
        mQuestionTextView.setText(question.getText());
        mExpalantionTextView.setVisibility(View.INVISIBLE);
        for (TestAnswer answer : question.getAnswers()) addAnswerView(answer);
    }

    /**
     * Добавить элемент, отображающий возможный вариант ответа в Activity.
     * @param answer Текст ответа.
     */
    private void addAnswerView(final TestAnswer answer) {
        final View answerView = getLayoutInflater(null).inflate(R.layout.listview_elem_answer, null);
        TextView answerTextView = (TextView) answerView.findViewById(R.id.tv_answerText);
        answerTextView.setText(answer.getText());
        answerView.setSelected(answer.isMarked());

        // Обработка нажатия на ответ.
        View.OnClickListener onAnswerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerView.setSelected(!answerView.isSelected());
                answer.setMark(answerView.isSelected());
            }
        };
        answerView.setOnClickListener(onAnswerClickListener);
        answerTextView.setOnClickListener(onAnswerClickListener);

        mAnswersLinearLayout.addView(answerView);
    }
}
