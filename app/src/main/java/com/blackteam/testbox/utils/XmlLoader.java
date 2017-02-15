package com.blackteam.testbox.utils;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Загрузчик xml-данных. Позволяет сохранять и загружать данные xml-формата.
 */
public abstract class XmlLoader {

    /**
     * Загрузка данных.
     * @param context контекст.
     * @param srcName имя источника, из которого необходимо загрузить данные.
     * @param xmlParceableObject объект куда необходимо загрузить данные.
     * @return true - если данные успешно загружены.
     * @throws IOException
     * @throws XmlPullParserException
     */
    public abstract boolean load(Context context, String srcName, XmlParceable xmlParceableObject)
            throws IOException, XmlPullParserException;

    /**
     * Сохранение данных.
     * @param context контекст.
     * @param srcName имя источника, в который необходимо сохранить данные.
     * @param xmlParceableObject объект который содержит сохраняемые данные.
     * @return true - если данные успешно сохранены.
     * @throws IOException
     */
    public abstract boolean save(Context context, String srcName, XmlParceable xmlParceableObject)
            throws IOException;

    /**
     * Парсит строку как xml файл.
     * @param xmlStr строка.
     * @return xmlParser.
     */
    protected static XmlPullParser parseXml(String xmlStr)
            throws XmlPullParserException {
        XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
        xmlFactory.setNamespaceAware(true);
        XmlPullParser xmlParser = xmlFactory.newPullParser();
        xmlParser.setInput(new StringReader(xmlStr));
        return xmlParser;
    }
}
