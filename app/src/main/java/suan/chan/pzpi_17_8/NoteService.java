package suan.chan.pzpi_17_8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import suan.chan.pzpi_17_8.entity.Note;
import suan.chan.pzpi_17_8.entity.PriorityType;
import suan.chan.pzpi_17_8.util.DbBitmapUtility;
import suan.chan.pzpi_17_8.util.NotesDbHelperV2;

public class NoteService {

    Context appContext;

    public NoteService(Context appContext) {
        this.appContext = appContext;
    }

    public ArrayList<Note> getNotesForList() {
        ArrayList<Note> notes = new ArrayList<>();
        NotesDbHelperV2 dbHelper = new NotesDbHelperV2(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try (Cursor c = db.query(NotesDbHelperV2.NOTES_TABLE, null, null, null, null, null, null)) {
            if (c.moveToFirst()) {
                int idColIndex = c.getColumnIndex(NotesDbHelperV2.ID_FIELD);
                int titleColIndex = c.getColumnIndex(NotesDbHelperV2.TITLE_FIELD);
                int descriptionColIndex = c.getColumnIndex(NotesDbHelperV2.DESCRIPTION_FIELD);
                int dateColIndex = c.getColumnIndex(NotesDbHelperV2.DATE_FIELD);
                int priorityColIndex = c.getColumnIndex(NotesDbHelperV2.PRIORITY_FIELD);
                int imageColIndex = c.getColumnIndex(NotesDbHelperV2.IMAGE_FIELD);
                do {
                    Note n = new Note();
                    n.setId((long) c.getInt(idColIndex));
                    n.setTitle(c.getString(titleColIndex));
                    n.setDescription(c.getString(descriptionColIndex));
                    n.setEditTime(new Date(c.getLong(dateColIndex)));
                    n.setPriority(PriorityType.values()[c.getInt(priorityColIndex)]);
                    n.setImage(DbBitmapUtility.getImage(c.getBlob(imageColIndex)));
                    notes.add(n);
                } while (c.moveToNext());
            }
        }
        dbHelper.close();
        return notes;
    }

    public void AddNewNote(Note note) {
        NotesDbHelperV2 dbHelper = new NotesDbHelperV2(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NotesDbHelperV2.TITLE_FIELD, note.getTitle());
        cv.put(NotesDbHelperV2.DESCRIPTION_FIELD, note.getDescription());
        cv.put(NotesDbHelperV2.PRIORITY_FIELD, note.getPriority().getValue());
        cv.put(NotesDbHelperV2.DATE_FIELD, System.currentTimeMillis());
        cv.put(NotesDbHelperV2.IMAGE_FIELD, DbBitmapUtility.getBytes(note.getImage()));
        cv.put(NotesDbHelperV2.TEXT_FIELD, note.getText());
        final long insert = db.insert(NotesDbHelperV2.NOTES_TABLE, null, cv);
        dbHelper.close();
    }

    public Note getNote(long id) {
        Note n = null;
        NotesDbHelperV2 dbHelper = new NotesDbHelperV2(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try (Cursor c = db.query(NotesDbHelperV2.NOTES_TABLE, null, "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            int idColIndex = c.getColumnIndex(NotesDbHelperV2.ID_FIELD);
            int titleColIndex = c.getColumnIndex(NotesDbHelperV2.TITLE_FIELD);
            int descriptionColIndex = c.getColumnIndex(NotesDbHelperV2.DESCRIPTION_FIELD);
            int dateColIndex = c.getColumnIndex(NotesDbHelperV2.DATE_FIELD);
            int priorityColIndex = c.getColumnIndex(NotesDbHelperV2.PRIORITY_FIELD);
            int textColIndex = c.getColumnIndex(NotesDbHelperV2.TEXT_FIELD);
            int imageColIndex = c.getColumnIndex(NotesDbHelperV2.IMAGE_FIELD);
            if (c.moveToFirst()) {
                n = new Note();
                n.setId((long) c.getInt(idColIndex));
                n.setTitle(c.getString(titleColIndex));
                n.setDescription(c.getString(descriptionColIndex));
                n.setEditTime(new Date(c.getLong(dateColIndex)));
                n.setPriority(PriorityType.values()[c.getInt(priorityColIndex)]);
                n.setImage(DbBitmapUtility.getImage(c.getBlob(imageColIndex)));
                n.setText(c.getString(textColIndex));
            }
        }
        dbHelper.close();
        return n;
    }

    public boolean isNoteFileContainedString(long noteId, String string){
        boolean res = false;
        Note n = null;
        NotesDbHelperV2 dbHelper = new NotesDbHelperV2(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try (Cursor c = db.query(NotesDbHelperV2.NOTES_TABLE, new String[]{NotesDbHelperV2.TITLE_FIELD, NotesDbHelperV2.DESCRIPTION_FIELD, NotesDbHelperV2.TEXT_FIELD}, "id = ?", new String[]{String.valueOf(noteId)}, null, null, null)) {
            int titleColIndex = c.getColumnIndex(NotesDbHelperV2.TITLE_FIELD);
            int descriptionColIndex = c.getColumnIndex(NotesDbHelperV2.DESCRIPTION_FIELD);
            int textColIndex = c.getColumnIndex(NotesDbHelperV2.TEXT_FIELD);
            if (c.moveToFirst()) {
                if(c.getString(titleColIndex).contains(string) || c.getString(descriptionColIndex).contains(string) || c.getString(textColIndex).contains(string)){
                    res = true;
                }
            }
        }
        dbHelper.close();
        return res;
    }

    public void deleteNote(long id) {
        NotesDbHelperV2 dbHelper = new NotesDbHelperV2(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int delete = db.delete(NotesDbHelperV2.NOTES_TABLE, "id = ?", new String[]{String.valueOf(id)});
        dbHelper.close();
    }

    public void updateNote(Note note) {
        NotesDbHelperV2 dbHelper = new NotesDbHelperV2(appContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NotesDbHelperV2.TITLE_FIELD, note.getTitle());
        cv.put(NotesDbHelperV2.DESCRIPTION_FIELD, note.getDescription());
        cv.put(NotesDbHelperV2.PRIORITY_FIELD, note.getPriority().getValue());
        cv.put(NotesDbHelperV2.DATE_FIELD, System.currentTimeMillis());
        cv.put(NotesDbHelperV2.IMAGE_FIELD, DbBitmapUtility.getBytes(note.getImage()));
        cv.put(NotesDbHelperV2.TEXT_FIELD, note.getText());
        int update = db.update(NotesDbHelperV2.NOTES_TABLE, cv, "id = ?", new String[]{String.valueOf(note.getId())});
        dbHelper.close();
    }

}
