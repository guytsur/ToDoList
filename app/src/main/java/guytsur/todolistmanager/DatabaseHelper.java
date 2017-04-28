package guytsur.todolistmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Guy on 4/25/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{


    private SQLiteDatabase myDataBase;
    private static final String TABLE_NAME = "jobs_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "text";
    File dbFile;
    File dbFilepath;
    Context myContext;

    public DatabaseHelper(Context context){
        super(context, TABLE_NAME, null, 1);
        this.myContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            createDataBase(this.myContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 +" TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public boolean addData(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, item);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * Returns only the ID that matches the name passed in
     * @param name
     * @return
     */
    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Updates the job field
     * @param newJob
     * @param id
     * @param oldJob
     */
    public void updateJob(String newJob, int id, String oldJob){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 +
                " = '" + newJob + "' WHERE " + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + oldJob + "'";
        db.execSQL(query);
    }

    /**
     * Delete from database
     * @param id
     * @param job
     */
    public void deleteJob(int id, String job){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + job + "'";
        db.execSQL(query);
    }
    /**
     * Returns all the data from database
     * @return
     */
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void createDataBase(Context mycontext) throws IOException {

        boolean dbExist = checkDataBase();
        if(dbExist){
            String path = dbFile.getAbsolutePath();
        }
        if(!dbExist)
        {
            dbFile= new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"ToDoList");
            boolean flag=dbFile.mkdir();
            dbFilepath.createNewFile();
        }

    }

    private boolean checkDataBase() {
        dbFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"PhotoApp");
        return dbFile.exists();
    }

    public void openDataBase() throws SQLException {
        String myPath =      Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"PhotoApp"+"/"+"photodb" ;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null,     SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {

        return super.getWritableDatabase();
    }
}


