package suan.chan.pzpi_17_8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

import suan.chan.pzpi_17_8.entity.Note;
import suan.chan.pzpi_17_8.entity.PriorityType;
import suan.chan.pzpi_17_8.util.NotesDbHelper;

public class NoteService {
    private static final String ROOT_NOTES = "notes";
    private static final String ROOT_IMAGE = "images";

    Context appContext;

    public NoteService(Context appContext) {
        this.appContext = appContext;
    }

    public ArrayList<Note> getNotesForList() {
        ArrayList<Note> notes = new ArrayList<>();
        NotesDbHelper dbHelper = new NotesDbHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try (Cursor c = db.query(NotesDbHelper.NOTES_TABLE, null, null, null, null, null, null)) {
            if (c.moveToFirst()) {
                int idColIndex = c.getColumnIndex(NotesDbHelper.ID_FIELD);
                int titleColIndex = c.getColumnIndex(NotesDbHelper.TITLE_FIELD);
                int descriptionColIndex = c.getColumnIndex(NotesDbHelper.DESCRIPTION_FIELD);
                int dateColIndex = c.getColumnIndex(NotesDbHelper.DATE_FIELD);
                int priorityColIndex = c.getColumnIndex(NotesDbHelper.PRIORITY_FIELD);
                do {
                    Note n = new Note();
                    n.setId((long) c.getInt(idColIndex));
                    n.setTitle(c.getString(titleColIndex));
                    n.setDescription(c.getString(descriptionColIndex));
                    n.setEditTime(new Date(c.getLong(dateColIndex)));
                    n.setPriority(PriorityType.values()[c.getInt(priorityColIndex)]);
                    n.setImage(BitmapFactory.decodeResource(appContext.getResources(), R.drawable.landscape_sample));
                    notes.add(n);
                } while (c.moveToNext());
            }
            c.close();
        }
        dbHelper.close();
        return notes;
    }

    public void AddNewNote(Note note) {
        NotesDbHelper dbHelper = new NotesDbHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NotesDbHelper.TITLE_FIELD, note.getTitle());
        cv.put(NotesDbHelper.DESCRIPTION_FIELD, note.getDescription());
        cv.put(NotesDbHelper.PRIORITY_FIELD, note.getPriority().getValue());
        cv.put(NotesDbHelper.DATE_FIELD, System.currentTimeMillis());
        final long insert = db.insert(NotesDbHelper.NOTES_TABLE, null, cv);
        dbHelper.close();
        if (insert != 0) {
            note.setId(insert);
            saveText(note);
        }
    }

    public Note getNote(long id) {
        Note n = null;
        NotesDbHelper dbHelper = new NotesDbHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try (Cursor c = db.query(NotesDbHelper.NOTES_TABLE, null, "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            int idColIndex = c.getColumnIndex(NotesDbHelper.ID_FIELD);
            int titleColIndex = c.getColumnIndex(NotesDbHelper.TITLE_FIELD);
            int descriptionColIndex = c.getColumnIndex(NotesDbHelper.DESCRIPTION_FIELD);
            int dateColIndex = c.getColumnIndex(NotesDbHelper.DATE_FIELD);
            int priorityColIndex = c.getColumnIndex(NotesDbHelper.PRIORITY_FIELD);
            if (c.moveToFirst()) {
                n = new Note();
                n.setId((long) c.getInt(idColIndex));
                n.setTitle(c.getString(titleColIndex));
                n.setDescription(c.getString(descriptionColIndex));
                n.setEditTime(new Date(c.getLong(dateColIndex)));
                n.setPriority(PriorityType.values()[c.getInt(priorityColIndex)]);
                n.setImage(BitmapFactory.decodeResource(appContext.getResources(), R.drawable.landscape_sample));
                String text = getText(n.getId());
                if (text != null) {
                    n.setText(text);
                }
            }
            c.close();
        }
        dbHelper.close();
        return n;
    }

    public void deleteNote(long id) {
        NotesDbHelper dbHelper = new NotesDbHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int delete = db.delete(NotesDbHelper.NOTES_TABLE, "id = ?", new String[]{String.valueOf(id)});
        dbHelper.close();
        if (delete != 0) {
            deleteText(id);
        }
    }

    public void updateNote(Note note) {
        NotesDbHelper dbHelper = new NotesDbHelper(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NotesDbHelper.TITLE_FIELD, note.getTitle());
        cv.put(NotesDbHelper.DESCRIPTION_FIELD, note.getDescription());
        cv.put(NotesDbHelper.PRIORITY_FIELD, note.getPriority().getValue());
        cv.put(NotesDbHelper.DATE_FIELD, System.currentTimeMillis());
        int update = db.update(NotesDbHelper.NOTES_TABLE, cv, "id = ?", new String[]{String.valueOf(note.getId())});
        dbHelper.close();
        if (update != 0) {
            saveText(note);
        }
    }

    public String getText(long noteId) {
        String text = null;
        FileInputStream fin = null;
        try {
            File resDir = appContext.getDir(ROOT_NOTES, Context.MODE_PRIVATE);
            File resFile = new File(resDir, noteId + ".txt");
            if(resFile.exists()){
                fin = new FileInputStream(resFile);
                byte[] bytes = new byte[fin.available()];
                fin.read(bytes);
                text = new String(bytes);
            }
        } catch (IOException ignored){

        }
        finally {
            try {
                if(fin != null){
                    fin.close();
                }
            } catch (IOException ignored){

            }
        }
        return text;
    }

    public void saveText(Note note) {
        try {
            File resDir = appContext.getDir(ROOT_NOTES, Context.MODE_PRIVATE);
            File resFile = new File(resDir, note.getId() + ".txt");
            if (!resFile.exists()) {
                final boolean newFile = resFile.createNewFile();
                if (newFile) {
                    FileWriter fw = new FileWriter(resFile);
                    fw.write(note.getText());
                    fw.flush();
                    fw.close();
                }
            } else {
                FileWriter fw = new FileWriter(resFile);
                fw.write(note.getText());
                fw.flush();
                fw.close();
            }
        } catch (IOException ignored) {

        }
    }

    public void deleteText(long noteId) {
        File resDir = appContext.getDir(ROOT_NOTES, Context.MODE_PRIVATE);
        File resFile = new File(resDir, noteId + ".txt");
        if (resFile.exists()) {
            final boolean delete = resFile.delete();
        }
    }
}
