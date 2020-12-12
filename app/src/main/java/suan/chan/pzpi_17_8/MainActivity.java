package suan.chan.pzpi_17_8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.security.Provider;
import java.util.ArrayList;

import suan.chan.pzpi_17_8.entity.Note;

public class MainActivity extends AppCompatActivity {

    EditText searchBox;
    ImageView searchIcon;
    ImageView filterIcon;
    Button addNote;
    AppViewModel viewModel;
    RecyclerView noteList;
    NoteListAdapter noteListAdapter;
    ItemOnClickHandlers onClickHandlers;
    ArrayList<Note> notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBox = findViewById(R.id.search_text_edit);
        searchIcon = findViewById(R.id.search_icon);
        filterIcon = findViewById(R.id.filter_priority_icon);
        addNote = findViewById(R.id.add_new_note);
        noteList = findViewById(R.id.note_list);

        viewModel = new ViewModelProvider(this).get(AppViewModel.class);

        initNotesRecyclerView();

        viewModel.getNotes().observe(this, new Observer<ArrayList<Note>>() {
            @Override
            public void onChanged(ArrayList<Note> notes) {
                notesList = notes;
                noteListAdapter.setNotes(notes);
            }
        });

        onClickHandlers = new ItemOnClickHandlers() {
            @Override
            public void itemOnClick(int itemIndex) {
                Toast.makeText(getApplicationContext(), "Click on " + notesList.get(itemIndex).getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void itemOnLongClick(int itemIndex) {
                Toast.makeText(getApplicationContext(), "Long click on " + notesList.get(itemIndex).getTitle(), Toast.LENGTH_SHORT).show();
            }
        };

        noteListAdapter.setClickHandler(onClickHandlers);
    }

    private void initNotesRecyclerView(){
        //noteList.setLayoutManager(new LinearLayoutManager(this));
        noteListAdapter = new NoteListAdapter();
        noteList.setAdapter(noteListAdapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        noteList.addItemDecoration(itemDecoration);
    }


}