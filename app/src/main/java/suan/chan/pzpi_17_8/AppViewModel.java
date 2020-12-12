package suan.chan.pzpi_17_8;

import android.app.Application;
import android.os.Handler;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import suan.chan.pzpi_17_8.entity.Note;
import suan.chan.pzpi_17_8.util.mWorkingThread;

public class AppViewModel extends AndroidViewModel {

    ArrayList<Note> notes;
    MutableLiveData<ArrayList<Note>> liveNotes;
    mWorkingThread parallelThread;
    Handler ctxHandler = new Handler();

    public AppViewModel(Application application){
        super(application);
        parallelThread = new mWorkingThread("parallelThread");
        parallelThread.start();
        parallelThread.prepareHandler();
    }

    public LiveData<ArrayList<Note>> getNotes(){
        if(liveNotes == null){
            liveNotes = new MutableLiveData<>();
            loadNotes();
        }
        return liveNotes;
    }

    private Runnable loadingNotes = new Runnable() {
        @Override
        public void run() {
            NoteService service = new NoteService();
            final ArrayList<Note> n = service.getNotes();
            ctxHandler.post(new Runnable() {
                @Override
                public void run() {
                    notes = n;
                    liveNotes.postValue(notes);
                }
            });
        }
    };

    private void loadNotes(){
        if(notes == null){
            parallelThread.postTask(loadingNotes);
        }
    }

    @Override
    protected void onCleared() {
        if (parallelThread != null){
            parallelThread.quit();
        }
        super.onCleared();
    }
}
