package suan.chan.pzpi_17_8;

import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import suan.chan.pzpi_17_8.entity.Note;
import suan.chan.pzpi_17_8.entity.PriorityType;

public class NoteInfoActivity extends AppCompatActivity {
    public static final String NOTE_ID  = "NOTE_ID";
    private static final int LOAD_IMAGE_INTENT = 743;

    boolean newNote = true;
    long noteId;
    EditText title;
    EditText description;
    EditText text;
    ImageView image;
    ImageView priority;
    Button saveBtn;
    NoteService noteService;
    Note note;
    NoteInfoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_info);

        noteService = new NoteService(getApplicationContext());

        title = findViewById(R.id.note_info_title);
        description = findViewById(R.id.note_info_description);
        text = findViewById(R.id.note_info_text);
        image = findViewById(R.id.note_info_img);
        saveBtn = findViewById(R.id.note_info_save_btn);
        priority = findViewById(R.id.note_info_priority);

        viewModel = new ViewModelProvider(this).get(NoteInfoViewModel.class);

        Bundle arguments = getIntent().getExtras();
        if(arguments != null){
            newNote = false;
            noteId = (long) arguments.getSerializable(NOTE_ID);
            viewModel.loadNote(noteId);
        }

        viewModel.getLiveNote().observe(this, new Observer<Note>() {
            @Override
            public void onChanged(Note noteF) {
                note = noteF;
                setDisplayNoteInfo(note);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPriorityAlertDialog();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, LOAD_IMAGE_INTENT);
            }
        });
    }

    private void saveNote(){
        setNoteInfoPartialHelper();
        if(newNote){
            noteService.AddNewNote(note);
            newNote = false;
        } else {
            noteService.updateNote(note);
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        Toast.makeText(getApplicationContext(), R.string.successful_save, Toast.LENGTH_SHORT).show();
    }

    private void setDisplayNoteInfo(Note note){
        title.setText(note.getTitle());
        description.setText(note.getDescription());
        if(note.getText() != null){
            text.setText(note.getText());
        }
        setDisplayNotePriorityInfo(note);
        if(note.getImage() != null){
            image.setImageBitmap(note.getImage());
        }
    }

    private void setNoteInfoPartialHelper(){
        note.setTitle(title.getText().toString().trim());
        note.setDescription(description.getText().toString().trim());
        note.setText(text.getText().toString().trim());
    }

    private void setDisplayNotePriorityInfo(Note note){
        switch (note.getPriority()){

            case First:
                priority.setImageResource(R.drawable.ic_baseline_filter_1_24);
                break;
            case Second:
                priority.setImageResource(R.drawable.ic_baseline_filter_2_24);
                break;
            case Third:
                priority.setImageResource(R.drawable.ic_baseline_filter_3_24);
                break;
        }
    }

    private void setPriorityAlertDialog(){
        final String[] options = {getString(R.string.first_priority), getString(R.string.second_priority), getString(R.string.third_priority)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        dialogInterface.cancel();
                        note.setPriority(PriorityType.First);
                        break;
                    case 1:
                        dialogInterface.cancel();
                        note.setPriority(PriorityType.Second);
                        break;
                    case 2:
                        dialogInterface.cancel();
                        note.setPriority(PriorityType.Third);
                        break;
                    default:
                        dialogInterface.cancel();
                        break;
                }

                setDisplayNotePriorityInfo(note);
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
        if(requestCode == LOAD_IMAGE_INTENT && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();

            String picturePath = null;
            String[] filePathCol = {MediaStore.Images.Media.DATA};
            if(selectedImage != null){
                Cursor cursor = getContentResolver().query(selectedImage, filePathCol, null, null, null);
                if(cursor != null){
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathCol[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                }
            }

            if(picturePath != null){
                Bitmap selectedBmp = BitmapFactory.decodeFile(picturePath);
                Bitmap res = scaleBitmap(selectedBmp);
                selectedBmp.recycle();
                note.setImage(res);
                image.setImageBitmap(res);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        setNoteInfoPartialHelper();
    }

    private Bitmap scaleBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int maxWidth = 150;
        int maxHeight = 150;

        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int)(height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int)(width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }
}