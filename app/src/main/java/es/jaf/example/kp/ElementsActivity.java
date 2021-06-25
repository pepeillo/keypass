package es.jaf.example.kp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import es.jaf.example.kp.database.DbManager;

import java.util.List;

public class ElementsActivity extends Activity implements IElementAction{
    ListView listView;
    CustomListAdapter itemsAdapter;
    List<ElementStructure> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elements);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ElementStructure element = records.get(position);
                Intent intent = new Intent(ElementsActivity.this, ElementActivity.class);
                intent.putExtra("id", "" + element.getId());
                intent.putExtra("title", element.getTitle());
                intent.putExtra("username", element.getUserName());
                intent.putExtra("password", element.getPassword());
                intent.putExtra("url", element.getUrl());
                intent.putExtra("notes", element.getNotes());
                startActivityForResult(intent, 1001);
            }
        });

        GlobalApplication.getInstance().showProgress(ElementsActivity.this);
        new ElementsActivity.AsyncGenerateCipherDatabase().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                String action = data.getStringExtra("action");

                String id = data.getStringExtra("id");
                String title = data.getStringExtra("title");
                String userName = data.getStringExtra("username");
                String password = data.getStringExtra("password");
                String url = data.getStringExtra("url");
                String notes = data.getStringExtra("notes");
                if ("save".equalsIgnoreCase(action)) {
                    DbManager dbManager = GlobalApplication.getInstance().getDbManager();
                    dbManager.openDatabase(GlobalApplication.getInstance().getPassword());
                    if ("0".equals(id)) {
                        dbManager.addRecord(new ElementStructure(Integer.parseInt(id), title, userName, password, url, notes));
                    } else {
                        dbManager.updateRecord(new ElementStructure(Integer.parseInt(id), title, userName, password, url, notes));
                    }
                    records = dbManager.getRecords();
                    itemsAdapter.clear();
                    itemsAdapter.addAll(records);
                    itemsAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void cmdDeleteClick(int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ElementsActivity.this);
        dialog.setTitle(R.string.app_name)
                .setIcon(android.R.drawable.ic_menu_help)
                .setMessage(ElementsActivity.this.getString(R.string.confirm_delete, records.get(position).getTitle()))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DbManager dbManager = GlobalApplication.getInstance().getDbManager();
                        try {
                            dbManager.openDatabase(GlobalApplication.getInstance().getPassword());
                            dbManager.delete(records.get(position).getId());
                        }catch (Exception e) {
                            Toast.makeText(ElementsActivity.this, "Error:" +e.getMessage(), Toast.LENGTH_LONG).show();
                        } finally {
                            records = dbManager.getRecords();
                            itemsAdapter.clear();
                            itemsAdapter.addAll(records);
                            itemsAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        dialog.show();
    }

    private class AsyncGenerateCipherDatabase extends AsyncTask<Void, Void, Integer> {
        private String pwd;
        AlertDialog.Builder dialog;

        @Override
        protected void onPreExecute() {
            dialog = new AlertDialog.Builder(ElementsActivity.this);
            pwd = GlobalApplication.getInstance().getPassword();
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
                records = dbManager.getRecords();
                dbManager.closeDatabase();

                return 0;
            } catch (Exception e) {
                Log.e("ZZZZ", e.getMessage(), e);
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
                itemsAdapter = new CustomListAdapter(ElementsActivity.this, ElementsActivity.this, records);
                listView.setAdapter(itemsAdapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle() == null || item.getTitle().length() == 0) {
            return false;
        }

        int itemId = item.getItemId();

        if (itemId == R.id.newElement) {
            Intent intent = new Intent(ElementsActivity.this, ElementActivity.class);
            intent.putExtra("id", "0");
            intent.putExtra("title", "");
            intent.putExtra("username", "");
            intent.putExtra("password", "");
            intent.putExtra("url", "");
            intent.putExtra("notes", "");
            startActivityForResult(intent, 1001);
        }
        return false;
    }
}
