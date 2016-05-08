package dong.lan.code.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dong.lan.code.Interface.NoteItemClickListener;
import dong.lan.code.R;
import dong.lan.code.bean.Note;
import dong.lan.code.db.DBManager;
import dong.lan.code.db.NoteDao;
import dong.lan.code.fragment.FragmentNote;

/**
 * Created by Dooze on 2015/10/31.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    private Context context;
    private List<Note> notes = new ArrayList<>();
    private LayoutInflater inflater;

    NoteItemClickListener noteItemClickListener;

    public void setNoteItemClickListener(NoteItemClickListener listener)
    {
        this.noteItemClickListener = listener;
    }

    public  NoteAdapter(Context context,List<Note> notes)
    {
        this.context =context;
        this.notes = notes;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public NoteHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new NoteHolder(inflater.inflate(R.layout.item_note,null));
    }

    @Override
    public void onBindViewHolder(final NoteHolder noteHolder, final int i) {
        noteHolder.type.setText(notes.get(i).getType());
        noteHolder.time.setText(notes.get(i).getTime());
        noteHolder.note.setText(notes.get(i).getNote());
        noteHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noteItemClickListener != null) {
                    if (noteHolder.note.getText().toString().equals(FragmentNote.NO_NOTE)) {
                        noteItemClickListener.onNoteClick(null, noteHolder.getPosition());
                    } else {
                        noteItemClickListener.onNoteClick(notes.get(noteHolder.getPosition()), noteHolder.getPosition());
                    }
                }
            }
        });
        noteHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                noteItemClickListener.onNoteLongClick(notes.get(noteHolder.getPosition()),noteHolder.getPosition());
                return false;
            }
        });
    }

    public void addAll(List<Note> notes)
    {
        if(this.notes==null)
        {
            this.notes = notes;
        }else
        {
            this.notes.addAll(notes);
        }
    }

    public void delAddAll(List<Note> notes)
    {
        if(this.notes==null)
        {
            this.notes = notes;
        }else
        {
            this.notes.clear();
            this.notes.addAll(notes);
        }
        notifyDataSetChanged();
    }
    public void updateNote(String oldTime,Note note,int pos)
    {
        ContentValues values = new ContentValues();
        values.put(NoteDao.COLUMN_TIME, note.getTime());
        values.put(NoteDao.COLUMN_NOTE,note.getNote());
        DBManager.getInstance().updateNote(values, oldTime);
        notes.set(pos,note);
        notifyItemChanged(pos);
    }
    public void addNote(Note note)
    {
        if(notes==null)
            notes = new ArrayList<>();
        DBManager.getInstance().saveNote(note);
        notes.add(0, note);
        notifyItemInserted(0);
    }
    public void deleteNote(int pos)
    {
        DBManager.getInstance().deleteNote(notes.get(pos).getTime());
        notes.remove(pos);
        notifyItemRemoved(pos);
    }
    public Note getNoteAt(int pos)
    {
        return notes.get(pos);
    }
    @Override
    public int getItemCount() {
        if(notes==null)
            return 0;
        return notes.size();
    }

    class NoteHolder extends RecyclerView.ViewHolder
    {
        TextView time ;
        TextView type;
        TextView note;
        public NoteHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.note_time);
            type = (TextView) itemView.findViewById(R.id.note_type);
            note = (TextView) itemView.findViewById(R.id.note_des);
        }


    }
}
