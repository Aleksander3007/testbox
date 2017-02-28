package com.blackteam.testbox;

import android.content.Context;
import android.util.Xml;

import com.blackteam.testbox.utils.XmlParceable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Описывает данные связанные с экзамеционным тестом.
 */
public class ExamTest implements Serializable, XmlParceable {
    /** Формат имени файла. Для получения имени файла использовать функции {@link #getFileName()}*/
    private static final String sFileNameFormat = "et%s.xml";

    private static final String sExamTestTag = "examTest";
    private static final String sDescriptionTag = "description";
    private static final String sQuestionTag = "question";
    private static final String sExplanationTag = "explanation";
    private static final String sAnswerTag = "answer";
    private static final String sTestingAttrsTag = "testingAttrs";

    private static final String sDescriptionAttr = "text";
    private static final String sQuestionTextAttr = "text";
    private static final String sExplanationAttr = "text";
    private static final String sAnswerTextAttr = "text";
    private static final String sIsRightAnswerAttr = "isRight";
    private static final String sNumTestQuestionsAttr = "nQuestions";
    private static final String sTestTimeLimitAttr = "timeLimit";

    /** 20 минут. */
    private static final int sTestTimeLimitDefault = 1200;

    private final String mName;
    private String mDescription;
    private List<TestQuestion> mQuestions = new ArrayList<>();
    /** Время на прохождение теста, с. */
    private int mTestTimeLimit = sTestTimeLimitDefault;
    /** Количество вопросов, которые должны учавствовать в тестирование. */
    private int mNumTestQuestions;
    /** Актуальное кол-во вопросов, которые будут учавствовать в тестирование. */
    private int mActualNumQuestions;

    /**
     * Конструктор.
     * @param name Имя экзамеционного теста.
     */
    public ExamTest(String name) {
        this.mName = name;
    }

    public void setDescription(String description) { mDescription = description; }
    public String getDescription() { return mDescription; }

    public List<TestQuestion> getAllQuestions() { return mQuestions; }
    /** Возращает только те вопросы, которые будут учавствовать в тестировании или тренировки. */
    public List<TestQuestion> getQuestions() {
        List<TestQuestion> questions = new ArrayList<>();
        for (int iQuestion = 0; iQuestion < mActualNumQuestions; iQuestion++) {
            questions.add(mQuestions.get(iQuestion));
        }
        return questions;
    }

    public int getNumTestQuestions() {return mNumTestQuestions; }
    public void setNumTestQuestions(int numTestQuestions) { mNumTestQuestions = numTestQuestions; }

    public int getActualNumQuestions() { return mActualNumQuestions; }
    public void setActualNumQuestions(int actualNumQuestions) {mActualNumQuestions = actualNumQuestions; }

    public int getTimeLimit() {
        return mTestTimeLimit;
    }

    public void setTimeLimit(int testTimeLimit) {
        mTestTimeLimit = testTimeLimit;
    }

    /** Установка режима тестирования. */
    public void setTest() {
        mActualNumQuestions = mNumTestQuestions;
    }

    /**
     * Получить имя файла, содержащее информацию по данному тесту.
     * @return Имя файла.
     */
    public String getFileName() {
        return String.format(sFileNameFormat, mName);
    }

    /**
     * Удаление данных об экзамеционном тесте.
     * @param context {@link Context}
     * @return true если файл был успешно удалён.
     */
    public boolean delete(Context context) {
        return context.deleteFile(getFileName());
    }

    /**
     * Перемешать вопросы и ответы.
     */
    public void shuffle() {
        for (TestQuestion question : mQuestions) question.shuffleAnswers();
        Collections.shuffle(mQuestions);
    }

