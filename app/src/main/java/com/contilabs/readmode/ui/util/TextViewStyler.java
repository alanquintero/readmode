package com.contilabs.readmode.ui.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.contilabs.readmode.R;
import com.contilabs.readmode.model.ReadModeSettings;
import com.contilabs.readmode.util.Constants;
import com.contilabs.readmode.util.PrefsHelper;

/**
 * TextViewStyler is responsible for applying consistent visual styles to all text views.
 *
 * @author Alan Quintero
 */
public class TextViewStyler {

    private final @NonNull Context context;
    private final @NonNull TextView colorSettingsText;
    private final @NonNull PrefsHelper prefsHelper;

    public TextViewStyler(final @NonNull Context context, final @NonNull View rootView) {
        this.context = context;
        this.prefsHelper = PrefsHelper.init(context);
        colorSettingsText = rootView.findViewById(R.id.labelColorSettings);
    }

    /**
     * Updates the provided {@link TextView} with the current color settings text.
     * <p>
     * If no color is selected in the dropdown (i.e., the current position equals
     * {@code noColorDropdownPosition}), the text is set to a placeholder label.
     * Otherwise, the text is formatted to include the placeholder label along
     * with the currently selected color name.
     * </p>
     */
    public void setColorSettingsText(final @NonNull ReadModeSettings readModeSettings, final @NonNull String[] colors) {
        final String label = context.getString(R.string.color_settings_placeholder);
        if (prefsHelper.shouldUseSameIntensityBrightnessForAll() || readModeSettings.getColorDropdownPosition() == Constants.NO_COLOR_DROPDOWN_POSITION) {
            colorSettingsText.setText(label);
        } else {
            String selectedColor = colors[readModeSettings.getColorDropdownPosition()];
            colorSettingsText.setText(context.getString(R.string.color_settings_format, label, selectedColor));
        }
    }
}
