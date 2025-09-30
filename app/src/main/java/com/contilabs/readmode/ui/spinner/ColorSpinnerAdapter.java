/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode.ui.spinner;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.contilabs.readmode.R;
import com.contilabs.readmode.model.ReadModeSettings;

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

    private final @NonNull ReadModeSettings readModeSettings;

    public ColorSpinnerAdapter(final @NonNull Context context, final @NonNull List<ColorItem> items, final @NonNull ReadModeSettings readModeSettings) {
        super(context, 0, items);
        inflater = LayoutInflater.from(context);
        this.readModeSettings = readModeSettings;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, final @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(final int position, @Nullable View convertView, final @NonNull ViewGroup parent) {
        Log.d(TAG, "getDropDownView");
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }
        if (position == readModeSettings.getColorDropdownPosition()) {
            convertView.setBackgroundColor(inflater.getContext().getResources().getColor(R.color.spinner_selected_item));
        }
        return createItemView(position, convertView, parent);
    }

    private View createItemView(final int position, @Nullable View convertView, final @NonNull ViewGroup parent) {
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
            drawable.setStroke(3, inflater.getContext().getResources().getColor(R.color.spinner_item_stroke)); // stroke width + color
            icon.setImageDrawable(drawable);
            // Name
            name.setText(item.getName());
        } else {
            Log.w(TAG, "Item is null");
        }

        return convertView;
    }
}

