package suan.chan.pzpi_17_8;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Date;

import suan.chan.pzpi_17_8.entity.Note;
import suan.chan.pzpi_17_8.entity.PriorityType;

public class NoteService {
    Context appContext;

    public NoteService(Context appContext){
        this.appContext = appContext;
    }

    public ArrayList<Note> getNotes(){
        ArrayList<Note> notes = new ArrayList<>();
        for (int i = 0; i < 40; i++){
            Note n = new Note((long) i, "Note " + i, "Some description", PriorityType.First, new Date(System.currentTimeMillis()));
            n.setImage(BitmapFactory.decodeResource(appContext.getResources(), R.drawable.landscape_sample));
            notes.add(n);
        }
        return notes;
    }

    public Note getNote(long id){
        Note n = new Note(id, "Note Info" + id, "Some description", PriorityType.First, new Date(System.currentTimeMillis()));
        n.setImage(BitmapFactory.decodeResource(appContext.getResources(), R.drawable.landscape_sample));
        return n;
    }

    public void deleteNote(long id){

    }
}
