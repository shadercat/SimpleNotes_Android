package suan.chan.pzpi_17_8.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class NotesDbHelper extends SQLiteOpenHelper {

    public static final String NOTES_LIST = "notes_list";
    public static final String NOTES_TABLE = "notes";
    public static final String ID_FIELD = "id";
    public static final String TITLE_FIELD = "title";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String DATE_FIELD = "date";
    public static final String PRIORITY_FIELD = "priority";

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
