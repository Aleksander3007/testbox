package com.blackteam.testbox.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestAnswer;
import com.blackteam.testbox.TestQuestion;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Страница с результами прохожения теста/тренировки.
 */
public class TestResultActivity extends Activity {
    @BindView(R.id.tv_num_correct_answers)
    TextView mCorrectAnswersTextView;
    @BindView(R.id.tv_percent_correct_answers)
    TextView mPercentCorrectAnswersTextView;
    @BindView(R.id.ll_questions)
    LinearLayout mQuestionsLinearLayout;

    private ExamTest mExamTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        ButterKnife.bind(this);

        mExamTest = (ExamTest) getIntent().getExtras().getSerializable("ExamTest");
        verifQuestions();
        showResult();
        showQuestions();
    }

    /**
     * Запуск проверки вопросов на корректность ответа пользователя.
     */
    private void verifQuestions() {
        for (TestQuestion question : mExamTest.getQuestions()) question.verify();
    }

    /**
     * Нажатие на кнопку "Закрыть окно результата теста".
     * @param view
     */
    @OnClick(R.id.btn_close_test_result)
    public void closeResultOnClick(View view) {
        // Закрываем Activity (и переходим к странице старта теста, страница прохождения теста
        // недоступна, т.к. у неё стоит параметр noHistory, см. AndroidManifest.xml).
        this.finish();
    }

    /**
     * Отобразить результаты.
     */
    private void showResult() {
        int numCorrectAnswers = geNumCorrectAnswers();
        mCorrectAnswersTextView.setText(String.format("%d/%d", numCorrectAnswers, getNumQuestions()));
        mPercentCorrectAnswersTextView.setText(
                String.format(
                        "%.2f",
                        getPercentCorrectAnswers(numCorrectAnswers, getNumQuestions())
                )
        );
    }

    private int getNumQuestions() {
        return mExamTest.getAllQuestions().size();
    }

    private int geNumCorrectAnswers() {
        int numCorrectAnswers = 0;
        for (TestQuestion question : mExamTest.getAllQuestions()) {
            if (question.rightAnswer()) numCorrectAnswers++;
        }
        return numCorrectAnswers;
    }

    private float getPercentCorrectAnswers(int numCorrectAnswers, int totalAnswers) {
        return ((float) numCorrectAnswers / totalAnswers) * 100f;
    }

    /**
     * Отобразить список вопросов.
     */
    private void showQuestions() {
        for (TestQuestion question : mExamTest.getQuestions()) showQuestion(question);
    }

    /**
     * Отобразить вопрос.
     */
    private void showQuestion(TestQuestion question) {
        final View questionContentView =
                getLayoutInflater().inflate(R.layout.expansion_panel_question, null);
        final TextView questionTitleTextView =
                (TextView) questionContentView.findViewById(R.id.tv_question_title);
        final ImageView indicatorImageView =
                (ImageView) questionContentView.findViewById(R.id.iv_question_indicator);
        final TextView questionTextView =
                (TextView) questionContentView.findViewById(R.id.tv_question);
        final LinearLayout answersLinearLayout =
                (LinearLayout) questionContentView.findViewById(R.id.ll_answers);
        final TextView explanationTextView =
                (TextView) questionContentView.findViewById(R.id.tv_explanation);
        final View questionHeaderView =
                questionContentView.findViewById(R.id.rl_question_header);
        final ImageButton expandButton =
                (ImageButton) questionContentView.findViewById(R.id.btn_expand);
        final ExpandableLinearLayout questionContentExpandableLayout =
                (ExpandableLinearLayout) questionContentView.findViewById(R.id.el_question_content);

        questionHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Проверка в начале потому, что isExpanded() меняет свое значение,
                // только когда полностью свернётся или развернется.

                // Если панель была развернута, то мы сейчас ее свернем, а значит
                // изображение на кнопке должно стать "Развернуть".
                expandButton.setImageResource(questionContentExpandableLayout.isExpanded() ?
                        R.drawable.ic_expand_more_black_24dp :
                        R.drawable.ic_expand_less_black_24dp
                );

                questionContentExpandableLayout.toggle();
            }
        });

        questionTitleTextView.setText(question.getText());
        questionTextView.setVisibility(View.GONE);
        explanationTextView.setText(question.getExplanation());

        // Отображаем вопрос в соответствии как на него ответил пользователь (правильно/неправильно).
        // ... выставляем необходимую иконку.
        indicatorImageView.setImageResource(question.rightAnswer() ?
            R.drawable.ic_check_black_24dp :
            R.drawable.ic_error_outline_black_24dp);
        // ... меняем ее цвет на нужный.
        indicatorImageView.setColorFilter(question.rightAnswer() ?
                ContextCompat.getColor(this, R.color.question_indicator_success) :
            ContextCompat.getColor(this, R.color.question_indicator_error));

        // Отображаем ответы на вопрос.
        for (TestAnswer answer : question.getAnswers()) showAnswer(answer, answersLinearLayout);

        mQuestionsLinearLayout.addView(questionContentView);
    }

    private void showAnswer(TestAnswer answer, LinearLayout answersLinearLayout) {
        View answerView = getLayoutInflater().inflate(R.layout.listview_elem_answer, null);
        TextView answerTextView = (TextView) answerView.findViewById(R.id.tv_answerText);
        View answerIndicator = answerView.findViewById(R.id.indicator);

        answerView.setSelected(answer.isMarked());
        // Устанавливаем цвет индикатора правильный/неправильный ответ.
        answerIndicator.setBackgroundResource(answer.isRight() ?
                R.color.answer_indicator_success :
                R.color.answer_indicator_error);
        answerTextView.setText(answer.getText());

        answersLinearLayout.addView(answerView);
    }
}
