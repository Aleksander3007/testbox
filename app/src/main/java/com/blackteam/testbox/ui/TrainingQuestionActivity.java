package com.blackteam.testbox.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackteam.testbox.ExamTest;
import com.blackteam.testbox.R;
import com.blackteam.testbox.TestAnswer;
import com.blackteam.testbox.TestQuestion;
import com.blackteam.testbox.utils.ListCursor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Страница с вопросами для тренировки.
 */
public class TrainingQuestionActivity extends BaseActivity {

    public static final String EXTRA_EXAM_TEST = "com.testbox.extras.EXTRA_EXAM_TEST";

    /** Состояние в котором находится ответ на вопрос. */
    enum QuestionState {
        /** Ответ на вопрос еще не был дан. */
        THINKING,
        /** На вопрос был получен ответ. */
        ANSWERED,
        /** На все вопросы был получен ответ. */
        COMPLETED
    }
    @icepick.State QuestionState mQuestionState;

    @BindView(R.id.btn_finish) Button mFinishBtn;
    @BindView(R.id.tv_question) TextView mQuestionTextView;
    @BindView(R.id.tv_explanation) TextView mExplanationTextView;
    @BindView(R.id.ll_answers) LinearLayout mAnswersLinearLayout;
    @BindView(R.id.btn_submit) Button mSubmitBtn;
    @BindView(R.id.btn_nextQuestion) Button mNextQuestionBtn;
    @BindView(R.id.btn_goToResult) Button mGoToResultBtn;

    @icepick.State ExamTest mExamTest;
    @icepick.State ListCursor<TestQuestion> mQuestionCursor;
    @icepick.State HashMap<Integer, Boolean> selectedAnswers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_question);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            mExamTest = (ExamTest) getIntent().getExtras().getSerializable(EXTRA_EXAM_TEST);
            mExamTest.shuffle(); // Перемешиваем вопросы и ответы.
            mQuestionCursor = new ListCursor<>(mExamTest.getQuestions());
            mQuestionState = QuestionState.THINKING;
        }

        updateView(mQuestionState);
        displayQuestion(mQuestionCursor.getCurrent());

        if (savedInstanceState != null) {
            switch (mQuestionState) {
                case THINKING:
                    restoreSelectedAnswers();
                    break;
                case ANSWERED:
                case COMPLETED:
                    restoreSelectedAnswers();
                    verifAnswers();
                    break;
            }
        }
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

    @Override
    protected void onPause()
    {
        super.onPause();
        saveSelectedAnswers();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder confirmExitDialog = new AlertDialog.Builder(this);
        confirmExitDialog.setTitle(R.string.title_finish_training)
                .setMessage(R.string.msg_do_you_want_to_get_out_of_training)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TrainingQuestionActivity.super.onBackPressed();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @OnClick(R.id.btn_submit)
    public void onSubmitClick(View view) {
        if (mQuestionCursor.hasNext())
            updateView(QuestionState.ANSWERED);
        else
            updateView(QuestionState.COMPLETED);

        boolean right = verifAnswers();
        if (right) {
            Toast.makeText(this, R.string.msg_right_answer, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, R.string.msg_wrong_answer, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_nextQuestion)
    public void nextQuestionOnClick(View view) {
        if (mQuestionCursor.hasNext()) {
            updateView(QuestionState.THINKING);
            hideQuestionExplanation();
            displayQuestion(mQuestionCursor.next());
        }
        else {
            updateView(QuestionState.COMPLETED);
        }
    }

    /**
     * Нажатие на кнопку перехода к результатам.
     */
    @OnClick(R.id.btn_goToResult)
    public void onGoToResultClick(View view) {
        Intent trainingResultActivity = new Intent(this, TestResultActivity.class);
        trainingResultActivity.putExtra(TestResultActivity.EXTRA_EXAM_TEST, mExamTest);
        startActivity(trainingResultActivity);
    }

    /**
     * Нажатие на кнопку завершить тренировку.
     */
    @OnClick(R.id.btn_finish)
    public void onFinishClick(View view) {
        onSubmitClick(view);
        onGoToResultClick(view);
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
                hideQuestionExplanation();
                break;
            case ANSWERED:
                mNextQuestionBtn.setVisibility(View.VISIBLE);
                showQuestionExplanation();
                break;
            case COMPLETED:
                mGoToResultBtn.setVisibility(View.VISIBLE);
                mFinishBtn.setVisibility(View.INVISIBLE);
                showQuestionExplanation();
                break;
        }

        mQuestionState = questionState;
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
     * Отобразить объяснение ответа на вопрос.
     */
    private void showQuestionExplanation() {
        mExplanationTextView.setText(mQuestionCursor.getCurrent().getExplanation());
        mExplanationTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Скрыть объяснение ответа на вопрос.
     */
    private void hideQuestionExplanation() {
        mExplanationTextView.setText("");
        mExplanationTextView.setVisibility(View.INVISIBLE);
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

    /**
     * Сохраняем состояние выделенных ответов.
     */
    private void saveSelectedAnswers() {
        for (int iAnswer = 0; iAnswer < mAnswersLinearLayout.getChildCount(); iAnswer++) {
            View answerView = mAnswersLinearLayout.getChildAt(iAnswer);
            selectedAnswers.put(iAnswer, answerView.isSelected());
        }
    }

    /**
     * Восстанавливаем состояние выделенных ответов.
     */
    private void restoreSelectedAnswers() {
        for (int iAnswer = 0; iAnswer < mAnswersLinearLayout.getChildCount(); iAnswer++) {
            View answerView = mAnswersLinearLayout.getChildAt(iAnswer);
            answerView.setSelected(selectedAnswers.get(iAnswer));
        }
    }
}
