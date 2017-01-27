package com.blackteam.testbox.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestAnswer;
import com.blackteam.testbox.TestQuestion;
import com.blackteam.testbox.utils.ListCursor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Страница с вопросами для тренировки.
 */
public class TrainingQuestionActivity extends Activity {

    /** Состояние в котором находится ответ на вопрос. */
    enum QuestionState {
        /** Ответ на вопрос еще не был дан. */
        THINKING,
        /** На вопрос был получен ответ. */
        ANSWERED,
        /** На все вопросы был получен ответ. */
        COMPLETED
    }

    @BindView(R.id.btn_finish) Button mFinishBtn;
    @BindView(R.id.tv_question) TextView mQuestionTextView;
    @BindView(R.id.ll_answers) LinearLayout mAnswersLinearLayout;
    @BindView(R.id.btn_submit) Button mSubmitBtn;
    @BindView(R.id.btn_nextQuestion) Button mNextQuestionBtn;
    @BindView(R.id.btn_goToResult) Button mGoToResultBtn;

    private ExamTest mExamTest;
    private ListCursor<TestQuestion> mQuestionCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_question);
        ButterKnife.bind(this);

        mExamTest = (ExamTest) getIntent().getExtras().getSerializable("ExamTest");
        mQuestionCursor = new ListCursor<>(mExamTest.getQuestions());
        displayQuestion(mQuestionCursor.getCurrent());
        updateView(QuestionState.THINKING);
    }

    @OnClick(R.id.btn_submit)
    public void submitOnClick(View view) {
        if (mQuestionCursor.hasNext())
            updateView(QuestionState.ANSWERED);
        else
            updateView(QuestionState.COMPLETED);

        boolean right = verifAnswers();
        if (right)
            Toast.makeText(this, R.string.msg_right_answer, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, R.string.msg_wrong_answer, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_nextQuestion)
    public void nextQuestionOnClick(View view) {
        if (mQuestionCursor.hasNext()) {
            updateView(QuestionState.THINKING);
            displayQuestion(mQuestionCursor.next());
        }
        else {
            updateView(QuestionState.COMPLETED);
        }
    }

    @OnClick(R.id.btn_goToResult)
    public void goToResultOnClick(View view) {
        // TODO: Переход на страницу с результатами.
    }

    @OnClick(R.id.btn_finish)
    public void finishOnClick(View view) {
        // TODO: Закончить тестирование и перейти на страницу с результатами.
    }

    /**
     * Обновление вида окна в зависимости от состояния ответа на вопрос.
     * @param questionState состояние ответа на вопрос.
     */
    private void updateView(QuestionState questionState) {
        mFinishBtn.setVisibility(View.VISIBLE);
        mSubmitBtn.setVisibility(View.INVISIBLE);
        mNextQuestionBtn.setVisibility(View.INVISIBLE);
        mGoToResultBtn.setVisibility(View.INVISIBLE);

        switch (questionState) {
            case THINKING:
                mSubmitBtn.setVisibility(View.VISIBLE);
                break;
            case ANSWERED:
                mNextQuestionBtn.setVisibility(View.VISIBLE);
                break;
            case COMPLETED:
                mGoToResultBtn.setVisibility(View.VISIBLE);
                mFinishBtn.setVisibility(View.INVISIBLE);
                break;
        }
    }
    /**
     * Отобразить указанный экзамеционный вопрос.
     * @param question экзамеционный вопрос, который необходимо отобразить.
     */
    private void displayQuestion(TestQuestion question) {
        mQuestionTextView.setText(question.getText());
        displayAnswer(question.getAnswers());
    }

    /**
     * Отобразить указанные ответы.
     * @param answers ответы, которые необходимо отобразить.
     */
    private void displayAnswer(List<TestAnswer> answers) {
        mAnswersLinearLayout.removeAllViews();
        for (TestAnswer answer : answers) addAnswerView(answer);
    }

    /**
     * Добавить элемент, отображающий возможный вариант ответа в Activity.
     * @param answer Текст ответа.
     */
    private void addAnswerView(TestAnswer answer) {
        final View answerView = getLayoutInflater().inflate(R.layout.listview_elem_answer, null);
        TextView answerTextView = (TextView) answerView.findViewById(R.id.tv_answerText);
        answerTextView.setText(answer.getText());
        // Обработка нажатия на ответ.
        answerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
            }
        });
        mAnswersLinearLayout.addView(answerView);
    }

    /**
     * Проверяем данные пользователем ответы.
     * @return true - если всё правильно.
     */
    private boolean verifAnswers() {
        List<TestAnswer> answers = mQuestionCursor.getCurrent().getAnswers();
        int nAnswers = mAnswersLinearLayout.getChildCount();
        for (int iAnswer = 0; iAnswer < nAnswers; iAnswer++) {
            TestAnswer answer = answers.get(iAnswer);
            final View answerView = mAnswersLinearLayout.getChildAt(iAnswer);
            View answerIndicator = answerView.findViewById(R.id.indicator);

            answer.setMark(answerView.isSelected());

            // Делаем ответы не кликабельными (После проверки ответа уже нельзя ничего не менять).
            answerView.setClickable(false);
            // Затемняем те ответы, которые не были выбраны.
            answerView.setAlpha(answer.isMarked() ? 1.0f : 0.3f);
            // Устанавливаем цвет индикатора правильный/неправильный ответ.
            answerIndicator.setBackgroundResource(answer.isRight() ?
                    R.color.answer_indicator_success :
                    R.color.answer_indicator_error);
        }

        return mQuestionCursor.getCurrent().verify();
    }
}
