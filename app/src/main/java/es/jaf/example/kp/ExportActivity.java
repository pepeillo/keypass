package es.jaf.example.kp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import es.jaf.example.kp.database.DbManager;
import es.jaf.example.kp.folderpicker.FolderPicker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class ExportActivity extends Activity {
    private TextView txtFile;
    private View cmdExport;
    private TextView txtFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        txtFile = findViewById(R.id.txtFile);
        txtFolder = findViewById(R.id.txtFolder);
        cmdExport = findViewById(R.id.cmdExport);
        findViewById(R.id.cmdSelect).setOnClickListener(v -> {
            Intent intent = new Intent(ExportActivity.this, FolderPicker.class);
            intent.putExtra("title", getString(R.string.prompt_select_a_folder));
            intent.putExtra("location", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
            intent.putExtra("pickFiles", false);
            startActivityForResult(intent, GlobalApplication.FOLDERPICKER_CODE);
        });

        cmdExport.setOnClickListener(v -> {
            String path = txtFolder.getText().toString();
            String filename = txtFile.getText().toString();
            if (filename.length() > 0 && path.length() > 0) {
                try {
                    int recs = exportCSV(path + "/" + filename);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ExportActivity.this);
                    dialog.setTitle(R.string.app_name)
                            .setMessage(getString(R.string.prompt_process_ok, recs))
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, (dialog12, id) -> {
                                dialog12.dismiss();
                                finish();
                            });
                    dialog.show();

                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ExportActivity.this);
                    dialog.setTitle(R.string.app_name)
                            .setMessage(R.string.prompt_process_err)
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, (dialog1, id) -> dialog1.dismiss());
                    dialog.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == GlobalApplication.FOLDERPICKER_CODE && resultCode == Activity.RESULT_OK) {

            String folderLocation = intent.getExtras().getString("data");
            txtFolder.setText(folderLocation);
            cmdExport.setEnabled(true);
        }
    }

    private int exportCSV(String path) throws Exception {
        DbManager dbManager = GlobalApplication.getInstance().getDbManager();
        try {
            File file = new File(path);
            dbManager.openDatabase(GlobalApplication.getInstance().getPassword());

            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter buffer = new BufferedWriter(fileWriter);
            buffer.write("\"Group\",\"Title\",\"Username\",\"Password\",\"URL\",\"Notes\"");
            buffer.newLine();

            List<ElementStructure> records = dbManager.getRecords();

            int counter = 0;
            for (ElementStructure record : records) {
                counter++;
                buffer.write(record.toString());
                buffer.newLine();
            }
            buffer.flush();
            buffer.close();
            return counter;
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return 0;
    }
}