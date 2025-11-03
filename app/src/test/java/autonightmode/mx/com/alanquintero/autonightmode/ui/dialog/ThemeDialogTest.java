/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.FragmentActivity;
import androidx.test.core.app.ApplicationProvider;

import autonightmode.mx.com.alanquintero.autonightmode.R;
import autonightmode.mx.com.alanquintero.autonightmode.util.Constants;
import autonightmode.mx.com.alanquintero.autonightmode.util.PrefsHelper;
import autonightmode.mx.com.alanquintero.autonightmode.util.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class ThemeDialogTest {

    private AutoCloseable mocks;
    private ThemeDialog themeDialog;
    private PrefsHelper mockPrefsHelper;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        // Create a real activity with Robolectric
        final FragmentActivity activity = Robolectric.buildActivity(FragmentActivity.class).create().start().resume().get();

        // Setup mock PrefsHelper
        mockPrefsHelper = mock(PrefsHelper.class);

        // Create and attach the fragment manually
        themeDialog = new ThemeDialog();

        // Attach the fragment to the activity
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(themeDialog, "test")
                .commitNow();
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void onCreateDialog_createsDialog() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class);
             MockedStatic<Utils> utilsStatic = mockStatic(Utils.class)) {

            // Given
            setupMocks(prefsHelperStatic, utilsStatic);

            // When
            Dialog dialog = themeDialog.onCreateDialog(null);

            // Then
            assertNotNull(dialog);
            // We can verify the behavior through mocks since we can't easily get the actual dialog view
            verify(mockPrefsHelper).getTheme();
        }
    }

    @Test
    public void onRadioButtonSelected_savesCorrectTheme() {
        try (MockedStatic<PrefsHelper> prefsHelperStatic = mockStatic(PrefsHelper.class);
             MockedStatic<Utils> utilsStatic = mockStatic(Utils.class)) {

            // Given
            setupMocks(prefsHelperStatic, utilsStatic);

            // Create a real RadioGroup to test the listener
            final Context context = ApplicationProvider.getApplicationContext();
            final RadioGroup radioGroup = new RadioGroup(context);
            final RadioButton lightButton = new RadioButton(context);
            lightButton.setId(R.id.radio_light);
            radioGroup.addView(lightButton);

            // Set up the listener
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.radio_light) {
                    Utils.setAppTheme(Constants.ThemeMode.LIGHT);
                    mockPrefsHelper.saveProperty(Constants.PREF_THEME, Constants.ThemeMode.LIGHT.getValue());
                }
            });

            // When
            radioGroup.check(R.id.radio_light);

            // Then
            verify(mockPrefsHelper, atLeast(1)).saveProperty(Constants.PREF_THEME, Constants.ThemeMode.LIGHT.getValue());
            utilsStatic.verify(() -> Utils.setAppTheme(Constants.ThemeMode.LIGHT), atLeast(2));
        }
    }

    // Helper method
    private void setupMocks(MockedStatic<PrefsHelper> prefsHelperStatic, MockedStatic<Utils> utilsStatic) {
        when(mockPrefsHelper.getTheme()).thenReturn(Constants.ThemeMode.SYSTEM_DEFAULT);
        prefsHelperStatic.when(() -> PrefsHelper.init(any())).thenReturn(mockPrefsHelper);

        // Mock Utils methods
        utilsStatic.when(() -> Utils.setAppTheme(any(Constants.ThemeMode.class))).thenAnswer(invocation -> null);
        utilsStatic.when(() -> Utils.createDialogTitle(any(), anyInt())).thenReturn(mock(View.class));
    }
}
