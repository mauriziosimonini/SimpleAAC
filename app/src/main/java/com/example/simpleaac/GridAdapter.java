package com.example.simpleaac;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private static final String TAG = "GridAdapter";
    private Context context;
    private List<Item> items;
    private LayoutInflater inflater;

    public GridAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
        this.inflater = LayoutInflater.from(context);
        Log.d(TAG, "GridAdapter created with " + items.size() + " items");
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.item_text);
            convertView.setTag(holder);
            Log.d(TAG, "Created new view for position: " + position);
        } else {
            holder = (ViewHolder) convertView.getTag();
            Log.d(TAG, "Reusing view for position: " + position);
        }

        Item item = items.get(position);
        holder.textView.setText(item.getText());

        // Set a background color to make items visible
        convertView.setBackgroundColor(0xFFE0E0E0); // Light gray background

        Log.d(TAG, "Binding data for position: " + position + " with text: " + item.getText());

        return convertView;
    }

    private static class ViewHolder {
        TextView textView;
    }
}