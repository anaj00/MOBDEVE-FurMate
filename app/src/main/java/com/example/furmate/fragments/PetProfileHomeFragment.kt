package com.example.furmate


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class PetProfileHomeFragment : Fragment() {

    companion object {
        fun newInstance(): PetProfileHomeFragment {
            return PetProfileHomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,  container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.screen_pet_profile_main, container, false)
        return rootView
    }
}