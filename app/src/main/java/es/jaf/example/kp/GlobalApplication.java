package es.jaf.example.kp;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import es.jaf.example.kp.database.DbManager;

public class GlobalApplication extends Application {
    public static final int FOLDERPICKER_CODE = 123;
    private static GlobalApplication instance;
    private DbManager dbManager = null;
    private String password = null;
    private ProgressDialog progress;

    @Override
    public void onCreate() {
        super.onCreate();
        dbManager = new DbManager(getApplicationContext());
        instance = this;
    }

    public static GlobalApplication getInstance() {
        if (instance == null) {
            instance = new GlobalApplication();
        }
        return instance;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public DbManager getDbManager() {
        return dbManager;
    }

    public void showProgress(Context context) {
        if (progress != null) {
            return;
        }
        progress = new ProgressDialog(context);
        progress.setTitle(R.string.prompt_loading);
        progress.setCanceledOnTouchOutside(false);

        Window w = progress.getWindow();
        if (w != null) {
            //w.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            w.setGravity(Gravity.CENTER);
        }

        progress.show();
    }

    public void hideProgress() {
        if (progress != null) {
            progress.dismiss();
        }
        progress = null;
    }
}
