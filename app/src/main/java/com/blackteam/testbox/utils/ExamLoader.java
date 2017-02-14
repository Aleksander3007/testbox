package com.blackteam.testbox.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Xml;

import com.blackteam.testbox.ExamThemeData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Управляет загрузкой и сохранением данных об экзаменах.
 */
public class ExamLoader {
    private static String sExamFileName = "exam.xml";
    private static final String sSdDir = "testbox";

    private static final String sThemeTag = "theme";

    private static final String sNameAttr = "name";
    private static final String sIdAttr = "id";
    private static final String IS_TEST_ATTR = "isTest";

    /**
     * Загрузить данные об экзаменах.
     * @param context Контекст приложения.
     * @throws IOException, XmlPullParserException
     */
    public static NavigationTree<ExamThemeData> loadExam(Context context)
            throws IOException, XmlPullParserException {
        try {
            FileInputStream fileInputStream = context.openFileInput(sExamFileName);
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
     * Сохранение на SD-карту.
     * @param examTree дерево с данными об экзаменах.
     * @return true - если успешно завершено, false - скорее всего отсутсвует SD карта.
     * @throws IOException
     */
    public static boolean saveToSdCard(NavigationTree<ExamThemeData> examTree) throws IOException {
        // Если SD-карта есть.
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Получаем путь к SD.
            File backupFile = getSdExamFile();
            if (backupFile != null) {
                if (backupFile.exists()) backupFile.delete();
                backupFile.createNewFile();
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(backupFile));
                String dataWrite = createXmlData(examTree);
                bufferedWriter.write(dataWrite);
                bufferedWriter.close();
                return true;
            }
            else
                return false;
        }
        else
            return false;
    }

    /**
     * Чтение с SD-карты.
     * @return дерево с данными об экзаменах.
     * @throws IOException
     * @throws XmlPullParserException
     */
    @Nullable
    public static NavigationTree<ExamThemeData> loadFromSdCard()
            throws IOException, XmlPullParserException {
        // Если SD-карта есть.
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Получаем путь к SD.
            File recoveryFile = getSdExamFile();
            if (recoveryFile != null) {
                // открываем поток для чтения
                BufferedReader bufferedReader = new BufferedReader(new FileReader(recoveryFile));
                String str;
                StringBuilder examDataStr = new StringBuilder();
                // читаем содержимое
                while ((str = bufferedReader.readLine()) != null) {
                    examDataStr.append(str);
                }
                // Парсим строку как xml.
                XmlPullParser xmlParser = parseXml(examDataStr.toString());

                return parseExamTree(xmlParser);
            }
            else
                return null;
        }
        else
            return null;
    }

    /**
     * Получаем доступ к файлу (c экзам. данными) на SD.
     * @return файл.
     */
    private static File getSdExamFile() {
        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/" + sSdDir);
        sdPath.mkdirs();
        File sdFile = new File(sdPath, sExamFileName);
        return sdFile;
    }

    /**
     * Установка режима unit-тестирования данного класса, чтобы не перезаписывался основной файл.
     */
    public static void setUnitTestMode() {
        sExamFileName = "__examUnitTest.xml";
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
            FileOutputStream fileOutputStream = context.openFileOutput(sExamFileName, Context.MODE_PRIVATE);
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

        // Парсим строку как xml.
        XmlPullParser xmlParser = parseXml(data);

        return parseExamTree(xmlParser);
    }

    private static NavigationTree<ExamThemeData> parseExamTree(XmlPullParser xmlParser)
            throws XmlPullParserException, IOException {

        NavigationTree<ExamThemeData> examTree = new NavigationTree<>();

        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                // Движемся вперед, пока не дойдем до конечной темы.
                case XmlPullParser.START_TAG:
                    // Считываем все данные по экзамеционной теме.
                    String examName = xmlParser.getAttributeValue(null, sNameAttr);
                    String examId = xmlParser.getAttributeValue(null, sIdAttr);
                    boolean isExamTest = Boolean.parseBoolean(xmlParser.getAttributeValue(null, IS_TEST_ATTR));
                    ExamThemeData examThemeData = new ExamThemeData(examName, examId, isExamTest);

                    if (examTree.getRootElement() != null) {
                        examTree.getCurElem().addChild(examThemeData);
                        examTree.next(examThemeData);
                    }
                    else {
                        examTree.createRootElement(examThemeData);
                    }
                    break;
                case XmlPullParser.END_TAG:
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
     * Парсит строку как xml файл.
     * @param xmlStr строка.
     * @return xmlParser.
     */
    private static XmlPullParser parseXml(String xmlStr)
            throws XmlPullParserException {
        XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
        xmlFactory.setNamespaceAware(true);
        XmlPullParser xmlParser = xmlFactory.newPullParser();
        xmlParser.setInput(new StringReader(xmlStr));
        return xmlParser;
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

        xmlSerializer.startTag(null, sThemeTag);
        xmlSerializer.attribute(null, sNameAttr, parentExamTheme.getData().getName());
        xmlSerializer.attribute(null, sIdAttr, parentExamTheme.getData().getId());
        xmlSerializer.attribute(null, IS_TEST_ATTR,
                String.valueOf(parentExamTheme.getData().containsTest()));

        // Рекурсия по составным темам входящей темы.
        for (NavigationTree.Node<ExamThemeData> examTheme : parentExamTheme.getChildren()) {
            createXmlExamTheme(xmlSerializer, examTheme);
        }

        xmlSerializer.endTag(null, sThemeTag);
    }
}
