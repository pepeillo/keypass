package es.jaf.example.kp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ElementActivity extends Activity {
    private String id;
    private EditText txtTitle;
    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtUrl;
    private EditText txtNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        String title = intent.getStringExtra("title");
        String userName = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String url = intent.getStringExtra("url");
        String notes = intent.getStringExtra("notes");

        txtTitle = findViewById(R.id.elTitle);
        txtUsername = findViewById(R.id.elUsername);
        txtPassword = findViewById(R.id.elPassword);
        txtUrl= findViewById(R.id.elUrl);
        txtNotes= findViewById(R.id.elNotes);

        txtTitle.setText(title);
        txtUsername.setText(userName);
        txtPassword.setText(password);
        txtUrl.setText(url);
        txtNotes.setText(notes);

        findViewById(R.id.cmdSave).setOnClickListener(v -> {
            Intent intent1 = new Intent();
            intent1.putExtra("action", "save");
            intent1.putExtra("id", id);
            intent1.putExtra("title", txtTitle.getText().toString());
            intent1.putExtra("username", txtUsername.getText().toString());
            intent1.putExtra("password", txtPassword.getText().toString());
            intent1.putExtra("url", txtUrl.getText().toString());
            intent1.putExtra("notes", txtNotes.getText().toString());
            setResult(Activity.RESULT_OK, intent1);
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_element, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle() == null || item.getTitle().length() == 0) {
            return false;
        }

        if (item.getItemId() == R.id.delete_element) {
            if ("0".equals(id)) {
                setResult(Activity.RESULT_OK, new Intent());
                finish();
                return false;
            }
            AlertDialog.Builder dialog = new AlertDialog.Builder(ElementActivity.this);
            dialog.setTitle(R.string.app_name)
                    .setIcon(android.R.drawable.ic_menu_help)
                    .setMessage(ElementActivity.this.getString(R.string.confirm_delete))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, (dlg, ide) -> {
                        Intent intent = new Intent();
                        intent.putExtra("action", "delete");
                        intent.putExtra("id", id);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, (dlg, ide) -> {
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                    });

            dialog.show();
        }
        return false;
    }
}