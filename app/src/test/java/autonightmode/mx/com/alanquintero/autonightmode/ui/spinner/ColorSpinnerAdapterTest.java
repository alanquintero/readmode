/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode.ui.spinner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.test.core.app.ApplicationProvider;

import autonightmode.mx.com.alanquintero.autonightmode.model.ReadModeSettings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class ColorSpinnerAdapterTest {

    @Mock
    private ReadModeSettings mockReadModeSettings;

    private AutoCloseable mocks;
    private Context context;
    private ColorSpinnerAdapter adapter;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        context = ApplicationProvider.getApplicationContext();

        // Setup mock items
        final ColorItem item1 = new ColorItem("Red", 0xFFFF0000);
        final ColorItem item2 = new ColorItem("Blue", 0xFF0000FF);
        final ColorItem item3 = new ColorItem("Green", 0xFF00FF00);
        final List<ColorItem> colorItems = Arrays.asList(item1, item2, item3);

        // Creating adapter with real context
        adapter = new ColorSpinnerAdapter(context, colorItems, mockReadModeSettings);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void constructor_initializesCorrectly() {
        // Then
        assertNotNull(adapter);
        assertEquals(3, adapter.getCount());
    }

    @Test
    public void getItem_returnsCorrectItem() {
        // When
        final ColorItem item = adapter.getItem(1);

        // Then
        assertEquals("Blue", item.getName());
        assertEquals(0xFF0000FF, item.getIconColor());
    }

    @Test
    public void getItemId_returnsPosition() {
        // When
        final long itemId = adapter.getItemId(2);

        // Then
        assertEquals(2, itemId);
    }

    @Test
    public void getView_createsView() {
        // Given
        final ViewGroup parent = new FrameLayout(context);

        // When
        final View view = adapter.getView(0, null, parent);

        // Then
        assertNotNull(view);
    }

    @Test
    public void getDropDownView_createsView() {
        // Given
        when(mockReadModeSettings.getColorDropdownPosition()).thenReturn(1);
        final ViewGroup parent = new FrameLayout(context);

        // When
        final View view = adapter.getDropDownView(0, null, parent);

        // Then
        assertNotNull(view);
    }
}