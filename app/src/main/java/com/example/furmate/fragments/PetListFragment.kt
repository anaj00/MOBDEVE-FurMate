import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.gridlayout.widget.GridLayout
import com.example.furmate.R
import com.example.furmate.db.PetRepositoryAPI
import com.example.furmate.fragments.FormAddPetFragment
import com.example.furmate.fragments.PetProfileFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.example.furmate.models.Pet

class PetsFragment : Fragment() {
    private lateinit var petRepositoryAPI: PetRepositoryAPI

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout
        val rootView = inflater.inflate(R.layout.screen_pets, container, false)

        // Find the GridLayout in the layout
        val gridLayout = rootView.findViewById<GridLayout>(R.id.grid_layout_pets)

        // Set the column count for a two-column layout
        gridLayout.columnCount = 2

        val firestore = FirebaseFirestore.getInstance()
        val petCollection = firestore.collection("Pet") // Adjust collection name as needed
        petRepositoryAPI = PetRepositoryAPI(petCollection)


        // Get all pets from the database
        // Set up the list of pets
        getAllPets { pets, error ->
            if (error != null) {
                Log.e("PetsFragment", "Error fetching pets: $error")
                return@getAllPets
            }
            // Add a button to add a new pet
            val petAdd = inflater.inflate(R.layout.button_addpet, gridLayout, false)
            petAdd.post {
                val width = petAdd.width
                val layoutParams = petAdd.layoutParams
                layoutParams.height = width
                petAdd.layoutParams = layoutParams
            }

            petAdd.setOnClickListener {
                openAddPetForm()
            }

            gridLayout.addView(petAdd)
            // Dynamically add pet items to GridLayout
            pets?.let { petList ->
                for (pet in petList) {
                    val petItemView = inflater.inflate(R.layout.composable_pet_button, gridLayout, false)

                    // Set the pet name
                    val petNameView = petItemView.findViewById<TextView>(R.id.pet_name)
                    petNameView.text = pet.name

                    // Measure the width of the card to set the height equal to the width (for a square appearance)
                    petItemView.post {
                        val width = petItemView.width
                        val layoutParams = petItemView.layoutParams
                        layoutParams.height = width
                        petItemView.layoutParams = layoutParams
                    }

                    petItemView.setOnClickListener {
                        openPetProfile(pet)
                    }

                    // Add the pet item to the GridLayout
                    gridLayout.addView(petItemView)
                }
            }
        }
        return rootView
    }

    private fun openPetProfile(pet: Pet) {
        val fragment = PetProfileFragment.newInstance(pet.id ?: -1)
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }

    private fun openAddPetForm() {
        val fragment = FormAddPetFragment()
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }

    // Sample pets for demonstration
    private fun getAllPets(callback: (List<Pet>?, Exception?) -> Unit) {
        petRepositoryAPI.getAllPets { pets, error ->
            if (error != null) {
                callback(null, error) // Pass the error to the callback
            } else {
                callback(pets, null) // Pass the fetched pets to the callback
            }
        }
    }

}
