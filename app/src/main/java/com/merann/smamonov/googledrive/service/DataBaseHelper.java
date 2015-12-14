package com.merann.smamonov.googledrive.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.merann.smamonov.googledrive.model.Image;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by samam_000 on 15.12.2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    static private final String LOG_TAG = "DataBaseHelper";
    static private final String DATABASE_NAME = "Images";
    static private final String FILE_NAME_FIELD_NAME = "file_name";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        Log.d(LOG_TAG, "DataBaseHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_TAG, "onCreate");
        db.execSQL("create table +"
                + DATABASE_NAME
                + " ("
                + "id integer primary key autoincrement,"
                + FILE_NAME_FIELD_NAME
                + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long add(Image image) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FILE_NAME_FIELD_NAME, image.getFileName());
        return getWritableDatabase().insert(DATABASE_NAME, null, contentValues);
    }

    public List<Image> getImagesFromDb() {

        List<Image> result = new LinkedList();

        Cursor cursor = getReadableDatabase().query(DATABASE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            int idColumnName = cursor.getColumnIndex(FILE_NAME_FIELD_NAME);
            do {
                Image image = new Image(cursor.getString(idColumnName));
                result.add(image);
            } while (cursor.moveToNext());
        }

        return result;
    }
}
