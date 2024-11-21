package com.example.furmate.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.furmate.PetProfileHomeFragment
import com.example.furmate.PetRecordsFragment
import com.example.furmate.PetScheduleFragment
import com.example.furmate.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PetProfileFragment : Fragment() {

    companion object {
        private const val ARG_PET_ID = "pet_id"

        fun newInstance(petId: Int): PetProfileFragment {
            val fragment = PetProfileFragment()
            val args = Bundle()
            args.putInt(ARG_PET_ID, petId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.screen_pet_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get references to TabLayout and ViewPager2
        val tabLayout = view.findViewById<TabLayout>(R.id.pet_tabs)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)

        // Set up ViewPager2 with the adapter
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> PetProfileHomeFragment.newInstance()
                    1 -> PetRecordsFragment.newInstance()
                    2 -> PetScheduleFragment.newInstance()
                    else -> throw IllegalArgumentException("Invalid position")
                }
            }
        }
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.icon = when (position) {
                0 -> resources.getDrawable(R.drawable.profile, null) // Replace with your profile icon drawable
                1 -> resources.getDrawable(R.drawable.schedule, null) // Replace with your schedule icon drawable
                2 -> resources.getDrawable(R.drawable.record, null) // Replace with your records icon drawable
                else -> null
            }
        }.attach()

    }

}
