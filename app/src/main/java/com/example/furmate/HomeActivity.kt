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
import android.util.Log
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
        loadInitialFragment(HomeFragment())

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

        // Handle click on Add Schedule
        scheduleFAB.setOnClickListener {
            openFormSchedule(isSchedule = true)
        }

        // Handle click on Add Record
        recordFAB.setOnClickListener {
            openFormSchedule(isSchedule = false)
        }

    }

    private fun openFormSchedule(
        isSchedule: Boolean,
        title: String? = null,
        date: String? = null,
        where: String? = null,
        pet: String? = null,
        notes: String? = null
    ) {
        // Load form_schedule fragment with pre-filled data and hide FABs
        hideFABs()
        val fragment = FormScheduleFragment.newInstance(isSchedule, title, date, where, pet, notes)
        loadFragment(fragment)
    }

    private fun handleFABClick() {
        mainFAB.setOnClickListener {
            toggleFABMenu()
        }
    }

    private fun hideFABs() {
        mainFAB.visibility = View.GONE
        scheduleFAB.visibility = View.GONE
        recordFAB.visibility = View.GONE
    }

    private fun toggleFABMenu() {
        if (isFabMenuOpen) {
            closeFabMenu()
        } else {
            openFabMenu()
        }
    }

    private fun openFabMenu() {
        val fadeThrough = MaterialFadeThrough().apply { duration = 300 }
        TransitionManager.beginDelayedTransition(findViewById(R.id.home_coord), fadeThrough)
        scheduleFAB.visibility = View.VISIBLE
        recordFAB.visibility = View.VISIBLE
        isFabMenuOpen = true
    }

    private fun closeFabMenu() {
        val fadeThrough = MaterialFadeThrough().apply { duration = 300 }
        TransitionManager.beginDelayedTransition(findViewById(R.id.home_coord), fadeThrough)
        scheduleFAB.visibility = View.GONE
        recordFAB.visibility = View.GONE
        isFabMenuOpen = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isFabMenuOpen && event.action == MotionEvent.ACTION_DOWN) {
            val fabRect = Rect()
            val fab = findViewById<FloatingActionButton>(R.id.add_fab)
            fab.getGlobalVisibleRect(fabRect)

            if (!fabRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                closeFabMenu()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun loadInitialFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        // Get the current fragment
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        // If we are in FormScheduleFragment, show the FABs again
        if (currentFragment is FormScheduleFragment) {
            showFABs()
        }

        // Call the default onBackPressed behavior to pop the fragment back stack
        super.onBackPressed()
    }

    private fun showFABs() {
        mainFAB.visibility = View.VISIBLE
        scheduleFAB.visibility = View.GONE // Keep these hidden until the main FAB is clicked again
        recordFAB.visibility = View.GONE
    }
}
