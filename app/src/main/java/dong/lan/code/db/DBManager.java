package dong.lan.code.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import dong.lan.code.bean.Code;
import dong.lan.code.bean.Note;
import dong.lan.code.utils.AES;

/**
 * 项目：code
 * 作者：梁桂栋
 * 日期： 2015/9/20  20:54.
 * Email:760625325@qq.com
 */
public class DBManager {

    private static DBManager manager = new DBManager();
    private static DBHelper helper;

    public static void onInit(Context context)
    {
        helper = DBHelper.getInstance(context);
        AES.init();
    }


    public static synchronized DBManager getInstance()
    {
        return manager;
    }

    public synchronized DBHelper getHelper(){
        return helper;
    }
    public synchronized  void saveNote(Note note)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteDao.COLUMN_NOTE,note.getNote());
        values.put(NoteDao.COLUMN_TIME,note.getTime());
        values.put(NoteDao.COLUMN_TYPE, note.getType());
        if(db.isOpen())
        {
           db.replace(NoteDao.TABLE_NAME,null,values);
        }
    }

    public synchronized void deleteNote(String time)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen())
        {
            db.delete(NoteDao.TABLE_NAME,NoteDao.COLUMN_TIME + " = ?",new String[]{time});
        }
    }
    public synchronized List<Note> getAllNote()
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(NoteDao.TABLE_NAME,null,null,null,null,null,NoteDao.COLUMN_TIME+" desc",null);
        if(!cursor.moveToFirst())
            return  null;
        List<Note> notes = new ArrayList<>();
        do {
            Note note = new Note();
            note.setNote(cursor.getString(cursor.getColumnIndex(NoteDao.COLUMN_NOTE)));
            note.setTime(cursor.getString(cursor.getColumnIndex(NoteDao.COLUMN_TIME)));
            note.setType(cursor.getString(cursor.getColumnIndex(NoteDao.COLUMN_TYPE)));
            notes.add(note);
        }while (cursor.moveToNext());
        cursor.close();
        return notes;
    }
    public synchronized List<Note> getSearchNotes(String des)
    {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from note where noteDes like ?",new String[]{"%"+des+"%"});
        if(!cursor.moveToFirst())
        {
            return  null;
        }
        List<Note> notes = new ArrayList<>();
        int noteIndex = cursor.getColumnIndex(NoteDao.COLUMN_NOTE);
        int timeIndex = cursor.getColumnIndex(NoteDao.COLUMN_TIME);
        int typeIndex = cursor.getColumnIndex(NoteDao.COLUMN_TYPE);
        do {
            Note note = new Note();
            note.setNote(cursor.getString(noteIndex));
            note.setTime(cursor.getString(timeIndex));
            note.setType(cursor.getString(typeIndex));
            notes.add(note);
        }while (cursor.moveToNext());

        cursor.close();
        return notes;
    }

    public synchronized void updateNote(ContentValues values, String time)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen())
        {
            db.update(NoteDao.TABLE_NAME,values,NoteDao.COLUMN_TIME+" = ?",new String[]{time});
        }
    }

    public synchronized void clearCode(){
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()){
            db.execSQL("delete from "+CodeDao.TABLE_NAME);
        }
    }

    public synchronized void clearCode(SQLiteDatabase db){
        if(db.isOpen()){
            db.execSQL("delete from "+CodeDao.TABLE_NAME);
        }
    }
    public synchronized List<Code> getAllCodes()
    {
        SQLiteDatabase db = helper.getReadableDatabase();

            Cursor cursor = db.query(CodeDao.TABLE_NAME, null, null, null, null, null, CodeDao.COLUMN_COUNT+" asc",null);
            if(!cursor.moveToFirst())
            {
                return  null;
            }
            List<Code> codes = new ArrayList<>();
            int id = cursor.getColumnIndex(CodeDao.COLUMN_ID);
            int des = cursor.getColumnIndex(CodeDao.COLUMN_CODE);
            int word = cursor.getColumnIndex(CodeDao.COLUNMN_WORD);
            int count = cursor.getColumnIndex(CodeDao.COLUMN_COUNT);
            int other = cursor.getColumnIndex(CodeDao.COLUMN_OTHER);
            int asyn = cursor.getColumnIndex(CodeDao.COLUMN_ASYN);
            do {
                Code code = new Code();
                code.setId(cursor.getInt(id));
                code.setWord(AES.decode(cursor.getString(word)));
                code.setCount(cursor.getInt(count));
                code.setAsyn(cursor.getInt(asyn));
                code.setDes((cursor.getString(des)));
                code.setOther(AES.decode(cursor.getString(other)));
                codes.add(code);
            }while (cursor.moveToNext());

        cursor.close();
        return codes;
    }

	public synchronized List<Code> getSearchCodes(String des)
    {
        SQLiteDatabase db = helper.getReadableDatabase();

		Cursor cursor = db.rawQuery("select * from code where codeName like ?",new String[]{"%"+des+"%"});
		if(!cursor.moveToFirst())
		{
			return  null;
		}
		List<Code> codes = new ArrayList<>();
        int id = cursor.getColumnIndex(CodeDao.COLUMN_ID);
        int desIndex = cursor.getColumnIndex(CodeDao.COLUMN_CODE);
        int word = cursor.getColumnIndex(CodeDao.COLUNMN_WORD);
        int count = cursor.getColumnIndex(CodeDao.COLUMN_COUNT);
        int other = cursor.getColumnIndex(CodeDao.COLUMN_OTHER);
        int asyn = cursor.getColumnIndex(CodeDao.COLUMN_ASYN);
        do {
			Code code = new Code();
            code.setId(cursor.getInt(id));
            code.setDes(cursor.getString(desIndex));
            code.setWord(AES.decode(cursor.getString(word)));
            code.setCount(cursor.getInt(count));
            code.setAsyn(cursor.getInt(asyn));
            code.setOther(AES.decode(cursor.getString(other)));
            codes.add(code);
		}while (cursor.moveToNext());

        cursor.close();
        return codes;
    }
	
	
    public synchronized void saveCode(Code code)
    {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CodeDao.COLUMN_CODE,code.getDes());
        values.put(CodeDao.COLUNMN_WORD,AES.encode(code.getWord()));
        values.put(CodeDao.COLUMN_COUNT,code.getCount());
        values.put(CodeDao.COLUMN_OTHER,AES.encode(code.getOther()));
        values.put(CodeDao.COLUMN_ASYN,code.getAsyn());
        values.put(CodeDao.COLUMN_DES,(code.getDes()));
        if(db.isOpen())
        {
            db.replace(CodeDao.TABLE_NAME,null,values);
        }

    }
    public synchronized void saveCode(SQLiteDatabase db,Code code)
    {

        ContentValues values = new ContentValues();
        values.put(CodeDao.COLUMN_CODE,code.getDes());
        values.put(CodeDao.COLUNMN_WORD,AES.encode(code.getWord()));
        values.put(CodeDao.COLUMN_COUNT,code.getCount());
        values.put(CodeDao.COLUMN_OTHER,AES.encode(code.getOther()));
        values.put(CodeDao.COLUMN_ASYN,code.getAsyn());
        values.put(CodeDao.COLUMN_DES,(code.getDes()));
        if(db.isOpen())
        {
            db.replace(CodeDao.TABLE_NAME,null,values);
        }

    }

    public synchronized void saveDecodeCode(Code code)
    {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CodeDao.COLUMN_CODE,code.getDes());
        values.put(CodeDao.COLUNMN_WORD,code.getWord());
        values.put(CodeDao.COLUMN_COUNT,code.getCount());
        values.put(CodeDao.COLUMN_OTHER,code.getOther());
        values.put(CodeDao.COLUMN_ASYN,code.getAsyn());
        values.put(CodeDao.COLUMN_DES,code.getDes());
        if(db.isOpen())
        {
            db.replace(CodeDao.TABLE_NAME,null,values);
        }

    }

    public synchronized void saveDecodeCode(SQLiteDatabase db,Code code)
    {

        ContentValues values = new ContentValues();
        values.put(CodeDao.COLUMN_CODE,code.getDes());
        values.put(CodeDao.COLUNMN_WORD,code.getWord());
        values.put(CodeDao.COLUMN_COUNT,code.getCount());
        values.put(CodeDao.COLUMN_OTHER,code.getOther());
        values.put(CodeDao.COLUMN_ASYN,code.getAsyn());
        values.put(CodeDao.COLUMN_DES,code.getDes());
        if(db.isOpen())
        {
            db.replace(CodeDao.TABLE_NAME,null,values);
        }

    }


    public synchronized void updateCode(ContentValues values,String id)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.update(CodeDao.TABLE_NAME, values, CodeDao.COLUMN_ID + " = ?", new String[]{id});
        }
    }

    public synchronized void deleteCode(String name)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(CodeDao.TABLE_NAME, CodeDao.COLUMN_CODE + " = ?", new String[]{name});
        }
    }




}
