package es.jaf.example.kp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import es.jaf.example.kp.database.DbManager;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.concurrent.Executor;

public class MainActivity extends FragmentActivity {

    //UI Views
    private TextView authStatusTv;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init UI views
        authStatusTv = findViewById(R.id.authStatusTv);
        View authBtn = findViewById(R.id.authBtn);

        //init bio metric

        Executor executor = ContextCompat.getMainExecutor(this);
        //error authenticating, stop tasks that requires auth
        //failed authenticating, stop tasks that requires auth
        BiometricPrompt.AuthenticationCallback authCB = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                //error authenticating, stop tasks that requires auth
                authStatusTv.setText(getString(R.string.auth_error) + ": " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                findViewById(R.id.layoutPassword).setVisibility(View.VISIBLE);
                findViewById(R.id.cmdLogin).setVisibility(View.VISIBLE);
                findViewById(R.id.imgBiometric).setVisibility(View.INVISIBLE);
                findViewById(R.id.authStatusTv).setVisibility(View.INVISIBLE);
                findViewById(R.id.authBtn).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                //failed authenticating, stop tasks that requires auth
                authStatusTv.setText(R.string.auth_failed);
            }
        };
        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, authCB);

        //setup title,description on auth dialog
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.auth_title))
                .setNegativeButtonText(getString(android.R.string.cancel))
                .build();

        //handle authBtn click, start authentication
        authBtn.setOnClickListener(v -> {
            //show auth dialog
            biometricPrompt.authenticate(promptInfo);
        });

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

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}