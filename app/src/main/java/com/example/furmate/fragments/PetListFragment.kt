import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.gridlayout.widget.GridLayout
import com.example.furmate.R
import com.example.furmate.fragments.FormAddPetFragment
import com.example.furmate.fragments.PetProfileFragment

data class Pet(val name: String, val id: Int)

class PetsFragment : Fragment() {

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

        // Set up the list of pets
        val pets = getSamplePets()

        // Dynamically add pet items to GridLayout
        for (pet in pets) {
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

            petItemView.setOnClickListener(){
                openPetProfile(pet)
            }

            // Add the pet item to the GridLayout
            gridLayout.addView(petItemView)
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

        return rootView
    }

    private fun openPetProfile(pet: Pet) {
        val fragment = PetProfileFragment.newInstance(pet.id)
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
    private fun getSamplePets(): List<Pet> {
        return listOf(
            Pet("Moo Deng", 123),
            Pet("Booboo", 124),
            Pet("Baabaa", 125),
            Pet("Dodo", 126),
            Pet("Milo", 127),
            Pet("Fido", 128)
        )
    }
}
