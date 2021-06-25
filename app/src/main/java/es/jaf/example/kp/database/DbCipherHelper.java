package es.jaf.example.kp.database;

import android.content.Context;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class DbCipherHelper extends SQLiteOpenHelper {
    public static final String TABLE_TARGET = "table_result";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "col_title";
    public static final String COLUMN_USERNAME= "col_username";
    public static final String COLUMN_PASSWORD= "col_password";
    public static final String COLUMN_URL = "col_url";
    public static final String COLUMN_NOTES= "col_notes";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TARGET + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text, "
            + COLUMN_USERNAME+ " text, "
            + COLUMN_PASSWORD+ " text, "
            + COLUMN_URL+ " text, "
            + COLUMN_NOTES+ " text);";

    public static final String DATABASE_NAME = "4366839f3cc7d75bd64db6bab4f4cf47";

    private static final int DATABASE_VERSION = 2;

    public DbCipherHelper(Context context) {
        //super(context, android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + BuildConfig.APPLICATION_ID + "/" + DATABASE_NAME, null, DATABASE_VERSION);
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Upgrading database from version " + oldVersion + " to "+ newVersion
        //will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TARGET);
        onCreate(db);
    }
} 