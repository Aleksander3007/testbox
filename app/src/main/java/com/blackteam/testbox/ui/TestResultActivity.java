package com.blackteam.testbox.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
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
public class TestResultActivity extends BaseActivity {

    public static final String EXTRA_EXAM_TEST = "com.testbox.extras.EXTRA_EXAM_TEST";

    @BindView(R.id.tv_num_correct_answers) TextView mCorrectAnswersTextView;
    @BindView(R.id.tv_percent_correct_answers) TextView mPercentCorrectAnswersTextView;
    @BindView(R.id.ll_questions) LinearLayout mQuestionsLinearLayout;

    private ExamTest mExamTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);
        ButterKnife.bind(this);

        mExamTest = (ExamTest) getIntent().getExtras().getSerializable(EXTRA_EXAM_TEST);
        verifQuestions();
        showResult();
        showQuestions();
    }

    @Override
    public void onBackPressed() {
        this.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // INFO: onCreateOptionsMenu() вызывается после Activity.onResume().

        // Проверяем есть ли меню.
        boolean menuDisplayed = super.onCreateOptionsMenu(menu);
        if (menuDisplayed) {
            // Скрываем кнопку перехода между режимами.
            mUserTypeMenuItem.setVisible(false);
        }

        return menuDisplayed;
    }

    /**
     * Запуск проверки вопросов на корректность ответа пользователя.
     */
    private void verifQuestions() {
        for (TestQuestion question : mExamTest.getQuestions()) question.verify();
    }

    /**
     * Нажатие на кнопку "Закрыть окно результата теста".
     */
    @OnClick(R.id.btn_close_test_result)
    public void onCloseResultClick() {
        this.close();
    }

    /**
     * Закрываем Activity.
     */
    private void close() {
        // Закрываем Activity (и переходим к странице старта теста, страница прохождения теста/тренировки
        // недоступна, см. флаги ниже).
        Intent testStartActivity = new Intent(this, ExamTestStartActivity.class);
        // Возращение к окну прохождения теста/тренировки заапрещено.
        testStartActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(testStartActivity);
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
        return mExamTest.getActualNumQuestions();
    }

    private int geNumCorrectAnswers() {
        int numCorrectAnswers = 0;
        for (TestQuestion question : mExamTest.getQuestions()) {
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
        final View questionContentView = getLayoutInflater()
                .inflate(R.layout.expansion_panel_question, mQuestionsLinearLayout, false);
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
        View answerView = getLayoutInflater()
                .inflate(R.layout.listview_elem_answer, answersLinearLayout, false);
        TextView answerTextView = (TextView) answerView.findViewById(R.id.tv_answerText);
        View answerIndicator = answerView.findViewById(R.id.indicator);

        answerView.setSelected(answer.isMarked());
        // Затемняем невыбранные ответы пользователем.
        answerView.setAlpha(answer.isMarked() ? 1f : 0.5f);
        // Устанавливаем цвет индикатора правильный/неправильный ответ.
        answerIndicator.setBackgroundResource(answer.isRight() ?
                R.color.answer_indicator_success :
                R.color.answer_indicator_error);
        answerTextView.setText(answer.getText());

        answersLinearLayout.addView(answerView);
    }
}
