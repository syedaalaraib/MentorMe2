package com.laraib.i210865

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditTextInputTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LOGIN::class.java)

    @Test
    fun testEditTextInput() {
        // Type text into the EditText with id "enteremail"
        onView(withId(R.id.enteremail)).perform(typeText("laraib@gmail.com"), closeSoftKeyboard())

        // Check if the EditText has the expected text
        onView(withId(R.id.enteremail)).check(matches(withText("laraib@gmail.com")))
    }
}
