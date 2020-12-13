package suan.chan.pzpi_17_8.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class NotesDbHelper extends SQLiteOpenHelper {

    private static final String NOTES_LIST = "Notes_List";

    public NotesDbHelper(Context context){
        super(context, NOTES_LIST, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table notes(" +
                "id integer primary key autoincrement," +
                "title text," +
                "description text," +
                "date number," +
                "priority integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
