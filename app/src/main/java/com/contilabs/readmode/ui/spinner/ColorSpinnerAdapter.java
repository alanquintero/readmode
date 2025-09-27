/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.spinner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.contilabs.readmode.R;

import java.util.List;

/**
 * Custom adapter for displaying ColorItem objects in a Spinner.
 * Each item shows an icon with a tinted color and a text label.
 *
 * @author Alan Quintero
 */
public class ColorSpinnerAdapter extends ArrayAdapter<ColorItem> {

    private static final String TAG = ColorSpinnerAdapter.class.getSimpleName();

    private final @NonNull LayoutInflater inflater;

    public ColorSpinnerAdapter(final @NonNull Context context, final @NonNull List<ColorItem> items) {
        super(context, 0, items);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, final @NonNull View convertView, final @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(final int position, final View convertView, final @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(final int position, View convertView, final @NonNull ViewGroup parent) {
        Log.d(TAG, "Creating item view");
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }

        final ColorItem item = getItem(position);

        final ImageView icon = convertView.findViewById(R.id.icon);
        final TextView name = convertView.findViewById(R.id.name);

        if (item != null) {
            Log.d(TAG, "Creating the item");
            // Icon
            final GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(item.getIconColor()); // fill color
            drawable.setStroke(3, Color.BLACK); // stroke width + color
            icon.setImageDrawable(drawable);
            // Name
            name.setText(item.getName());
        } else {
            Log.w(TAG, "Item is null");
        }

        return convertView;
    }
}

