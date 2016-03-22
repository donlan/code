package dong.lan.code.Interface;

import dong.lan.code.bean.Note;

/**
 * Created by Dooze on 2015/10/31.
 */
public interface NoteItemClickListener {
    void onNoteClick(Note note ,int pos);
    void onNoteLongClick(Note note ,int pos);
}
