package es.jaf.example.kp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.EditText;
import es.jaf.example.kp.database.DbManager;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.*;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase.loadLibs(getApplicationContext());
        final EditText txtPassword = findViewById(R.id.password);
        findViewById(R.id.cmdLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalApplication.getInstance().showProgress(MainActivity.this);
                new AsyncGenerateCipherDatabase().execute();
            }
        });
    }

    /**
     * AsyncTask to generate and test encrypted database
     */
    private class AsyncGenerateCipherDatabase extends AsyncTask<Void, Void, Integer> {

        private String pwd;
        AlertDialog.Builder dialog;

        @Override
        protected void onPreExecute() {
            dialog = new AlertDialog.Builder(MainActivity.this);
            pwd = ((EditText)findViewById(R.id.password)).getText().toString();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            /*
            if (params.length != 1) {
                throw new IllegalArgumentException("Must pass operation as argument (encrypted)");
            }
             */
            DbManager dbManager = null;
            try {
                dbManager = GlobalApplication.getInstance().getDbManager();
                dbManager.openDatabase(pwd);

                //dbManager.deleteAll();
                //importCSV(dbManager);
                return 0;
            } catch (Exception e) {
                Log.e("ZZZZ", e.getMessage(), e);
                return  1;
            } finally {
                if (dbManager != null) {
                    dbManager.closeDatabase();
                }
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            GlobalApplication.getInstance().hideProgress();
            if (result == 0) {
                GlobalApplication.getInstance().setPassword(pwd);
                startActivity(new Intent(MainActivity.this, OptionsActivity.class));
            } else {
                dialog.setTitle(R.string.app_name)
                        .setMessage(R.string.login_failed)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                finish();
                                android.os.Process.killProcess(Process.myPid());
                            }
                        });
                dialog.show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private void importCSV(DbManager dbManager) throws Exception {
        try {
            File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
            if (!dir.exists()) {
                return;
            }
            if (!new File(dir.getAbsolutePath() + "/db.txt").exists()) {
                return;
            }
            if (!new File(dir.getAbsolutePath() + "/db.txt").canRead()) {
                return;
            }
            FileReader file = new FileReader(dir.getAbsolutePath() + "/db.txt");
            BufferedReader buffer = new BufferedReader(file);
            String line = "";

            dbManager.beginTransaction();
            while ((line = buffer.readLine()) != null) {
                String[] str = line.split(",");
                if (!(line.length() > 9 && line.substring(1, 8).startsWith("Raíz"))) {
                    continue;
                }
                String title = (str[1].substring(1, str[1].length() - 1)).replace("'", "''");
                String username = (str[2].substring(1, str[2].length() - 1)).replace("'", "''");
                String password = (str[3].substring(1, str[3].length() - 1)).replace("'", "''");
                String url = (str[4].substring(1, str[4].length() - 1)).replace("'", "''");
                String notes = (str[5].substring(1, str[5].length() - 1)).replace("'", "''");

                dbManager.addRecord(new ElementStructure(0, title, username, password, url, notes));
            }
            dbManager.setTransactionSuccessful();
            dbManager.endTransaction();
        } catch (Exception e) {
            //
        }
    }
}