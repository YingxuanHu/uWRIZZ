package com.example.uwrizz

import com.example.uwrizz.ui.theme.UWRizzTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.*
import androidx.compose.ui.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.vectorResource
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.rememberImagePainter
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.filled.AccountCircle
import android.content.Context
import android.content.SharedPreferences

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore



@Composable
fun ProfileSettingsScreen(
    profileImage: ImageVector, // Placeholder for profile image
    onImageClick: () -> Unit, // Placeholder click action for adding an image
    onImageSelected: (Uri) -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToSurvey: () -> Unit,
    context: Context
) {
    UWRizzTheme {
        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()

        var imageUri by rememberSaveable{ mutableStateOf<Uri?>(null) }
        var imageUri1 by rememberSaveable { mutableStateOf<Uri?>(null) }
        var imageUri2 by rememberSaveable { mutableStateOf<Uri?>(null) }
        var imageUri3 by rememberSaveable { mutableStateOf<Uri?>(null) }
        var currentImageSelection by rememberSaveable { mutableStateOf(0) }


        var firstname by rememberSaveable { mutableStateOf("") }
        var lastname by rememberSaveable { mutableStateOf("") }
        var bio by rememberSaveable { mutableStateOf("") }
        var hobby by rememberSaveable { mutableStateOf("") }
        var job by rememberSaveable { mutableStateOf("") }
        var age by rememberSaveable { mutableStateOf(18f) }
        var showAgeSlider by rememberSaveable { mutableStateOf(false) }
        //Dropdown states
        var expandedGender by rememberSaveable { mutableStateOf(false) }
        val genderOptions =
            listOf("Male", "Female", "Other", "Prefer not to say") // Define your options here
        var selectedGender by rememberSaveable { mutableStateOf("Please select your gender") }

        // Program dropdown states
        var expandedProgram by rememberSaveable { mutableStateOf(false) }
        val programOptions = listOf(
            "Arts",
            "Engineering",
            "Environment",
            "Health",
            "Mathematics",
            "Science"
        ) // Define your options here
        var selectedProgram by rememberSaveable { mutableStateOf("Please select your program") }

        var expandedEthnicity by rememberSaveable { mutableStateOf(false) }
        val ethnicityOptions = listOf(
            "Black/African Descent",
            "East Asian",
            "Hispanic/Latino",
            "Middle Eastern",
            "Native",
            "Pacific Islander",
            "South Asian",
            "South East Asian",
            "White/Caucasian",
            "Other"
        ) // Define your options here
        var selectedEthnicity by rememberSaveable { mutableStateOf("Please select your ethnicity") }

        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        fun saveImageUri(uri: Uri, key: String) {
            editor.putString(key, uri.toString()).apply()
        }
        fun loadImageUri(key: String): Uri? {
            val uriString = sharedPreferences.getString(key, null)
            return uriString?.let { Uri.parse(it) }
        }

        val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                when (currentImageSelection) {
                    1 -> {
                        imageUri1 = it; saveImageUri(it, "image_uri_1")
                    }

                    2 -> {
                        imageUri2 = it; saveImageUri(it, "image_uri_2")
                    }

                    3 -> {
                        imageUri3 = it; saveImageUri(it, "image_uri_3")
                    }

                    else -> {
                        imageUri = it; saveImageUri(it, "profile_image_uri")
                    }
                }
            }
        }

        val scrollState = rememberScrollState()

        remember {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val usersCollection = db.collection("users")
                val userRef = usersCollection.whereEqualTo("userId", userId)

                userRef.get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val user =
                                querySnapshot.documents[0].toObject(BasicUserInfo::class.java)
                            if (user != null) {
                                // Update mutable state variables with user data
                                firstname = user.firstName
                                lastname = user.lastName
                                bio = user.bio
                                selectedEthnicity = user.ethnicity
                                selectedGender = user.gender
                                selectedProgram = user.program
                                hobby = user.hobby
                                job = user.job
                                age = user.age

                                // Load image URIs
                                imageUri = loadImageUri("profile_image_uri")
                                imageUri1 = loadImageUri("image_uri_1")
                                imageUri2 = loadImageUri("image_uri_2")
                                imageUri3 = loadImageUri("image_uri_3")
                            }
                        } else {
                            Log.d(
                                "ProfileSettingsScreen",
                                "No matching document found for the userId"
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("ProfileSettingsScreen", "get failed with ", exception)
                    }
            } else {
                Log.d("ProfileSettingsScreen", "User not authenticated or UID is null")
            }
        }
        Column(
            modifier = Modifier
                .verticalScroll(scrollState) // This adds the scrolling behavior
                .fillMaxHeight() // This makes the Column fill the available height
                .padding(16.dp) // Replace with your desired padding
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth() // Fill the width of the parent to allow the Spacer to push the button to the right
                    .padding(bottom = 16.dp), //
                verticalAlignment = Alignment.Top // Align items to the top
            ) {
                // Profile picture
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(118.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                        .clickable {
                            currentImageSelection = 0 // Assign a value indicating profile picture selection
                            galleryLauncher.launch("image/*") // Launch the gallery
                        }
                ) {
                    imageUri?.let { uri ->
                        Image(
                            painter = rememberImagePainter(data = uri),
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Icon(
                        // If imageUri is null, display a default icon to indicate profile picture upload
                        imageVector = Icons.Default.AccountCircle, // Use the correct default icon here
                        contentDescription = "Profile picture placeholder",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }


                Spacer(Modifier.weight(1f)) // This pushes the button to the right
                Column(horizontalAlignment = Alignment.End) {
                    // Edit Preferences button
                    Button(
                        onClick = onNavigateToPreferences,
                        modifier = Modifier
                    ) {
                        Text("Edit Preferences")
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Button(
                        onClick = onNavigateToSurvey,
                        modifier = Modifier
                    ) {
                        Text("Edit Survey")
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            Text("Insert your pictures:")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ImageUploadButton(imageUri = imageUri1) {
                    currentImageSelection = 1; galleryLauncher.launch("image/*")
                }
                ImageUploadButton(imageUri = imageUri2) {
                    currentImageSelection = 2; galleryLauncher.launch("image/*")
                }
                ImageUploadButton(imageUri = imageUri3) {
                    currentImageSelection = 3; galleryLauncher.launch("image/*")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Existing fields
            Text("Profile Settings", style = MaterialTheme.typography.h5)


            OutlinedTextField(
                value = firstname,
                onValueChange = { firstname = it },
                label = { Text("First Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            OutlinedTextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = { Text("Last Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            Log.d("Before", age.toString())
            //age
            val label = "Select Age"
            val ageRange = 18f..30f
            var showSlider by remember { mutableStateOf(false) } // State to control the visibility of the slider

            Column() {
                OutlinedTextField(
                    value = "Age: ${age.toInt()}",
                    onValueChange = {},
                    label = { Text(label) },
                    readOnly = true, // Makes it non-editable
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, "Select Age", Modifier.clickable { showSlider = !showSlider })
                    },
                    modifier = Modifier
                        .clickable { showSlider = !showSlider }
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                // Show the Slider when the OutlinedTextField is clicked
                if (showSlider) {
                    Slider(
                        value = age,
                        onValueChange = { age = it },
                        valueRange = ageRange,
                        steps = (ageRange.endInclusive.toInt() - ageRange.start.toInt()) - 1,
                        onValueChangeFinished = {
                            age = age
                            showAgeSlider = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
            Log.d("After", age.toString())

            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            //Ethnicity
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Top)
            ) {
                OutlinedTextField(
                    value = selectedEthnicity,
                    onValueChange = { /* ReadOnly TextField */ },
                    label = { Text("Ethnicity") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { expandedEthnicity = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { expandedEthnicity = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = expandedEthnicity,
                    onDismissRequest = { expandedEthnicity = false }
                ) {
                    ethnicityOptions.forEach { ethnicity ->
                        DropdownMenuItem(onClick = {
                            selectedEthnicity = ethnicity
                            expandedEthnicity = false
                        }) {
                            Text(text = ethnicity)
                        }
                    }
                }
            }

            //Gender
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Top)
            ) {
                OutlinedTextField(
                    value = selectedGender,
                    onValueChange = { /* ReadOnly TextField */ },
                    label = { Text("Gender") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { expandedGender = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { expandedGender = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = expandedGender,
                    onDismissRequest = { expandedGender = false }
                ) {
                    genderOptions.forEach { gender ->
                        DropdownMenuItem(onClick = {
                            selectedGender = gender
                            expandedGender = false
                        }) {
                            Text(text = gender)
                        }
                    }
                }
            }

            //Program
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Top)
            ) {
                OutlinedTextField(
                    value = selectedProgram,
                    onValueChange = { /* ReadOnly TextField */ },
                    label = { Text("Program") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable { expandedProgram = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Dropdown",
                            Modifier.clickable { expandedProgram = true }
                        )
                    },
                    readOnly = true // Make TextField readonly
                )
                DropdownMenu(
                    expanded = expandedProgram,
                    onDismissRequest = { expandedProgram = false }
                ) {
                    programOptions.forEach { program ->
                        DropdownMenuItem(onClick = {
                            selectedProgram = program
                            expandedProgram = false
                        }) {
                            Text(text = program)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = hobby,
                onValueChange = { hobby = it },
                label = { Text("Hobby") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            OutlinedTextField(
                value = job,
                onValueChange = { job = it },
                label = { Text("Job") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            // Save button
            Button(
                onClick = {
                    val userId = auth.currentUser?.uid
                    if (userId == null) {
                        // Handle the case where the user is not authenticated or the UID is null
                        Log.e("Profile", "User not authenticated or UID is null")
                        return@Button
                    }
                    val usersCollection = db.collection("users")
                    val userRef = usersCollection.whereEqualTo("userId", userId)
                    val updatedUser = BasicUserInfo(
                        userId = userId,
                        firstName = firstname,
                        lastName = lastname,
                        age = age,
                        ethnicity = selectedEthnicity,
                        gender = selectedGender,
                        program = selectedProgram,
                        hobby = hobby,
                        job = job,
                        bio = bio,
                        profilePictureUri = imageUri?.toString() ?: "",
                        pictureUri1 = imageUri1?.toString() ?: "",
                        pictureUri2 = imageUri2?.toString() ?: "",
                        pictureUri3 = imageUri3?.toString() ?: ""
                    )
                    userRef.get()
                        .addOnSuccessListener { querySnapshot ->
                            // Assuming only one document should match the query
                            val document = querySnapshot.documents.firstOrNull()
                            if (document != null) {
                                val docId = document.id // Get the document ID
                                usersCollection.document(docId)
                                    .set(updatedUser) // Update the document with updatedUser data
                                    .addOnSuccessListener {
                                        Log.d("Profile", "DocumentSnapshot successfully updated!")
                                        // Handle success
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("Profile", "Error updating document", e)
                                        // Handle failure
                                    }
                            } else {
                                Log.d("Profile", "No matching document found for the userId")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w("Profile", "Error getting documents: ", exception)
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
}



//Image selection
enum class ImageSource {
    Gallery,
    Camera
}

fun onImageSelected(source: ImageSource) {
    // Handle image selection based on the source (Gallery or Camera)
    when (source) {
        ImageSource.Gallery -> {
            // Logic to handle gallery image selection
        }
        ImageSource.Camera -> {
            // Logic to handle camera image capture
        }
    }
}

@Composable
fun ImageUploadButton(
    imageUri: Uri?,
    onImageClick: () -> Unit
) {
    Button(
        onClick = onImageClick,
        modifier = Modifier.size(100.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface
        )
    ) {
        imageUri?.let {
            Image(
                painter = rememberImagePainter(it),
                contentDescription = "Uploaded image",
                modifier = Modifier.size(96.dp),
                contentScale = ContentScale.Crop
            )
        } ?: Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_add),
            contentDescription = "Add picture",
            modifier = Modifier.size(24.dp)
        )
    }
}


//Preference page

//Function for multi selection
@Composable
fun MultiSelect(
    options: List<String>,
    selectedOptions: List<String>,
    onOptionSelected: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = selectedOptions.joinToString(", "),
            onValueChange = { },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Close dropdown" else "Open dropdown",
                    Modifier.clickable { expanded = !expanded }
                )
            },
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedOptions.contains(option),
                            onClick = {
                                onOptionSelected(
                                    option,
                                    !selectedOptions.contains(option)
                                )
                            }
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedOptions.contains(option),
                        onCheckedChange = { checked ->
                            onOptionSelected(option, checked)
                        }
                    )
                    Text(
                        text = option,
                        style = MaterialTheme.typography.body1.merge(),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}


