/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.ui.controller;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import autonightmode.mx.com.alanquintero.autonightmode.R;
import autonightmode.mx.com.alanquintero.autonightmode.model.ReadModeSettings;
import autonightmode.mx.com.alanquintero.autonightmode.observer.dropdown.ColorDropdownObserver;
import autonightmode.mx.com.alanquintero.autonightmode.observer.settings.SettingsObserver;
import autonightmode.mx.com.alanquintero.autonightmode.util.Constants;
import autonightmode.mx.com.alanquintero.autonightmode.util.PrefsHelper;

/**
 * TextViewController is responsible for applying consistent visual styles to all text views.
 *
 * @author Alan Quintero
 */
public class TextViewController implements ColorDropdownObserver, SettingsObserver {

    private static final String TAG = TextViewController.class.getSimpleName();

    private final @NonNull Context context;
    private final @NonNull PrefsHelper prefsHelper;
    private final @NonNull ReadModeSettings readModeSettings;
    private final @NonNull String[] colorNames;
    private final @NonNull TextView colorSettingsText;

    public TextViewController(final @NonNull Context context, final @NonNull View rootView, final @NonNull ReadModeSettings readModeSettings, final @NonNull String[] colorNames) {
        this.context = context;
        this.prefsHelper = PrefsHelper.init(context);
        this.readModeSettings = readModeSettings;
        this.colorNames = colorNames;
        colorSettingsText = rootView.findViewById(R.id.labelColorSettings);
    }

    /**
     * Sets the initial value for the Color Settings text
     */
    public void setupTextViews() {
        setColorSettingsText();
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
    public void setColorSettingsText() {
        final String label = context.getString(R.string.color_settings_placeholder);
        if (prefsHelper.shouldUseSameIntensityBrightnessForAll()) {
            colorSettingsText.setText(label);
        } else {
            String selectedColor = colorNames[readModeSettings.getColorDropdownPosition()];
            colorSettingsText.setText(context.getString(R.string.color_settings_format, label, selectedColor));
        }
    }

    @Override
    public void onColorDropdownPositionChange(final int currentColorDropdownPosition) {
        setColorSettingsText();
    }

    @Override
    public void onSettingsChanged(final @NonNull Constants.SETTING_OPTIONS setting) {
        switch (setting) {
            case AUTO_READ_MODE:
                // nothing here
                break;
            case SAME_SETTINGS_FOR_ALL:
                setColorSettingsText();
                break;
            default:
                Log.w(TAG, "Invalid setting option");

        }
    }
}
