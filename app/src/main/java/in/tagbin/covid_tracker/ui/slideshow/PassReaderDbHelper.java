package in.tagbin.covid_tracker.ui.slideshow;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.tagbin.covid_tracker.DataReaderContract;

public class PassReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "FeedReader.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DataReaderContract.Pass.TABLE_NAME + " (" +
                    DataReaderContract.Pass._ID + " INTEGER PRIMARY KEY," +
                    DataReaderContract.Pass.COLUMN_NAME_CATEGORY + " TEXT ," +
                    DataReaderContract.Pass.COLUMN_NAME_FROM + " TEXT ," +
                    DataReaderContract.Pass.COLUMN_NAME_TO + " TEXT," +
                    DataReaderContract.Pass.COLUMN_NAME_DATE + " TEXT," +
                    DataReaderContract.Pass.COLUMN_NAME_TIME + " TEXT," +
                    DataReaderContract.Pass.COLUMN_NAME_REASON + " TEXT," +
                    DataReaderContract.Pass.COLUMN_NAME_DOC + " TEXT," +
                    DataReaderContract.Pass.COLUMN_NAME_IS_SYNCED + " TEXT DEFAULT 'FALSE'," +
                    DataReaderContract.Pass.COLUMN_NAME_DJANGO_ID + " TEXT," +
                    DataReaderContract.Pass.COLUMN_NAME_STATUS + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DataReaderContract.Pass.TABLE_NAME;

    public PassReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //TODO on Upgrade should only update coloumns
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}