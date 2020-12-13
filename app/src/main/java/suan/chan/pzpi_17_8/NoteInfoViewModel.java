package suan.chan.pzpi_17_8;

import android.app.Application;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import suan.chan.pzpi_17_8.entity.Note;
import suan.chan.pzpi_17_8.util.mWorkingThread;

public class NoteInfoViewModel extends AndroidViewModel {

    Note note;
    MutableLiveData<Note> liveNote;
    mWorkingThread parallelThread;
    Handler ctxHandler = new Handler();

    public NoteInfoViewModel(@NonNull Application application) {
        super(application);
        parallelThread = new mWorkingThread("parallelThread");
        parallelThread.start();
        parallelThread.prepareHandler();
        note = new Note();
    }

    public LiveData<Note> getLiveNote(){
        if(liveNote == null){
            liveNote = new MutableLiveData<>();
            liveNote.postValue(note);
        }
        return liveNote;
    }

    public void setNote(Note note){
        this.note = note;
        if(liveNote != null){
            liveNote.postValue(this.note);
        }
    }

    public void refreshNote(){
        liveNote.postValue(note);
    }

    public void loadNote(long noteId){
        if(note == null || note.getId() != noteId){
            parallelThread.postTask(parallelLoadingNote(noteId));
        }
    }

    private Runnable parallelLoadingNote(final long noteId){
        return new Runnable() {
            @Override
            public void run() {
                NoteService service = new NoteService(getApplication().getApplicationContext());
                final Note foundNote = service.getNote(noteId);
                ctxHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        note = foundNote;
                        liveNote.postValue(note);
                    }
                });
            }
        };
    }

    @Override
    protected void onCleared() {
        if (parallelThread != null){
            parallelThread.quit();
        }
        super.onCleared();
    }
}
