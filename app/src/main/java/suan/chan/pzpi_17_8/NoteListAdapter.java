package suan.chan.pzpi_17_8;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import suan.chan.pzpi_17_8.entity.Note;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>{
    private ItemOnClickHandlers clickHandler;
    private List<Note> notes;

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.size();
    }

    public void setClickHandler(ItemOnClickHandlers clickHandler) {
        this.clickHandler = clickHandler;
    }

    public void clearNotes() {
        notes.clear();
        notifyDataSetChanged();
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView title, date;
        ImageView img, priority;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            title = itemView.findViewById(R.id.item_note_title);
            date = itemView.findViewById(R.id.item_note_date);
            img = itemView.findViewById(R.id.item_note_image);
            priority = itemView.findViewById(R.id.item_note_priority_icon);
        }

        public void bind(Note note){
            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            title.setText(note.getTitle());
            date.setText(formater.format(note.getEditTime()));
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
            if(note.getImage() != null){
                img.setImageBitmap(note.getImage());
            }
        }

        @Override
        public void onClick(View view) {
            if(clickHandler != null){
                clickHandler.itemOnClick(getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if(clickHandler != null){
                clickHandler.itemOnLongClick(getLayoutPosition());
                return true;
            }
            return false;
        }
    }
}
