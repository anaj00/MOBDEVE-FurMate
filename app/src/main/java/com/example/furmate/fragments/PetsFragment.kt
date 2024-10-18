import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

            // Add the pet item to the GridLayout
            gridLayout.addView(petItemView)
        }


        return rootView
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
