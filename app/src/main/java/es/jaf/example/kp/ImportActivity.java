package es.jaf.example.kp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.opencsv.CSVReader;
import es.jaf.example.kp.database.DbManager;
import es.jaf.example.kp.folderpicker.FolderPicker;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public class ImportActivity extends Activity {

    private TextView txtFile;
    private ToggleButton chkDelete;
    private View cmdImport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        txtFile = findViewById(R.id.txtFile);
        View cmdSelect = findViewById(R.id.cmdSelect);
        chkDelete = findViewById(R.id.chkDelete);
        cmdImport = findViewById(R.id.cmdImport);
        cmdSelect.setOnClickListener(v -> {
            Intent intent = new Intent(ImportActivity.this, FolderPicker.class);
            intent.putExtra("title", getString(R.string.prompt_select_a_file));
            intent.putExtra("location", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
            intent.putExtra("pickFiles", true);
            startActivityForResult(intent, GlobalApplication.FOLDERPICKER_CODE);
        });

        cmdImport.setOnClickListener(v -> {
            String filename = txtFile.getText().toString();
            if (filename.length() > 0) {
                try {
                    int recs = importCSV(filename, chkDelete.isChecked());
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ImportActivity.this);
                    dialog.setTitle(R.string.app_name)
                            .setMessage(getString(R.string.prompt_process_ok, recs))
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, (dialog1, id) -> {
                                dialog1.dismiss();
                                finish();
                            });
                    dialog.show();

                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ImportActivity.this);
                    dialog.setTitle(R.string.app_name)
                            .setMessage(R.string.prompt_process_err)
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, (dialog12, id) -> dialog12.dismiss());
                    dialog.show();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == GlobalApplication.FOLDERPICKER_CODE && resultCode == Activity.RESULT_OK) {

            String folderLocation = intent.getExtras().getString("data");
            txtFile.setText(folderLocation);
            cmdImport.setEnabled(true);
        }
    }

    private int importCSV(String path, boolean delete) {
        DbManager dbManager = GlobalApplication.getInstance().getDbManager();
        try {
            dbManager.openDatabase(GlobalApplication.getInstance().getPassword());

            File file = new File(path);
            if (!file.canRead()) {
                Toast.makeText(this, R.string.prompt_read_permissions, Toast.LENGTH_SHORT).show();
                return 0;
            }

            dbManager.beginTransaction();
            if (delete) {
                dbManager.deleteAllRecords();
            }
            int counter = 0;
            CSVReader reader = new CSVReader(new FileReader(path));
            List<String[]> myEntries = reader.readAll();
            for (String[] str: myEntries) {
                counter++;
                if (counter == 1) {
                    continue;
                }
                dbManager.addRecord(new ElementStructure(0, str[0], str[1], str[2], str[3], str[4], str[5]));
            }

            dbManager.setTransactionSuccessful();
            return (counter > 0) ? counter-1 : 0;
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            dbManager.endTransaction();
        }
        return 0;
    }

}