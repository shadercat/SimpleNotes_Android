package suan.chan.pzpi_17_8;

import android.app.Application;
import android.os.Handler;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import suan.chan.pzpi_17_8.entity.Note;
import suan.chan.pzpi_17_8.entity.PriorityType;
import suan.chan.pzpi_17_8.util.mWorkingThread;

public class AppViewModel extends AndroidViewModel {

    MutableLiveData<PriorityType> livePriorityFilter;
    ArrayList<Note> notes;
    ArrayList<Note> notesProjection;
    MutableLiveData<ArrayList<Note>> liveNotes;
    mWorkingThread parallelThread;
    Handler ctxHandler = new Handler();

    public AppViewModel(Application application){
        super(application);
        parallelThread = new mWorkingThread("parallelThread");
        parallelThread.start();
        parallelThread.prepareHandler();
    }

    public LiveData<ArrayList<Note>> getNotesProjection(){
        if(liveNotes == null){
            liveNotes = new MutableLiveData<>();
            loadNotes();
        }
        return liveNotes;
    }

    public LiveData<PriorityType> getCurrentPriorityFilter(){
        if(livePriorityFilter == null){
            livePriorityFilter = new MutableLiveData<>();
        }
        return livePriorityFilter;
    }

    private final Runnable loadingNotes = new Runnable() {
        @Override
        public void run() {
            NoteService service = new NoteService(getApplication().getApplicationContext());
            final ArrayList<Note> n = service.getNotesForList();
            ctxHandler.post(new Runnable() {
                @Override
                public void run() {
                    notes = n;
                    notesProjection = new ArrayList<>(n);
                    liveNotes.postValue(notesProjection);
                }
            });
        }
    };

    private void loadNotes(){
        if(notesProjection == null){
            parallelThread.postTask(loadingNotes);
        }
    }

    public void deleteNote(int noteIndex){
        NoteService service = new NoteService(getApplication().getApplicationContext());
        service.deleteNote(notesProjection.get(noteIndex).getId());
        notesProjection.remove(noteIndex);
        liveNotes.postValue(notesProjection);
    }

    public void refreshNotes(){
        liveNotes.postValue(notesProjection);
    }

    public void reloadNotes(){
        notes.clear();
        notesProjection.clear();
        liveNotes.postValue(notesProjection);
        livePriorityFilter.postValue(null);
        parallelThread.postTask(loadingNotes);
    }

    public void priorityFiltering(PriorityType priorityType){
        livePriorityFilter.postValue(priorityType);
        notesProjection.clear();
        for (Note n : notes) {
            if(n.getPriority() == priorityType){
                notesProjection.add(n);
            }
        }
        liveNotes.postValue(notesProjection);
    }

    public void clearPriorityFilter(){
        livePriorityFilter.postValue(null);
        notesProjection.clear();
        notesProjection.addAll(notes);
        liveNotes.postValue(notesProjection);
    }

    @Override
    protected void onCleared() {
        if (parallelThread != null){
            parallelThread.quit();
        }
        super.onCleared();
    }
}
