package suan.chan.pzpi_17_8;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import suan.chan.pzpi_17_8.entity.Note;
import suan.chan.pzpi_17_8.entity.PriorityType;

public class MainActivity extends AppCompatActivity {

    private static final int EDIT_ACTIVITY = 393;
    private static final int ADD_ACTIVITY = 620;

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

        viewModel.getNotesProjection().observe(this, new Observer<ArrayList<Note>>() {
            @Override
            public void onChanged(ArrayList<Note> notes) {
                notesList = notes;
                noteListAdapter.setNotes(notes);
            }
        });

        viewModel.getCurrentPriorityFilter().observe(this, new Observer<PriorityType>() {
            @Override
            public void onChanged(PriorityType priorityType) {
                if(priorityType == null){
                    filterIcon.setImageResource(R.drawable.ic_baseline_filter_none_24);
                    return;
                }
                switch (priorityType){

                    case First:
                        filterIcon.setImageResource(R.drawable.ic_baseline_filter_1_24);
                        break;
                    case Second:
                        filterIcon.setImageResource(R.drawable.ic_baseline_filter_2_24);
                        break;
                    case Third:
                        filterIcon.setImageResource(R.drawable.ic_baseline_filter_3_24);
                        break;
                }
            }
        });

        onClickHandlers = new ItemOnClickHandlers() {
            @Override
            public void itemOnClick(int itemIndex) {
                Toast.makeText(getApplicationContext(), "Click on " + notesList.get(itemIndex).getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void itemOnLongClick(int itemIndex) {
                initClickAlertDialog(itemIndex);
            }
        };

        noteListAdapter.setClickHandler(onClickHandlers);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNoteInfoActivity();
            }
        });

        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priorityFilterAlertDialog();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = searchBox.getText().toString().trim();
                if(!text.isEmpty()){
                    viewModel.searchFiltering(text);
                } else {
                    viewModel.clearSearchFilter();
                }
            }
        });
    }

    private void initNotesRecyclerView() {
        //noteList.setLayoutManager(new LinearLayoutManager(this));
        noteListAdapter = new NoteListAdapter();
        noteList.setAdapter(noteListAdapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        noteList.addItemDecoration(itemDecoration);
    }

    private void initClickAlertDialog(final long itemIndex) {

        final String[] options = {getString(R.string.edit), getString(R.string.delete)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        dialogInterface.cancel();
                        openNoteInfoActivity(notesList.get((int) itemIndex).getId());
                        break;
                    case 1:
                        dialogInterface.cancel();
                        viewModel.deleteNote((int) itemIndex);
                        break;
                    default:
                        dialogInterface.cancel();
                        break;
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void priorityFilterAlertDialog(){
        final String[] options = {getString(R.string.first_priority), getString(R.string.second_priority), getString(R.string.third_priority), getString(R.string.none)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        dialogInterface.cancel();
                        viewModel.priorityFiltering(PriorityType.First);
                        break;
                    case 1:
                        dialogInterface.cancel();
                        viewModel.priorityFiltering(PriorityType.Second);
                        break;
                    case 2:
                        dialogInterface.cancel();
                        viewModel.priorityFiltering(PriorityType.Third);
                        break;
                    case 3:
                        dialogInterface.cancel();
                        viewModel.clearPriorityFilter();
                        break;
                    default:
                        dialogInterface.cancel();
                        break;
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    viewModel.reloadNotes();
                    Toast.makeText(getApplicationContext(), "edited", Toast.LENGTH_SHORT).show();
                }
                break;
            case ADD_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    viewModel.reloadNotes();
                    Toast.makeText(getApplicationContext(), "added", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void openNoteInfoActivity() {
        Intent noteInfo = new Intent(getApplicationContext(), NoteInfoActivity.class);
        startActivityForResult(noteInfo, ADD_ACTIVITY);
    }

    private void openNoteInfoActivity(long noteId) {
        Intent noteInfo = new Intent(getApplicationContext(), NoteInfoActivity.class);
        noteInfo.putExtra(NoteInfoActivity.NOTE_ID, noteId);
        startActivityForResult(noteInfo, EDIT_ACTIVITY);
    }

}