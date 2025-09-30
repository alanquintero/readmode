/*****************************************************************
 * Copyright (C) 2025 Alan Quintero <https://github.com/alanquintero/>
 *****************************************************************/
package com.contilabs.readmode;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.contilabs.readmode.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Smoke test - most basic test to verify the app launches and shows main screen
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SmokeTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void appLaunchesSuccessfully() {
        // This is the most basic test - if this fails, something is fundamentally wrong
        // with the app launch or the test setup

        // Just check that the main button is displayed
        // This doesn't require any complex interactions
        onView(withId(R.id.startStopButton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void mainScreenComponentsAreVisible() {
        // Check that all main screen components are visible
        // without attempting any interactions

        onView(withId(R.id.startStopButton)).check(matches(isDisplayed()));
        onView(withId(R.id.bannerMenu)).check(matches(isDisplayed()));
        onView(withId(R.id.colorSpinner)).check(matches(isDisplayed()));
        onView(withId(R.id.colorLevelBar)).check(matches(isDisplayed()));
        onView(withId(R.id.brightnessLevelBar)).check(matches(isDisplayed()));
    }
}
