package com.blackteam.testbox.utils;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.blackteam.testbox.ExamThemeData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Управляет загрузкой и сохранением данных об экзаменах.
 */
public class ExamLoader {
    public static final String EXAM_FILE_NAME = "exam.xml";

    public static final String THEME_TAG = "theme";

    public static final String NAME_ATTR = "name";
    public static final String SOURCE_ATTR = "src";

    /**
     * Загрузить данные об экзаменах.
     * @param context Контекст приложения.
     * @throws IOException, XmlPullParserException
     */
    public static NavigationTree<ExamThemeData> loadExam(Context context)
            throws IOException, XmlPullParserException {
        try {
            FileInputStream fileInputStream = context.openFileInput(EXAM_FILE_NAME);
            NavigationTree<ExamThemeData> examTree = readExamFile(fileInputStream);
            fileInputStream.close();
            return examTree;
        }
        catch (FileNotFoundException fnfex) {
            return null;
        }
    }

    /**
     * Сохранить данные об экзаменах.
     * @param context контекст приложения.
     * @param examTree дерево с данными об экзаменах.
     * @throws IOException
     */
    public static void saveExam(Context context, NavigationTree<ExamThemeData> examTree) throws IOException {
        writeExamFile(context, examTree);
    }

    /**
     * Записать данные об экзаменах в в файл.
     * @param context контекст приложения.
     * @param examTree дерево с данными об экзаменах.
     * @throws IOException
     */
    private static void writeExamFile(Context context, NavigationTree<ExamThemeData> examTree)
        throws IOException {
        try {
            String dataWrite = createXmlData(examTree);
            FileOutputStream fileOutputStream = context.openFileOutput(EXAM_FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(dataWrite.getBytes());
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            // Такого быть не может, т.к. если файла не существует, openFileOutput() создаст файл.
            e.printStackTrace();
            Log.e("FileNotFoundException", e.getMessage());
        }
    }

    /**
     * Чтение данных об экзаменах из файла.
     * @param fileInputStream входной поток данных.
     * @return данных об экзаменах в виде дерева.
     * @throws IOException
     * @throws XmlPullParserException
     */
    private static NavigationTree<ExamThemeData> readExamFile(FileInputStream fileInputStream)
            throws IOException, XmlPullParserException {
        // Открываем reader и считываем всё в строку.
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        char[] inputBuffer = new char[fileInputStream.available()];
        inputStreamReader.read(inputBuffer);
        String data = new String(inputBuffer);
        inputStreamReader.close();

        // Распарсиваем строку как xml.
        XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
        xmlFactory.setNamespaceAware(true);
        XmlPullParser xmlParser = xmlFactory.newPullParser();
        xmlParser.setInput(new StringReader(data));

        NavigationTree<ExamThemeData> examTree = new NavigationTree<>();

        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                // Движемся вперед, пока не дойдем до конечной темы.
                case XmlPullParser.START_TAG:
                    String examName = xmlParser.getAttributeValue(null, NAME_ATTR);
                    ExamThemeData examThemeData = new ExamThemeData(examName);
                    if (examTree.getRootElement() != null) {
                        examTree.getCurElem().addChild(examThemeData);
                        examTree.next(examThemeData);
                    }
                    else {
                        examTree.createRootElement(examThemeData);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    String examSrc = xmlParser.getAttributeValue(null, SOURCE_ATTR);
                    examThemeData = examTree.getCurElem().getData();
                    examThemeData.setSource(examSrc);
                    // Возращаемся на уровень назад.
                    examTree.prev();
                    break;
                default:
                    // Остальные типы тегов нас не интересуют.
                    break;
            }

            eventType = xmlParser.next();
        }

        return examTree;
    }

    /**
     * Парсинг данных об экзаменах в xml.
     * @param examTree данные об экзаменах в виде дерева.
     * @return данные об экзаменах в виде строки.
     * @throws IOException
     */
    private static String createXmlData(NavigationTree<ExamThemeData> examTree) throws IOException {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();
        xmlSerializer.setOutput(stringWriter);
        xmlSerializer.startDocument("UTF-8", true);

        // Формируется xml следующего типа:
        // <theme> <!-- 1-го уровня -->
        //   <theme> <!-- 2-го уровня -->
        //      <!-- ... -->
        //   <theme>
        // </theme>

        // Реализовано в виде рекурсии.
        createXmlExamTheme(xmlSerializer, examTree.getRootElement());

        xmlSerializer.flush();
        String dataXml = stringWriter.toString();

        return dataXml;
    }

    /**
     * Парсинг отдельной темы экзамена (рекурсия!).
     * @param xmlSerializer {@link XmlSerializer}
     * @param parentExamTheme тема экзамена.
     * @throws IOException
     */
    private static void createXmlExamTheme(XmlSerializer xmlSerializer,
                                      NavigationTree.Node<ExamThemeData> parentExamTheme)
            throws IOException {

        xmlSerializer.startTag(null, THEME_TAG);
        xmlSerializer.attribute(null, NAME_ATTR, parentExamTheme.getData().getName());

        // Если нет детей, то значит конечная тема.
        if (parentExamTheme.getChildren().isEmpty())
            xmlSerializer.attribute(null, SOURCE_ATTR, parentExamTheme.getData().getSource());

        // Рекурсия по составным темам входящей темы.
        for (NavigationTree.Node<ExamThemeData> examTheme : parentExamTheme.getChildren()) {
            createXmlExamTheme(xmlSerializer, examTheme);
        }

        xmlSerializer.endTag(null, THEME_TAG);
    }
}
