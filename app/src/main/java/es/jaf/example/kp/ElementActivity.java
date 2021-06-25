package es.jaf.example.kp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

        findViewById(R.id.cmdSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("action", "save");
                intent.putExtra("id", id);
                intent.putExtra("title", txtTitle.getText().toString());
                intent.putExtra("username", txtUsername.getText().toString());
                intent.putExtra("password", txtPassword.getText().toString());
                intent.putExtra("url", txtUrl.getText().toString());
                intent.putExtra("notes", txtNotes.getText().toString());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}