package com.blackteam.testbox.utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Интерфейс для классов которые будут записывать и читать свои данные в xml формате.
 */
public interface XmlParceable {
    /**
     * Парсинг xml-данных как элемент.
     * @param xmlParser xml-парсер.
     */
    void parseData(XmlPullParser xmlParser) throws XmlPullParserException, IOException;
    /**
     * Парсинг элемента как строку в виде xml.
     */
    String parseXmlString() throws IOException;
}