    /**
     * Парсит тест через xmlParser.
     * @param xmlParser xmlParser.
     * @throws XmlPullParserException
     */
    @Override
    public void parseData(XmlPullParser xmlParser) throws XmlPullParserException, IOException {

        this.reset();

        TestQuestion question = null;
        List<TestAnswer> answers = new ArrayList<>();

        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    // Если тег вопрос, то считываем данные по вопросу.
                    if (xmlParser.getName().equals(sQuestionTag)) {
                        String questionText = xmlParser.getAttributeValue(null, sQuestionTextAttr);
                        question = new TestQuestion(questionText);
                    }
                    else if (xmlParser.getName().equals(sExplanationTag)) {
                        String explanationText = xmlParser.getAttributeValue(null, sQuestionTextAttr);
                        question.setExplanation(explanationText);
                    }
                    // Если тег ответ, то считываем данные по ответам.
                    else if (xmlParser.getName().equals(sAnswerTag)) {
                        String answerText = xmlParser.getAttributeValue(null, sAnswerTextAttr);
                        boolean isRightAnswer = Boolean.parseBoolean(
                                xmlParser.getAttributeValue(null, sIsRightAnswerAttr));
                        TestAnswer answer = new TestAnswer(answerText, isRightAnswer);
                        answers.add(answer);
                    }
                    else if (xmlParser.getName().equals(sDescriptionTag)) {
                        setDescription(xmlParser.getAttributeValue(null, sDescriptionAttr));
                    }
                    else if (xmlParser.getName().equals(sTestingAttrsTag)) {
                        try {
                            setNumTestQuestions(Integer.parseInt(xmlParser
                                    .getAttributeValue(null, sNumTestQuestionsAttr)));
                        }
                        catch (NumberFormatException nfex) {/* игнорируем, т.е. значение будет по default. */ }
                        try {
                            setTimeLimit(Integer.parseInt(xmlParser
                                    .getAttributeValue(null, sTestTimeLimitAttr)));
                        }
                        catch (NumberFormatException nfex) {/* игнорируем, т.е. значение будет по default. */ }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    // Если закрывается тег вопрос, то добавляем к нему все ответы,
                    // и сам вопрос добавляем в массив вопросов.
                    if (xmlParser.getName().equals(sQuestionTag)) {
                        if (question != null) {
                            question.addAnswers(answers);
                            mQuestions.add(question);
                            answers.clear();
                        }
                    }
                    break;
            }
            eventType = xmlParser.next();
        }
    }

    /**
     * Создать строку в формате xml, содержащию данные об экзамеционном тесте.
     * @return Строка в формате xml.
     * @throws IOException
     */
    @Override
    public String parseXmlString() throws IOException {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();
        xmlSerializer.setOutput(stringWriter);
        xmlSerializer.startDocument("UTF-8", true);

        xmlSerializer.startTag(null, sExamTestTag);

        if (mDescription != null) {
            xmlSerializer.startTag(null, sDescriptionTag);
            xmlSerializer.attribute(null, sDescriptionAttr, mDescription);
            xmlSerializer.endTag(null, sDescriptionTag);
        }

        xmlSerializer.startTag(null, sTestingAttrsTag);
        xmlSerializer.attribute(null, sNumTestQuestionsAttr, String.valueOf(mNumTestQuestions));
        xmlSerializer.attribute(null, sTestTimeLimitAttr, String.valueOf(mTestTimeLimit));
        xmlSerializer.endTag(null, sTestingAttrsTag);

        // Записываем все вопросы в тесте.
        for (TestQuestion testQuestion : mQuestions) {
            xmlSerializer.startTag(null, sQuestionTag);
            xmlSerializer.attribute(null, sQuestionTextAttr, testQuestion.getText());

            if (testQuestion.getExplanation() != null) {
                xmlSerializer.startTag(null, sExplanationTag);
                xmlSerializer.attribute(null, sExplanationAttr, testQuestion.getExplanation());
                xmlSerializer.endTag(null, sExplanationTag);
            }

            // Записываем все ответы для каждого вопроса в тесте.
            for (TestAnswer testAnswer : testQuestion.getAnswers()) {
                xmlSerializer.startTag(null, sAnswerTag);
                xmlSerializer.attribute(null, sAnswerTextAttr, testAnswer.getText());
                xmlSerializer.attribute(null, sIsRightAnswerAttr, String.valueOf(testAnswer.isRight()));
                xmlSerializer.endTag(null, sAnswerTag);
            }

            xmlSerializer.endTag(null, sQuestionTag);
        }

        xmlSerializer.endTag(null, sExamTestTag);

        xmlSerializer.flush();

        return stringWriter.toString();
    }

    /**
     * Сброс теста в начальное состояние.
     */
    private void reset() {
        mDescription = null;
        mQuestions = new ArrayList<>();
        mTestTimeLimit = sTestTimeLimitDefault;
        mNumTestQuestions = 0;
        mActualNumQuestions = 0;
    }
}
