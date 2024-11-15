package com.example.furmate

import CalendarFragment
import HomeFragment
import PetsFragment
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.transition.TransitionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.Date


class HomeActivity : AppCompatActivity() {
    companion object {
        enum class FragmentName {
            HOME, CALENDAR, PETS
        }
    }

    private var isFabMenuOpen = false

    // store name of current fragment as key to save back stack state
    private var currentFragmentName : String? = null

    // store current fragments to prevent them from being regenerated
    private lateinit var fragments : HashMap<String, Fragment>

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

        // create default back stacks for all fragments in bottom menu
        createFragments()

        // Handle bottom navigation item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(FragmentName.HOME.name)
                    showFABs()
                }
                R.id.calendar -> {
                    loadFragment(FragmentName.CALENDAR.name)
                    hideFABs()
                }
                R.id.pets -> {
                    loadFragment(FragmentName.PETS.name)
                    hideFABs()
                }
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
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container, fragment)
            // can safely add to back stack since this button is only visible
            // while the user is on the HomeScreen
            addToBackStack(null)
        }
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

    private fun createFragments() {
        fragments = HashMap()
        fragments[FragmentName.CALENDAR.name] = CalendarFragment()
        fragments[FragmentName.PETS.name] = PetsFragment()
        fragments[FragmentName.HOME.name] = HomeFragment()

        currentFragmentName = FragmentName.HOME.name
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container, fragments[currentFragmentName]!!)
        }
    }

    private fun loadFragment(name: String) {
        if (currentFragmentName != null) {
            supportFragmentManager.saveBackStack(currentFragmentName!!)
        }

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container, fragments[name]!!)
        }

        supportFragmentManager.restoreBackStack(name)
        currentFragmentName = name
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
