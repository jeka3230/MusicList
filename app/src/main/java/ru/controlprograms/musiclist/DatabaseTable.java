package ru.controlprograms.musiclist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by evgeny on 23.04.16.
 */
//База данных для поиска информации.
public class DatabaseTable {
    private static final String TAG = "DictionaryDatabase";

//  Имена колонок таблицы
    public static final String COL_WORD = "WORD";
    public static final String COL_DEFINITION = "DEFINITION";

    private static final String DATABASE_NAME = "DICTIONARY";
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 1;


    private final DatabaseOpenHelper mDatabaseOpenHelper;

    public DatabaseTable(Context context) {
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        private final Context mHelperContext;
        private SQLiteDatabase mDatabase;
//          Создание таблицы в бд.
        private static final String FTS_TABLE_CREATE =
                "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                        " USING fts3 (" +
                        COL_WORD + ", " +
                        COL_DEFINITION + ")";

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;
            mDatabase.execSQL(FTS_TABLE_CREATE);
//            Заполнить бд.
            loadDictionary();

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(db);
        }

        private void loadDictionary() {
            new Thread(new Runnable() {
                public void run() {
                    loadFromJSON();
                }
            }).start();
        }

        private void loadFromJSON() {
            JSONArray artists;
            try {
                IOJSON iojson = new IOJSON(mHelperContext);
                artists = new JSONArray(iojson.loadJSON());
                int artistsLen = artists.length();
//                Загрузить данные в таблицу FTS.
                for (int i = 0; i < artistsLen; i++) {
                    JSONObject artist = artists.getJSONObject(i);
                    ContentValues initialValues = new ContentValues();
                    GetFromJSON getFromJSON = new GetFromJSON(artist);
                    String artistName = getFromJSON.getName();
//                    Поле с именем исполнителя.
                    initialValues.put(COL_WORD, artistName);
//                    Поле с номером исполнителя в списке.
                    initialValues.put(COL_DEFINITION, i);

                    long result = mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);

                    if (result < 0) {
                        Log.e(TAG, "unable to add artist: " + artistName);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public Cursor getWordMatches(String query, String[] columns) {
//        Выбрать все слова, начинающеися на "запрос".
        String selection = COL_WORD + " MATCH ? ";
        String[] selectionArgs = new String[] {query+"*"};
        return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }


}