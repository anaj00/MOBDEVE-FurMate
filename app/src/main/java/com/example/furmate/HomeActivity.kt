package com.example.furmate

import CalendarFragment
import PetsFragment
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.transition.TransitionManager
import com.example.furmate.fragments.HomeFragment
import com.example.furmate.fragments.FormScheduleFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity(), FragmentNavigator {
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

    private lateinit var profileButton: ImageButton
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_home)

        mainFAB = findViewById(R.id.add_fab)
        scheduleFAB = findViewById(R.id.add_schedule)
        recordFAB = findViewById(R.id.add_record)
        profileButton = findViewById(R.id.profile_button)
        toolbar = findViewById(R.id.top_toolbar)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // create default back stacks for all fragments in bottom menu
        createFragments()

        // Handle bottom navigation item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> loadFragment(FragmentName.HOME.name)
                R.id.calendar -> loadFragment(FragmentName.CALENDAR.name)
                R.id.pets -> loadFragment(FragmentName.PETS.name)
            }
            showFABs()
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

        val profileButton: ImageButton = findViewById(R.id.profile_button)

        profileButton.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.profile_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.logout -> {
                        logOut()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    fun changeToolbarTitle(title: String) {
        toolbar.title = title
    }

    fun logOut() {
        val auth = Firebase.auth
        val currentUser = auth.currentUser

        var googleLogout = false
        var facebookLogout = false

        currentUser?.let {
            for (profile in it.providerData) {
                val providerId = profile.providerId

                if (providerId == "google.com") {
                    googleLogout = true
                }

                if (providerId == "facebook.com") {
                    facebookLogout = true
                }

                Log.d("UserProvider", providerId)
            }
        }

        auth.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("KEY_GOOGLE_LOGOUT", googleLogout)
        intent.putExtra("KEY_FACEBOOK_LOGOUT", facebookLogout)
        startActivity(intent)
        finish() // Finish this activity so it cannot be accessed via back button
    }

    override fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
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

    public fun handleFABClick() {
        mainFAB.setOnClickListener {
            toggleFABMenu()
        }
    }

    public fun hideFABs() {
        mainFAB.visibility = View.GONE
        scheduleFAB.visibility = View.GONE
        recordFAB.visibility = View.GONE
    }

    public fun toggleFABMenu() {
        if (isFabMenuOpen) {
            closeFabMenu()
        } else {
            openFabMenu()
        }
    }

    public fun openFabMenu() {
        val fadeThrough = MaterialFadeThrough().apply { duration = 300 }
        TransitionManager.beginDelayedTransition(findViewById(R.id.home_coord), fadeThrough)
        scheduleFAB.visibility = View.VISIBLE
        recordFAB.visibility = View.VISIBLE
        isFabMenuOpen = true
    }

    public fun closeFabMenu() {
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
