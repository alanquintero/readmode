/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import autonightmode.mx.com.alanquintero.autonightmode.R;
import autonightmode.mx.com.alanquintero.autonightmode.command.ReadModeCommand;
import autonightmode.mx.com.alanquintero.autonightmode.model.ReadModeSettings;
import autonightmode.mx.com.alanquintero.autonightmode.observer.customcolor.CustomColorSubject;
import autonightmode.mx.com.alanquintero.autonightmode.ui.spinner.ColorItem;
import autonightmode.mx.com.alanquintero.autonightmode.ui.spinner.ColorSpinnerAdapter;
import autonightmode.mx.com.alanquintero.autonightmode.util.ColorUtils;
import autonightmode.mx.com.alanquintero.autonightmode.util.Constants;
import autonightmode.mx.com.alanquintero.autonightmode.util.PrefsHelper;
import autonightmode.mx.com.alanquintero.autonightmode.util.Utils;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.List;

/**
 * CustomColorDialog is a custom dialog used to display and manage
 * the user-configurable settings for choosing a custom color for the Read Mode.
 *
 * @author Alan Quintero
 */
public class CustomColorDialog extends DialogFragment {

    private static final String TAG = CustomColorDialog.class.getSimpleName();

    private final @NonNull Context context;

    private final @NonNull ReadModeCommand readModeCommand;
    private final @NonNull ReadModeSettings readModeSettings;
    private final @NonNull CustomColorSubject customColorSubject;

    private List<ColorItem> colorItems;
    private ColorSpinnerAdapter colorSpinnerAdapter;

    public CustomColorDialog(final @NonNull Context context, final @NonNull ReadModeCommand readModeCommand, final @NonNull ReadModeSettings readModeSettings, final @NonNull CustomColorSubject customColorSubject) {
        this.context = context;
        this.readModeCommand = readModeCommand;
        this.readModeSettings = readModeSettings;
        this.customColorSubject = customColorSubject;
    }

    public void setColorItems(final List<ColorItem> colorItems) {
        this.colorItems = colorItems;
    }

    public void setColorSpinnerAdapter(final ColorSpinnerAdapter colorSpinnerAdapter) {
        this.colorSpinnerAdapter = colorSpinnerAdapter;
    }

    @Override
    public @NonNull android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "Opening custom color dialog");
        final PrefsHelper prefsHelper = PrefsHelper.init(requireContext());
        readModeCommand.pauseReadMode();

        final ColorPickerDialog.Builder colorPickerDialogBuilder = new ColorPickerDialog.Builder(context, R.style.AlertDialogCustom)
                .setCustomTitle(Utils.createDialogTitle(requireContext(), R.string.choose_custom_color))
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton(R.string.confirm,
                        (ColorEnvelopeListener) (envelope, fromUser) -> {
                            final String selectedHexColor = ColorUtils.getHexColor(envelope.getColor());
                            Log.d(TAG, "Selected color: " + selectedHexColor);

                            prefsHelper.saveProperty(Constants.PREF_COLOR, Constants.CUSTOM_COLOR);
                            prefsHelper.saveProperty(Constants.PREF_CUSTOM_COLOR, selectedHexColor);

                            // update preferences for custom color
                            Log.d(TAG, "Updating custom color to: " + selectedHexColor);
                            readModeSettings.setCustomColor(selectedHexColor);
                            customColorSubject.setCustomColor(selectedHexColor);
                            if (colorItems != null && colorSpinnerAdapter != null && colorItems.size() > Constants.CUSTOM_COLOR_DROPDOWN_POSITION) {
                                Log.d(TAG, "Updating custom color to in colorItems and notifying DataSet Changed...");
                                colorItems.get(Constants.CUSTOM_COLOR_DROPDOWN_POSITION).setIconColor(Color.parseColor(selectedHexColor));
                                colorSpinnerAdapter.notifyDataSetChanged();
                            }

                            // resume read mode
                            readModeCommand.resumeReadMode();
                        })
                .setNegativeButton(R.string.cancel,
                        (dialogInterface, i) -> {
                            readModeCommand.resumeReadMode();
                            dialogInterface.dismiss();
                        })
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .setBottomSpace(12); // set a bottom space between the last slidebar and buttons.


        final ColorPickerView colorPickerView = colorPickerDialogBuilder.getColorPickerView();
        // Initial color
        Log.d(TAG, "Loading initial custom color: " + readModeSettings.getCustomColor());
        final int initialColor = Color.parseColor(readModeSettings.getCustomColor());
        colorPickerView.setInitialColor(initialColor);
        // Flag
        final BubbleFlag bubbleFlag = new BubbleFlag(context);
        bubbleFlag.setFlagMode(FlagMode.ALWAYS);
        colorPickerView.setFlagView(bubbleFlag);

        return colorPickerDialogBuilder.show();
    }
}
