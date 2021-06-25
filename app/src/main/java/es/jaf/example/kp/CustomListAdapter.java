package es.jaf.example.kp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import es.jaf.example.kp.database.DbManager;

import java.io.File;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter<ElementStructure> {

    private final IElementAction listener;
    private Activity context;
    private List<ElementStructure> records;

    public CustomListAdapter(Activity context, IElementAction listener, List<ElementStructure> records) {
        super(context, 0, records);
        this.context = context;
        this.records = records;
        this.listener = listener;
    }

    @Override
    public View getView(int listPosition, View convertView, ViewGroup parent) {
        final ElementStructure element = (ElementStructure) records.get(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListTextView = convertView.findViewById(R.id.expandedListItem);
        expandedListTextView.setText(element.getTitle());
        convertView.findViewById(R.id.cmdDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cmdDeleteClick(listPosition);
            }
        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}