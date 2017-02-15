package com.blackteam.testbox;

import android.util.Xml;

import com.blackteam.testbox.utils.NavigationTree;
import com.blackteam.testbox.utils.XmlParceable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Класс описывает информацию об экзамене, существующих темах экзамена.
 */
public class Exam implements XmlParceable {

    private static final String sDefaultExamFileName = "exam.xml";

    private static final String sTagTheme = "theme";

    private static final String sAttrName = "name";
    private static final String sAttrId = "id";
    private static final String sAttrIsTest = "isTest";

    // Параметры для root элемента дерева, содержащего экзамен.
    private static final String sExamRootStr = "Экзамен";
    private static final String sExamRootId = "0";
    private static final boolean sIsExamRootTest = false;

    private NavigationTree<ExamThemeData> mExamTree;
    private String mFileName = sDefaultExamFileName;

    public String getFileName() {
        return mFileName;
    }

    public NavigationTree<ExamThemeData> getData() {
        return mExamTree;
    }

    /**
     * Конструктор.
     */
    public Exam() {
        init();
    }

    /**
     * Конструктор.
     * @param examTree экзам. дерево.
     */
    public Exam(NavigationTree<ExamThemeData> examTree) {
        if (examTree != null)
            mExamTree = examTree;
        else
            init();
    }

    @Override
    public void parseData(XmlPullParser xmlParser) throws XmlPullParserException, IOException {
        mExamTree = new NavigationTree<>();

        int eventType = xmlParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                // Движемся вперед, пока не дойдем до конечной темы.
                case XmlPullParser.START_TAG:
                    // Считываем все данные по экзамеционной теме.
                    String examName = xmlParser.getAttributeValue(null, sAttrName);
                    String examId = xmlParser.getAttributeValue(null, sAttrId);
                    boolean isExamTest = Boolean.parseBoolean(xmlParser.getAttributeValue(null, sAttrIsTest));
                    ExamThemeData examThemeData = new ExamThemeData(examName, examId, isExamTest);

                    if (mExamTree.getRootElement() != null) {
                        mExamTree.getCurElem().addChild(examThemeData);
                        mExamTree.next(examThemeData);
                    }
                    else {
                        mExamTree.createRootElement(examThemeData);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    // Возращаемся на уровень назад.
                    mExamTree.prev();
                    break;
                default:
                    // Остальные типы тегов нас не интересуют.
                    break;
            }

            eventType = xmlParser.next();
        }

        if (mExamTree == null) init();
    }

    @Override
    public String parseXmlString() throws IOException {
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
        createXmlExamTheme(xmlSerializer, mExamTree.getRootElement());

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

        xmlSerializer.startTag(null, sTagTheme);
        xmlSerializer.attribute(null, sAttrName, parentExamTheme.getData().getName());
        xmlSerializer.attribute(null, sAttrId, parentExamTheme.getData().getId());
        xmlSerializer.attribute(null, sAttrIsTest,
                String.valueOf(parentExamTheme.getData().containsTest()));

        // Рекурсия по составным темам входящей темы.
        for (NavigationTree.Node<ExamThemeData> examTheme : parentExamTheme.getChildren()) {
            createXmlExamTheme(xmlSerializer, examTheme);
        }

        xmlSerializer.endTag(null, sTagTheme);
    }

    /**
     * Инициализация экзамена.
     */
    public void init() {
        mExamTree = new NavigationTree<>();
        mExamTree.createRootElement(
                new ExamThemeData(sExamRootStr, sExamRootId, sIsExamRootTest));
    }

    /**
     * Установка режима unit-тестирования данного класса, чтобы не перезаписывался основной файл.
     */
    public void setUnitTestMode() {
        mFileName = "__examUnitTest.xml";
    }
}
