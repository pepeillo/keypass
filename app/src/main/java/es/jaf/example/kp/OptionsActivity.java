package es.jaf.example.kp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.Toast;

public class OptionsActivity extends Activity {


    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        findViewById(R.id.imgData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this, ElementsActivity.class));
            }
        });

        findViewById(R.id.imgImport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this, ImportActivity.class));
            }
        });

        findViewById(R.id.imgExport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this, ExportActivity.class));
            }
        });

        findViewById(R.id.imgChangePwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this, ChangePasswordActivity.class));
            }
        });

        findViewById(R.id.imgExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void onBackPressed() {
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {
            backPressedTime = t;
            Toast.makeText(this, R.string.tap_again, Toast.LENGTH_SHORT).show();
        } else {
            GlobalApplication.getInstance().getDbManager().closeDatabase();
            finish();
            android.os.Process.killProcess(Process.myPid());
        }
    }
}