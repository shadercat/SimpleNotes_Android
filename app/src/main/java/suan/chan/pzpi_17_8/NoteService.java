package suan.chan.pzpi_17_8;

import java.util.ArrayList;
import java.util.Date;

import suan.chan.pzpi_17_8.entity.Note;
import suan.chan.pzpi_17_8.entity.PriorityType;

public class NoteService {
    public ArrayList<Note> getNotes(){
        ArrayList<Note> notes = new ArrayList<>();
        for (int i = 0; i < 40; i++){
            notes.add(new Note((long) i, "Note " + i, "Some description", PriorityType.First, new Date(System.currentTimeMillis())));
        }
        return notes;
    }
}
