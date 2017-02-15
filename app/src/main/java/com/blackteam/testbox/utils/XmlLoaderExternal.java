package com.blackteam.testbox.utils;

import android.content.Context;
import android.os.Environment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Загрузчик xml-данных из внешней памяти (SD-карта).
 * Позволяет сохранять и загружать данные xml-формата во внешнею память (SD-карта).
 */
public class XmlLoaderExternal extends XmlLoader {

    private final String mDirName;

    /**
     * Конструктор.
     * @param dirName Имя папки куда будет сохранен файл.
     */
    public XmlLoaderExternal(String dirName) {
        super();
        mDirName = dirName;
    }

    /**
     * @param fileName имя файла, в который необходимо сохранить данные.
     */
    @Override
    public boolean load(Context context, String fileName, XmlParceable xmlParceableObject)
            throws IOException, XmlPullParserException {
        // Если SD-карта есть.
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Получаем путь к SD.
            File recoveryFile = getFile(fileName, mDirName);
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

                xmlParceableObject.parseData(xmlParser);
                return true;
            }
            else
                return false;
        }
        else
            return false;
    }

    /**
     * @param fileName имя файла, в который необходимо сохранить данные.
     */
    @Override
    public boolean save(Context context, String fileName, XmlParceable xmlParceableObject)
            throws IOException {
        // Если SD-карта есть.
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Получаем путь к SD.
            File backupFile = getFile(fileName, mDirName);
            if (backupFile != null) {
                if (backupFile.exists()) backupFile.delete();
                backupFile.createNewFile();
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(backupFile));
                String dataWrite = xmlParceableObject.parseXmlString();
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
     * Получаем доступ к файлу (c экзам. данными) на SD.
     * @return файл.
     */
    private static File getFile(String mFileName, String mDirName) {
        File sdPath = Environment.getExternalStorageDirectory();
        sdPath = new File(sdPath.getAbsolutePath() + "/" + mDirName);
        sdPath.mkdirs();
        File sdFile = new File(sdPath, mFileName);
        return sdFile;
    }
}
