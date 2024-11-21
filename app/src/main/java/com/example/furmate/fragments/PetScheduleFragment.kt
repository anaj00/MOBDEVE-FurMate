package com.example.furmate


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class PetScheduleFragment : Fragment() {

    companion object {
        fun newInstance(): PetScheduleFragment {
            return PetScheduleFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,  container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.screen_pet_profile_schedule, container, false)
        return rootView
    }
}