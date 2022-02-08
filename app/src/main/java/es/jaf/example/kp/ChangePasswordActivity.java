package es.jaf.example.kp;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import es.jaf.example.kp.database.DbManager;

public class ChangePasswordActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        final EditText txtCurrent = findViewById(R.id.txtCurrentPwd);
        final EditText txtNew1 = findViewById(R.id.txtNewPwd1);
        final EditText txtNew2 = findViewById(R.id.txtNewPwd2);
        ImageView cmdHideShow = findViewById(R.id.cmdHideShow);

        cmdHideShow.setOnClickListener(view -> {
            if (txtCurrent.getTransformationMethod() instanceof PasswordTransformationMethod) {
                txtCurrent.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                txtNew1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                txtNew2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                cmdHideShow.setImageResource(R.drawable.show);
            } else {
                txtCurrent.setTransformationMethod(PasswordTransformationMethod.getInstance());
                txtNew1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                txtNew2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                cmdHideShow.setImageResource(R.drawable.hide);
            }
        });

        findViewById(R.id.cmdChangePwd).setOnClickListener(v -> {
            String current = txtCurrent.getText().toString();
            String new1 = txtNew1.getText().toString();
            String new2 = txtNew2.getText().toString();
            int errorId = 0;
            if (new1.equals(new2) && new1.length() > 0) {
                if (current.equals(new1)) {
                    errorId = R.string.err_pwd1;
                    //la nueva contraseña debe ser distinta de la actual
                } else {
                    DbManager dbManager = GlobalApplication.getInstance().getDbManager();
                    try {
                        dbManager.openDatabase(current);
                    } catch(Exception e) {
                        errorId = R.string.err_pwd2;
                        //La contraseña actual no es correcta
                    }
                    if (errorId == 0) {
                        try {
                            dbManager.changePassword(new1);
                            GlobalApplication.getInstance().setPassword(new1);
                        } catch (Exception e) {
                            errorId = R.string.err_pwd3;
                            //No se ha podido cambiar la contraseña
                        } finally {
                            dbManager.closeDatabase();
                        }
                    }
                }
            } else {
                errorId = R.string.err_pwd4;
                //La nueva contraseña debe coincidir con la repetición de contraseña
            }
            if (errorId == 0) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePasswordActivity.this);
                dialog.setTitle(R.string.app_name)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage(R.string.prompt_password_changed)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, (dialog12, id) -> {
                            dialog12.dismiss();
                            finish();
                        });
                dialog.show();
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePasswordActivity.this);
                dialog.setTitle(R.string.app_name)
                        .setIcon(android.R.drawable.stat_notify_error)
                        .setMessage(errorId)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, (dialog1, id) -> dialog1.dismiss());
                dialog.show();
            }
        });
    }
}