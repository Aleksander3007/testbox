package com.blackteam.testbox;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Описывает данные связанные с экзамеционным тестом.
 */
public class ExamTest implements Serializable {
    /** Формат имени файла. Для получения имени файла использовать функции {@link #getFileName()}*/
    private static final String sFileNameFormat = "et%s.xml";

    private static final String sDescriptionTag = "description";
    private static final String sQuestionTag = "question";
    private static final String sAnswerTag = "answer";

    private static final String sDescriptionAttr = "text";
    private static final String sQuestionTextAttr = "text";
    private static final String sAnswerTextAttr = "text";
    private static final String sIsRightAnswerAttr = "isRight";

    private String mName;
    private String mDescription;
    private List<TestQuestion> questions = new ArrayList<>();

    /**
     * Конструктор.
     * @param name Имя экзамеционного теста.
     */
    public ExamTest(String name) {
        this.mName = name;
    }

    public void setDescription(String description) { mDescription = description; }
    public String getDescription() { return mDescription; }

    public List<TestQuestion> getQuestions() { return questions; }

    public void addQuestion(TestQuestion question) {
        questions.add(question);
    }

    /**
     * Загрузка данных об экзамеционном тесте.
     * @param context {@link Context}.
     * @return список экзамеционных вопросов.
     * @throws IOException
     * @throws XmlPullParserException
     */
    public List<TestQuestion> load(Context context)
            throws IOException, XmlPullParserException {

        FileInputStream fileInputStream = context.openFileInput(getFileName());
        readExamTestFile(fileInputStream);
        fileInputStream.close();
        return questions;
    }

    /**
     * Чтение файла, содержащего данные о экзам. тесте.
     * @param fileInputStream Поток файла, содержащего данные о экзам. тесте.
     * @throws IOException
     * @throws XmlPullParserException
     */
    private void readExamTestFile(FileInputStream fileInputStream)
            throws IOException, XmlPullParserException {

        // Открываем reader и считываем всё в строку.
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        char[] inputBuffer = new char[fileInputStream.available()];
        inputStreamReader.read(inputBuffer);
        String fileData = new String(inputBuffer);
        inputStreamReader.close();

        // Распарсиваем строку как xml.
        XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
        xmlFactory.setNamespaceAware(true);
        XmlPullParser xmlParser = xmlFactory.newPullParser();
        xmlParser.setInput(new StringReader(fileData));

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
                    // Если тег ответ, то считываем данные по ответам.
                    else if (xmlParser.getName().equals(sAnswerTag)) {
                        String answerText = xmlParser.getAttributeValue(null, sAnswerTextAttr);
                        boolean isRightAnswer = Boolean.parseBoolean(
                                xmlParser.getAttributeValue(null, sIsRightAnswerAttr));
                        TestAnswer answer = new TestAnswer(answerText, isRightAnswer);
                        answers.add(answer);
                    }
                    else if (xmlParser.getName().equals(sDescriptionTag)) {
                        mDescription = xmlParser.getAttributeValue(null, sDescriptionAttr);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    // Если закрывается тег вопрос, то добавляем к нему все ответы,
                    // и сам вопрос добавляем в массив вопросов.
                    if (xmlParser.getName().equals(sQuestionTag)) {
                        if (question != null) {
                            question.addAnswers(answers);
                            questions.add(question);
                            answers.clear();
                        }
                    }
                    break;
            }
            eventType = xmlParser.next();
        }
    }

    /**
     * Сохранение данных об экзамеционном тесте.
     * @param context {@link Context}.
     * @throws IOException
     */
    public void save(Context context) throws IOException {
        writeExamTestFile(context);
    }

    /**
     * Получить имя файла, содержащее информацию по данному тесту.
     * @return Имя файла.
     */
    public String getFileName() {
        return String.format(sFileNameFormat, mName);
    }

    private void writeExamTestFile(Context context) throws IOException {
        String dataWrite = createXmlData();
        try {
            FileOutputStream fileOutputStream =
                    context.openFileOutput(getFileName(), Context.MODE_PRIVATE);
            fileOutputStream.write(dataWrite.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException fnfex) {
            // Такого быть не может, т.к. если файла не существует, openFileOutput() создаст файл.
            fnfex.printStackTrace();
            Log.e("FileNotFoundException", fnfex.getMessage());
        }
    }

    /**
     * Создать строку в формате xml, содержащию данные об экзамеционном тесте.
     * @return Строка в формате xml.
     * @throws IOException
     */
    private String createXmlData() throws IOException {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();
        xmlSerializer.setOutput(stringWriter);
        xmlSerializer.startDocument("UTF-8", true);

        if (mDescription != null) {
            xmlSerializer.startTag(null, sDescriptionTag);
            xmlSerializer.attribute(null, sDescriptionAttr, mDescription);
            xmlSerializer.endTag(null, sDescriptionTag);
        }

        // Записываем все вопросы в тесте.
        for (TestQuestion testQuestion : questions) {
            xmlSerializer.startTag(null, sQuestionTag);
            xmlSerializer.attribute(null, sQuestionTextAttr, testQuestion.getText());

            // Записываем все ответы для каждого вопроса в тесте.
            for (TestAnswer testAnswer : testQuestion.getAnswers()) {
                xmlSerializer.startTag(null, sAnswerTag);
                xmlSerializer.attribute(null, sAnswerTextAttr, testAnswer.getText());
                xmlSerializer.attribute(null, sIsRightAnswerAttr, String.valueOf(testAnswer.isRight()));
                xmlSerializer.endTag(null, sAnswerTag);
            }

            xmlSerializer.endTag(null, sQuestionTag);
        }

        xmlSerializer.flush();

        return stringWriter.toString();
    }

    /**
     * Удаление данных об экзамеционном тесте.
     * @param context {@link Context}
     * @return true если файл был успешно удалён.
     */
    public boolean delete(Context context) {
        return context.deleteFile(getFileName());
    }
}
