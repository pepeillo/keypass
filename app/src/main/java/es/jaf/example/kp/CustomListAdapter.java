package es.jaf.example.kp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomListAdapter extends ArrayAdapter<ElementStructure> {
    private final Activity context;
    private final List<ElementStructure> records;

    public CustomListAdapter(Activity context, List<ElementStructure> records) {
        super(context, 0, records);
        this.context = context;
        this.records = records;
    }

    @Override
    public View getView(int listPosition, View convertView, ViewGroup parent) {
        final ElementStructure element = records.get(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListTextView = convertView.findViewById(R.id.expandedListItem);
        expandedListTextView.setText(element.getTitle());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}