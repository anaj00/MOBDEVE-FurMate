package com.example.furmate

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.transition.TransitionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialFadeThrough

import CalendarFragment
import HomeFragment
import PetsFragment
import android.graphics.Rect
import android.view.MotionEvent
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class HomeActivity : AppCompatActivity() {

    private var isFabMenuOpen = false

    // Declare FABs here so they are only initialized once
    private lateinit var mainFAB: FloatingActionButton
    private lateinit var scheduleFAB: ExtendedFloatingActionButton
    private lateinit var recordFAB: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_home)

        // Initialize the FABs once
        mainFAB = findViewById(R.id.add_fab)
        scheduleFAB = findViewById(R.id.add_schedule)
        recordFAB = findViewById(R.id.add_record)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set default fragment
        loadFragment(HomeFragment())

        // Handle bottom navigation item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadFragment(HomeFragment())
                R.id.calendar -> loadFragment(CalendarFragment())
                R.id.pets -> loadFragment(PetsFragment())
            }
            true
        }

        // Handle FAB click to toggle menu
        handleFABClick()
    }

    // Function to handle FAB click
    private fun handleFABClick() {
        mainFAB.setOnClickListener {
            toggleFABMenu()
        }
    }

    // Function to toggle FAB menu
    private fun toggleFABMenu() {
        if (isFabMenuOpen) {
            closeFabMenu()
        } else {
            openFabMenu()
        }
    }

    // Function to open FAB menu
    private fun openFabMenu() {
        // Use MaterialFadeThrough for a smooth transition
        val fadeThrough = MaterialFadeThrough().apply {
            duration = 300
        }

        // Begin transition using TransitionManager
        TransitionManager.beginDelayedTransition(findViewById(R.id.home_coord), fadeThrough)

        // Show the smaller FABs
        scheduleFAB.visibility = View.VISIBLE
        recordFAB.visibility = View.VISIBLE

        isFabMenuOpen = true
    }

    // Function to close FAB menu
    private fun closeFabMenu() {
        // Use MaterialFadeThrough for a smooth transition
        val fadeThrough = MaterialFadeThrough().apply {
            duration = 300
        }

        // Begin transition using TransitionManager
        TransitionManager.beginDelayedTransition(findViewById(R.id.home_coord), fadeThrough)

        // Hide the smaller FABs
        scheduleFAB.visibility = View.GONE
        recordFAB.visibility = View.GONE

        isFabMenuOpen = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // If the FAB menu is open and the user touches outside the FAB, close the menu
        if (isFabMenuOpen && event.action == MotionEvent.ACTION_DOWN) {
            val fabRect = Rect()
            val fab = findViewById<FloatingActionButton>(R.id.add_fab)

            // Get the visible bounds of the main FAB
            fab.getGlobalVisibleRect(fabRect)

            // Check if the touch event is outside the FAB's visible bounds
            if (!fabRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                closeFabMenu() // Close the FAB menu if touched outside
            }
        }
        return super.onTouchEvent(event)
    }


    // Function to load a fragment
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
