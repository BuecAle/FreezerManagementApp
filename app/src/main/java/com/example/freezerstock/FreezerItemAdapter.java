package com.example.freezerstock;

// FreezerItemAdapter.java
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.freezerstock.FreezerItem;

import java.util.List;

public class FreezerItemAdapter extends ArrayAdapter<FreezerItem> {
    private Context context;
    private List<FreezerItem> items;
    private int selectedPosition = 0;  // To track the selected item

    public FreezerItemAdapter(Context context, List<FreezerItem> items) {
        super(context, R.layout.list_item, items);
        this.context = context;
        this.items = items;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        FreezerItem item = items.get(position);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView quantityTextView = convertView.findViewById(R.id.quantityTextView);
        TextView unitTextView = convertView.findViewById(R.id.unitTextView);

        nameTextView.setText(item.getName());
        quantityTextView.setText(String.valueOf(item.getQuantity()));
        unitTextView.setText(item.getUnit());

        // Highlight the selected item
        if (position == selectedPosition) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.selected_item_background));
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }

        return convertView;
    }
}