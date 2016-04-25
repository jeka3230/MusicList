package ru.controlprograms.musiclist;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by evgeny on 24.04.16.
 */
public class IOJSON {
    Context mContext;
    public IOJSON(Context context) {
        mContext = context;
    }
//    Считывание jsonArray с диска.
    public String loadJSON() {
        String filename = mContext.getResources().getString(R.string.filename);
        File file = new File(mContext.getFilesDir() +"/"+filename);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
//Запись jsonarray на диск.
    public void writeJSONArray(String JSONResponse) {
        String filename = mContext.getResources().getString(R.string.filename);
        try {
            FileWriter fileWriter = new FileWriter(mContext.getFilesDir() + "/"+ filename);
            fileWriter.write(JSONResponse);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
