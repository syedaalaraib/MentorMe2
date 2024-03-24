package com.laraib.i210865

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ButtonClickTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LOGIN::class.java)

    @Test
    fun testButtonClick() {
        // Type the username and password
        onView(withId(R.id.enteremail)).perform(typeText("laraib@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.enterpassword)).perform(typeText("12345678"), closeSoftKeyboard())

        // Click the login button
        onView(withId(R.id.login)).perform(click())

//        // Check if the TextView with id "textView" displays the expected text after successful login
//        onView(withId(R.id.textView)).check(matches(withText("Login successful")))

        // Click the Button with id "button"
        onView(withId(R.id.login)).perform(click())

        // Check if the TextView with id "textView" displays the expected text after button click
        //onView(withId(R.id.textView)).check(matches(withText("Button clicked!")))
    }
}
