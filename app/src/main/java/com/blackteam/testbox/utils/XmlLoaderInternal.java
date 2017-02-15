package com.blackteam.testbox.utils;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Загрузчик xml-данных из внутренней памяти приложения.
 * Позволяет сохранять и загружать данные xml-формата во внутреннею память приложения.
 */
public class XmlLoaderInternal extends XmlLoader {
    /**
     * @param fileName имя файла, из которого необходимо загрузить данные.
     */
    @Override
    public boolean load(Context context, String fileName, XmlParceable xmlParceableObject)
            throws IOException, XmlPullParserException {
        FileInputStream fileInputStream = context.openFileInput(fileName);
        XmlPullParser xmlParser = readFile(fileInputStream);
        fileInputStream.close();
        xmlParceableObject.parseData(xmlParser);
        return true;
    }

    /**
     * @param fileName имя файла, в который необходимо сохранить данные.
     */
    @Override
    public boolean save(Context context, String fileName, XmlParceable xmlParceableObject) throws IOException {
        try {
            String dataWrite = xmlParceableObject.parseXmlString();
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(dataWrite.getBytes());
            fileOutputStream.close();
            return true;

        } catch (FileNotFoundException fnfex) {
            // Такого быть не может, т.к. если файла не существует, openFileOutput() создаст файл.
            fnfex.printStackTrace();
            return false;
        }
    }

    /**
     * Чтение данных из файла.
     * @param fileInputStream входной поток данных.
     * @return прочитанные данные.
     * @throws IOException
     * @throws XmlPullParserException
     */
    private static XmlPullParser readFile(FileInputStream fileInputStream)
            throws IOException, XmlPullParserException {
        // Открываем reader и считываем всё в строку.
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        char[] inputBuffer = new char[fileInputStream.available()];
        inputStreamReader.read(inputBuffer);
        String data = new String(inputBuffer);
        inputStreamReader.close();
        // Парсим строку как xml.
        return parseXml(data);
    }
}
