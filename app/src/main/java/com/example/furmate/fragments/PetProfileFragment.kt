package com.example.furmate.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.furmate.R

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
}
