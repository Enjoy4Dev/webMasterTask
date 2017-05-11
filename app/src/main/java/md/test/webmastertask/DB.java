package md.test.webmastertask;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public  class DB{
    public DbHelper mDbHelper;
    public SQLiteDatabase mDb;

    public DB(Context context) {
        mDbHelper = new DbHelper(context);
        mDb = mDbHelper.getWritableDatabase();
    }

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FeedReader.db";

    private static final String TABLE_NAME = "orders";
    private static final String COLUMN_NAME_ADMIN_AGREE = "agree";

    private static final String _ID = "_id";
    private static final String INT_TYPE = " INT";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME_ADMIN_AGREE       + INT_TYPE
                    + ");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;




    public void insertId (int _id, int agree) {
        ContentValues cv = new ContentValues();
        cv.put(_ID, _id);
        cv.put(COLUMN_NAME_ADMIN_AGREE, agree);
        Log.d(TAG, "insertId AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA " + cv);
        mDb.insert(TABLE_NAME, null, cv);
    }


    public void deleteTable () {

        String buildSQL = "DELETE FROM " + TABLE_NAME;

        Log.d(TAG, "deleteTable" + buildSQL);

        mDb.execSQL(buildSQL);
    }

//
    public void setAgree (int myId, int adminAgree) {

        String buildSQL = "UPDATE " + TABLE_NAME + " SET agree = '" + adminAgree + "' WHERE _id = '" + myId + "'";

        Log.d(TAG, "setAgree: " + buildSQL);

        mDb.execSQL(buildSQL);
    }

    public Cursor getId (int myId) {

        String buildSQL = "SELECT " + _ID + " FROM " + TABLE_NAME + " WHERE _id = '" + myId + "'";

        Log.d(TAG, "getId SQL: " + buildSQL);

        return mDb.rawQuery(buildSQL, null);
    }

    public Cursor getDbSize () {

//        String buildSQL = "SELECT " + _ID + " FROM " + TABLE_NAME + " WHERE _id = '" + myId + "'";
        String buildSQL = "SELECT COUNT(*) FROM " + TABLE_NAME;


        Log.d(TAG, "getDbSize SQL: " + buildSQL);

        return mDb.rawQuery(buildSQL, null);
    }


    public Cursor getIdFromAgree (int myAgree) {

        String buildSQL = "SELECT " + _ID + " FROM " + TABLE_NAME + " WHERE agree = '" + myAgree + "'";

        Log.d(TAG, "getIdFromAgree SQL: " + buildSQL);

        return mDb.rawQuery(buildSQL, null);

    }

    public Cursor getAll (int myId) {

        String buildSQL = "SELECT * FROM " + TABLE_NAME + " WHERE _id = '" + myId + "'";

        Log.d(TAG, "getAll SQL: " + buildSQL);

        return mDb.rawQuery(buildSQL, null);
    }

    public void deleteId (int myId) {

        String buildSQL = "DELETE FROM " + TABLE_NAME + " WHERE _id = '" + myId + "'";

        Log.d(TAG, "deleteId" + buildSQL);

        mDb.execSQL(buildSQL);
    }

    public class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }



        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            this.onCreate(db);
        }
    }
}
