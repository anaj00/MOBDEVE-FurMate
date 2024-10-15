import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.gridlayout.widget.GridLayout
import com.example.furmate.R

data class Pet(val name: String, val id: Int)

class PetsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout
        val rootView = inflater.inflate(R.layout.fragment_pets, container, false)

        // Find the GridLayout in the layout
        val gridLayout = rootView.findViewById<GridLayout>(R.id.grid_layout_pets)

        // Set up the list of pets
        val pets = getSamplePets()

        // Dynamically add pet items to GridLayout
        for (pet in pets) {
            // Inflate a new pet button view for each pet
            val petItemView = inflater.inflate(R.layout.pets_button, gridLayout, false)

            // Find and set the pet's name and icon
            val petNameView = petItemView.findViewById<TextView>(R.id.pet_name)
            petNameView.text = pet.name

//            // Set LayoutParams for proper spacing and alignment
//            val params = GridLayout.LayoutParams().apply {
//                width = GridLayout.LayoutParams.WRAP_CONTENT
//                height = GridLayout.LayoutParams.WRAP_CONTENT
//                setGravity(Gravity.CENTER)
//                setMargins(16, 16, 16, 16) // Add margins around the item
//            }
//
//            // Apply LayoutParams to the item view
//            petItemView.layoutParams = params

            // Add the pet item view to the GridLayout
            gridLayout.addView(petItemView)
        }

        return rootView
    }

    private fun getSamplePets(): List<Pet> {
        return listOf(
            Pet("Moo Deng", 123),
            Pet("Booboo", 124),
            Pet("Baabaa", 125)
        )
    }
}
