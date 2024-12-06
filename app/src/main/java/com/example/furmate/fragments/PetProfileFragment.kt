package com.example.furmate.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.furmate.HomeActivity
import com.example.furmate.PetProfileHomeFragment
import com.example.furmate.PetRecordFragment
import com.example.furmate.PetScheduleFragment
import com.example.furmate.R
import com.example.furmate.db.PetRepositoryAPI
import com.example.furmate.utils.ImageLoader
import com.example.furmate.viewmodels.PetViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore

class PetProfileFragment : Fragment() {
    val petViewModel: PetViewModel by activityViewModels()
    companion object {
        private const val ARG_PET_ID = "pet_id"
        const val PET_NAME_KEY = "pet_name_key"
        fun newInstance(petId: String): PetProfileFragment {
            val fragment = PetProfileFragment()
            val args = Bundle()
            args.putString(ARG_PET_ID, petId)
            fragment.arguments = args
            Log.d("FormAddBookFragment", "${fragment.arguments} in pet profile fragment")
            return fragment
        }
    }

    private lateinit var petRepositoryAPI: PetRepositoryAPI

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.screen_pet_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize firestore and collections
        val firestore = FirebaseFirestore.getInstance()
        val petCollection = firestore.collection("Pet") // Adjust collection name as needed
        petRepositoryAPI = PetRepositoryAPI(petCollection)

        // Get references to TabLayout and ViewPager2
        val tabLayout = view.findViewById<TabLayout>(R.id.pet_tabs)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)
        val petNameTextView = view.findViewById<TextView>(R.id.pet_name)

        petViewModel.petData.observe(viewLifecycleOwner, Observer { updatedPet ->
            updatedPet?.let {
                // Update the pet name UI
                petNameTextView.text = it.name
                // Update the toolbar title as well
                (requireActivity() as? HomeActivity)?.changeToolbarTitle("${it.name}'s Profile")
            }
        })

        // Get pet info
        val petId = arguments?.getString(ARG_PET_ID)
        if(petId != null) {
            petRepositoryAPI.getPetByID(petCollection, petId) { pet, error ->
                if (error != null) {
                    // Handle error
                    return@getPetByID
                } else if (pet != null) {
                    // Populate UI with pet details
                    val resultBundle = Bundle().apply {
                        putString(PET_NAME_KEY, pet.name)
                    }
                    parentFragmentManager.setFragmentResult(PET_NAME_KEY, resultBundle)
                    petNameTextView.text = pet.name
                    (requireActivity() as? HomeActivity)?.changeToolbarTitle(pet.name + "'s Profile")

                    val petImageView = view.findViewById<ImageView>(R.id.imageView)
                    pet.image?.let {
                        petImageView.setImageBitmap(ImageLoader.fromBlobScaled(it, 150, 150))
                    }

                } else {
                    petNameTextView.text = "Pet not found"
                }
            }
        } else {
            Log.e("PetProfileFragment", "Pet ID is null")
            petNameTextView.text = "Pet not found"
        }


        // Set up ViewPager2 with the adapter
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3
            override fun createFragment(position: Int): Fragment {
                val petID = arguments?.getString(ARG_PET_ID)
                return when (position) {
                    0 -> {
                        Log.d("PetProfileFragment", "Creating PetProfileHomeFragment")
                        PetProfileHomeFragment.newInstance(petID ?: "unknown")
                    }
                    1 ->{
                        if (petID.isNullOrEmpty()) {
                            Log.e("FormAddBookFragment", "petID is null or empty. Defaulting to 'unknown'")
                        } else {
                            Log.d("FormAddBookFragment", "Passing petID: $petID to PetBookFragment")
                        }
                        PetRecordFragment.newInstance(petID ?: "unknown")
                    }
                    2 -> PetScheduleFragment.newInstance(petName = petNameTextView.text.toString())
                    else -> throw IllegalArgumentException("Invalid position")
                }
            }
        }
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.icon = when (position) {
                0 -> resources.getDrawable(R.drawable.profile, null)
                1 -> resources.getDrawable(R.drawable.record, null)
                2 -> resources.getDrawable(R.drawable.schedule, null)
                else -> null
            }
        }.attach()

    }

}
