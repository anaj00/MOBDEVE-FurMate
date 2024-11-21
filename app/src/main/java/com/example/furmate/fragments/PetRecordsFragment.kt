package com.example.furmate


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class PetRecordsFragment : Fragment() {

    companion object {
        fun newInstance(): PetRecordsFragment {
            return PetRecordsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,  container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.screen_pet_profile_record, container, false)
        return rootView
    }
}