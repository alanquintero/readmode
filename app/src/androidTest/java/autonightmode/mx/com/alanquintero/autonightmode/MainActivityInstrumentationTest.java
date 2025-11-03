/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import autonightmode.mx.com.alanquintero.autonightmode.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // Wait for the activity to be fully loaded
        activityRule.getScenario().onActivity(activity -> {
            // You could register idling resources here if needed
        });
    }

    @Test
    public void appLaunch_shouldDisplayMainUIComponents() {
        // Basic test to verify main UI components are present
        // This is the most fundamental test that should always pass

        // Check if main buttons are displayed
        onView(withId(R.id.startStopButton))
                .check(matches(isDisplayed()));

        onView(withId(R.id.bannerMenu))
                .check(matches(isDisplayed()));

        // Check if seekbars are displayed
        onView(withId(R.id.colorLevelBar))
                .check(matches(isDisplayed()));

        onView(withId(R.id.brightnessLevelBar))
                .check(matches(isDisplayed()));

        // Check if spinner is displayed
        onView(withId(R.id.colorSpinner))
                .check(matches(isDisplayed()));
    }

    @Test
    public void startStopButton_shouldBeClickable() {
        // Simple test to verify the start/stop button can be clicked
        // without causing crashes

        onView(withId(R.id.startStopButton))
                .check(matches(isDisplayed()))
                .perform(click());

        // After click, the button should still be visible
        onView(withId(R.id.startStopButton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void menuButton_shouldBeClickable() {
        // Test that menu button can be clicked without crashes

        onView(withId(R.id.bannerMenu))
                .check(matches(isDisplayed()))
                .perform(click());

        // After click, main UI should still be visible
        onView(withId(R.id.startStopButton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void seekBars_shouldBeVisibleAndAccessible() {
        // Test that seekbars are present and can receive interactions

        onView(withId(R.id.colorLevelBar))
                .check(matches(isDisplayed()))
                .perform(ViewActions.click()); // Simple click to test accessibility

        onView(withId(R.id.brightnessLevelBar))
                .check(matches(isDisplayed()))
                .perform(ViewActions.click());
    }

    @Test
    public void colorSpinner_shouldBeClickable() {
        // Test that color spinner can be clicked

        onView(withId(R.id.colorSpinner))
                .check(matches(isDisplayed()))
                .perform(click());

        // After clicking spinner, main UI should still be accessible
        onView(withId(R.id.startStopButton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void app_shouldMaintainBasicFunctionalityAfterInteractions() {
        // Test sequence of basic interactions to ensure app remains stable

        // 1. Click start/stop button
        onView(withId(R.id.startStopButton)).perform(click());

        // 2. Click menu button
        onView(withId(R.id.bannerMenu)).perform(click());

        // 3. Click color spinner
        onView(withId(R.id.colorSpinner)).perform(click());

        // 4. Click seekbars
        onView(withId(R.id.colorLevelBar)).perform(ViewActions.click());
        onView(withId(R.id.brightnessLevelBar)).perform(ViewActions.click());

        // 5. Verify app is still functional
        onView(withId(R.id.startStopButton))
                .check(matches(isDisplayed()))
                .perform(click()); // Should be clickable again
    }
}
