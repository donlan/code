package dong.lan.code.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dooze on 2015/9/20.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME ="CODE";
    public static final int VER =8;
    private static DBHelper instance = null;
    private static  final String CREATE_CODE_TABLE="create table "
            +CodeDao.TABLE_NAME+" ("
            +CodeDao.COLUMN_ID+" integer primary key autoincrement, "
            +CodeDao.COLUMN_CODE+" text, "
            +CodeDao.COLUNMN_WORD+" text, "
            +CodeDao.COLUMN_OTHER+" text, "
            +CodeDao.COLUMN_DES+" text, "
            +CodeDao.COLUMN_ASYN+" integer default 0, "
            +CodeDao.COLUMN_COUNT + " integer default 0);";


    private static final String CREATE_NOTE_TABLE="create table "
            +NoteDao.TABLE_NAME+" ("
            +NoteDao.COLUMN_TYPE+ " text, "
            +NoteDao.COLUMN_TIME+ " text primary key, "
            +NoteDao.COLUMN_NOTE+ " text );";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VER);
    }

    public static DBHelper getInstance(Context context)
    {
        if(instance==null)
            instance =new DBHelper(context.getApplicationContext());
        return instance;
    }

    public static void init(Context context)
    {
        if(instance==null)
            instance =new DBHelper(context.getApplicationContext());
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CODE_TABLE);
        db.execSQL(CREATE_NOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion==2)
        {
            db.execSQL(CREATE_NOTE_TABLE);
        }
        if(newVersion==8)
        {
            db.execSQL("alter table code add column des text");
            System.out.println("UPDATE");
        }
    }

    public void closeDB()
    {
        if(instance!=null)
        {
            SQLiteDatabase db =instance.getWritableDatabase();
            db.close();

            instance =null;
        }
    }
}
