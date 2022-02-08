package es.jaf.example.kp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import es.jaf.example.kp.database.DbManager;
import net.sqlcipher.database.SQLiteDatabase;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SQLiteDatabase.loadLibs(getApplicationContext());
        findViewById(R.id.cmdLogin).setOnClickListener(view -> {
            GlobalApplication.getInstance().showProgress(MainActivity.this);
            new MainActivity.AsyncGenerateCipherDatabase().execute();
        });
        EditText txtPwd = findViewById(R.id.password);
        ImageView cmdHideShow = findViewById(R.id.cmdHideShow);

        cmdHideShow.setOnClickListener(view -> {
            if (txtPwd.getTransformationMethod() instanceof PasswordTransformationMethod) {
                txtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                cmdHideShow.setImageResource(R.drawable.show);
            } else {
                txtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                cmdHideShow.setImageResource(R.drawable.hide);
            }
        });
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(Process.myPid());
        finish();
    }

    /**
     * AsyncTask to generate and test encrypted database
     */
    private class AsyncGenerateCipherDatabase extends AsyncTask<Void, Void, Integer> {

        private String pwd;
        private AlertDialog.Builder dialog;
        private EditText txtPwd;

        @Override
        protected void onPreExecute() {
            dialog = new AlertDialog.Builder(MainActivity.this);
            txtPwd = findViewById(R.id.password);
            pwd = ((EditText) findViewById(R.id.password)).getText().toString();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            DbManager dbManager = null;
            try {
                dbManager = GlobalApplication.getInstance().getDbManager();
                dbManager.openDatabase(pwd);
                return 0;
            } catch (Exception e) {
                return 1;
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
                        .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                            dialog.dismiss();
                            txtPwd.setText("");
                        });
                dialog.show();
            }
        }
    }
}