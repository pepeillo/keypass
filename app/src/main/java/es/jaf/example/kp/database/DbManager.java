package es.jaf.example.kp.database;

import android.content.ContentValues;
import android.content.Context;
import es.jaf.example.kp.ElementStructure;
import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
    @SuppressWarnings("unused")
    private static final String TAG = "DbCipherTable";

    private SQLiteDatabase mDatabase;
    private final DbCipherHelper mDbHelper;

    /**
     * Array of all columns in the table
     */
    private final String[] mAllColumns = {
            DbCipherHelper.COLUMN_ID,
            DbCipherHelper.COLUMN_GROUP,
            DbCipherHelper.COLUMN_TITLE,
            DbCipherHelper.COLUMN_USERNAME,
            DbCipherHelper.COLUMN_PASSWORD,
            DbCipherHelper.COLUMN_URL,
            DbCipherHelper.COLUMN_NOTES
    };

    /**
     * Constructor
     * @param context Contexto
     */
    public DbManager(Context context) {
        mDbHelper = new DbCipherHelper(context);
    }

    /**
     * Open the SQLite database
     */
    public void openDatabase(String pwd) throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase(pwd);
    }

    /**
     * Close the SQLite database
     */
    public void closeDatabase() {
        mDbHelper.close();
    }

    /**
     * Add a row in the database table
     * @param record Record Structure
     * @return Record structure
     */
    public ElementStructure addRecord(final ElementStructure record) {
        ContentValues values = new ContentValues();
        values.put(DbCipherHelper.COLUMN_GROUP, record.getGroup());
        values.put(DbCipherHelper.COLUMN_TITLE, record.getTitle());
        values.put(DbCipherHelper.COLUMN_USERNAME, record.getUserName());
        values.put(DbCipherHelper.COLUMN_PASSWORD, record.getPassword());
        values.put(DbCipherHelper.COLUMN_URL, record.getUrl());
        values.put(DbCipherHelper.COLUMN_NOTES, record.getNotes());

        final long insertId = mDatabase.insert(DbCipherHelper.TABLE_TARGET, null, values);

        Cursor cursor = mDatabase.query(DbCipherHelper.TABLE_TARGET,
                mAllColumns, DbCipherHelper.COLUMN_ID + " = "
                        + insertId, null, null, null, null);

        cursor.moveToFirst();

        ElementStructure recordInserted = cursorToRecord(cursor);
        cursor.close();

        return recordInserted;
    }

    /**
     * Update a record from the database table
     * @param record record
     * @return structure
     */
    public ElementStructure updateRecord(final ElementStructure record) {
        ContentValues values = new ContentValues();
        values.put(DbCipherHelper.COLUMN_GROUP, record.getGroup());
        values.put(DbCipherHelper.COLUMN_TITLE, record.getTitle());
        values.put(DbCipherHelper.COLUMN_USERNAME, record.getUserName());
        values.put(DbCipherHelper.COLUMN_PASSWORD, record.getPassword());
        values.put(DbCipherHelper.COLUMN_URL, record.getUrl());
        values.put(DbCipherHelper.COLUMN_NOTES, record.getNotes());

        mDatabase.update(DbCipherHelper.TABLE_TARGET, values, DbCipherHelper.COLUMN_ID + "=?", new String[]{""+record.getId()});

        Cursor cursor = mDatabase.query(DbCipherHelper.TABLE_TARGET,
                mAllColumns, DbCipherHelper.COLUMN_ID + " = "
                        + record.getId(), null, null, null, null);

        cursor.moveToFirst();

        ElementStructure recordInserted = cursorToRecord(cursor);
        cursor.close();

        return recordInserted;
    }

    /**
     * Delete all records from the database table, not the table itself
     */
    public void deleteAllRecords() {
        mDatabase.delete(DbCipherHelper.TABLE_TARGET, null, null);
    }

    public void delete(long id) {
        mDatabase.delete(DbCipherHelper.TABLE_TARGET, DbCipherHelper.COLUMN_ID + "=?", new String[]{""+id});
    }

    /**
     * Retrieve a list of records that match the filters passed
     *
     * @return List of dbQuake objects
     */
    public List<ElementStructure> getRecords() {
        String allCols = DbCipherHelper.COLUMN_ID + "," +
                DbCipherHelper.COLUMN_GROUP + "," +
                DbCipherHelper.COLUMN_TITLE + "," +
                DbCipherHelper.COLUMN_USERNAME + "," +
                DbCipherHelper.COLUMN_PASSWORD + "," +
                DbCipherHelper.COLUMN_URL + "," +
                DbCipherHelper.COLUMN_NOTES;

        List<ElementStructure> selectedRecords = new ArrayList<>();
        Cursor cursor =mDatabase.rawQuery("SELECT " + allCols +
                " FROM " + DbCipherHelper.TABLE_TARGET +
                " ORDER BY " + DbCipherHelper.COLUMN_TITLE +
                " COLLATE NOCASE ASC;", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ElementStructure record = cursorToRecord(cursor);
            selectedRecords.add(record);
            cursor.moveToNext();
        }

        cursor.close();

        return selectedRecords;
    }

    /**
     * Begin a transaction
     */
    public void beginTransaction() {
        mDatabase.beginTransaction();
    }

    /**
     * Sets the transaction as finished successfully
     */
    public void setTransactionSuccessful() {
        mDatabase.setTransactionSuccessful();
    }

    /**
     * Ends the transaction
     */
    public void endTransaction() {
        mDatabase.endTransaction();
    }

    /**
     * Change the password of database
     * @param newPassword new password
     */
    public void changePassword(String newPassword) {
        mDatabase.changePassword(newPassword);
    }

    /**
     * Take the cursor from a database query and create fill the record structure
     *
     * @param cursor Database cursor from sqlite query
     * @return new record structure
     */
    private ElementStructure cursorToRecord(final Cursor cursor) {
        return new ElementStructure(cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6));
    }
}