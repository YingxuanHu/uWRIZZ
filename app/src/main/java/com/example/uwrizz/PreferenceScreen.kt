import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RangeSlider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.uwrizz.AgeSelector
import com.example.uwrizz.BasicUserInfo
import com.example.uwrizz.MultiSelect
import com.example.uwrizz.R
import com.example.uwrizz.UserPreference
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import androidx.compose.material.Surface
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.RangeSlider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.OutlinedTextField

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PreferencesScreen(
    onNavigateToProfile: () -> Unit
) {
    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()

    val scrollState = rememberScrollState()

    var age by remember { mutableStateOf(18f) } // Default initial age
    var age2 by remember { mutableStateOf(30f) }
    var showAgeSlider by remember { mutableStateOf(false) }

    val genderOptions2 = listOf("Male", "Female", "Other") // Define your options here
    var selectGenders2 by remember { mutableStateOf(listOf<String>()) }

    val programOptions2 = listOf("Arts", "Engineering", "Environment", "Health", "Mathematics", "Science") // Define your options here
    var selectedPrograms2 by remember { mutableStateOf(listOf<String>()) }

    val ethnicityOptions2 = listOf("Black/African Descent", "East Asian", "Hispanic/Latino",
        "Middle Eastern", "Native", "Pacific Islander", "South Asian", "South East Asian", "White/Caucasian", "Other") // Define your options here
    var selectedEthnicity2 by remember { mutableStateOf(listOf<String>()) }

    remember {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val prefCollection = db.collection("preferences")
            val userRef = prefCollection.whereEqualTo("userId", userId)

            userRef.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val preference =
                            querySnapshot.documents[0].toObject(UserPreference::class.java)
                        if (preference != null) {
                            // Update mutable state variables with user data
                            age = preference.agePreferenceMin.toFloat()
                            age2 = preference.agePreferenceMax.toFloat()
                            selectGenders2 = preference.interestedInGender
                            selectedPrograms2 = preference.interestedInProgram
                            selectedEthnicity2 = preference.interestedInEthnicity
                        }
                    } else {
                        Log.d(
                            "PreferenceScreen",
                            "No matching document found for the userId"
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("PreferenceScreen", "get failed with ", exception)
                }
        } else {
            Log.d("PreferenceScreen", "User not authenticated or UID is null")
        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(scrollState) // This adds the scrolling behavior
            .fillMaxHeight() // This makes the Column fill the available height
            .padding(16.dp) // Replace with your desired padding
    ) {
        Spacer(Modifier.weight(1f)) // This is used for layout purposes
        Button(
            onClick = onNavigateToProfile,
            modifier = Modifier
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) { // Align icon and text vertically
                Icon(
                    ImageVector.vectorResource(id = R.drawable.ic_arrow), // Replace with your icon's resource ID
                    contentDescription = "Edit Profile" // Accessibility description
                )
                Spacer(Modifier.width(4.dp)) // Add some spacing between the icon and the text
                Text("Profile") // Text following the icon
            }
        }

        Spacer(modifier = Modifier.height(50.dp))
        Text("Preference Settings", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(24.dp))

        MultiSelect(
            options = genderOptions2,
            selectedOptions = selectGenders2,
            onOptionSelected = { option, isSelected ->
                selectGenders2 = if (isSelected) {
                    selectGenders2 + option
                } else {
                    selectGenders2 - option
                }
            },
            label = "I'm Interested In (Gender)"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MultiSelect(
            options = ethnicityOptions2,
            selectedOptions = selectedEthnicity2,
            onOptionSelected = { option, isSelected ->
                selectedEthnicity2 = if (isSelected) {
                    selectedEthnicity2 + option
                } else {
                    selectedEthnicity2 - option
                }
            },
            label = "I'm Interested In (Ethnicity)"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MultiSelect(
            options = programOptions2,
            selectedOptions = selectedPrograms2,
            onOptionSelected = { option, isSelected ->
                selectedPrograms2 = if (isSelected) {
                    selectedPrograms2 + option
                } else {
                    selectedPrograms2 - option
                }
            },
            label = "I'm Interested In (Program)"
        )
        var showSlider by remember { mutableStateOf(false) } // State to control the visibility of the slider

        Column() {
            OutlinedTextField(
                value = "Ages: ${age.toInt()} to ${age2.toInt()}",
                onValueChange = {},
                label = { Text("Select Age Range") },
                readOnly = true, // Makes it non-editable
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        "Select Age Range",
                        Modifier.clickable { showSlider = !showSlider })
                },
                modifier = Modifier
                    .clickable { showSlider = !showSlider }
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            if (showSlider) {
                RangeSlider(
                    value = age..age2,
                    onValueChange = { newRange ->
                        if (newRange.start == newRange.endInclusive) {
                            // If both sliders are at the same position, increment age2 by 1
                            age = newRange.start
                            age2 = newRange.endInclusive + 1
                        } else if (newRange.start < newRange.endInclusive) {
                            // If the range is valid, update age and age2 accordingly
                            age = newRange.start
                            age2 = newRange.endInclusive
                        } else {
                            // If the start value is greater than the end value, swap them
                            age = newRange.endInclusive
                            age2 = newRange.start
                        }
                    },
                    steps = 30 - 18 - 1,
                    valueRange = 18f..30f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
        }

        // Save button
        Button(
            onClick = {
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    // Handle the case where the user is not authenticated or the UID is null
                    Log.e("Preference", "User not authenticated or UID is null")
                    return@Button
                }
                val prefCollection = db.collection("preferences")
                val userRef = prefCollection.whereEqualTo("userId", userId)
                val updatedUser = UserPreference(
                    userId = auth.currentUser?.uid as String,
                    interestedInGender = selectGenders2,
                    interestedInEthnicity = selectedEthnicity2,
                    interestedInProgram = selectedPrograms2,
                    agePreferenceMin = age.toInt(),
                    agePreferenceMax = age2.toInt()

                )
                userRef.get()
                    .addOnSuccessListener { querySnapshot ->
                        // Assuming only one document should match the query
                        val document = querySnapshot.documents.firstOrNull()
                        if (document != null) {
                            val docId = document.id // Get the document ID
                            prefCollection.document(docId)
                                .set(updatedUser) // Update the document with updatedUser data
                                .addOnSuccessListener {
                                    Log.d("Preference", "DocumentSnapshot successfully updated!")
                                    // Handle success
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Preference", "Error updating document", e)
                                    // Handle failure
                                }
                        } else {
                            Log.d("Preference", "No matching document found for the userId")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w("Preference", "Error getting documents: ", exception)
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Save")
        }
    }
}