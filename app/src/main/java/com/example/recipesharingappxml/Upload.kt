package com.example.recipesharingappxml

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.*

class Upload : Fragment() {

    private lateinit var title: EditText
    private lateinit var ingredients: EditText
    private lateinit var steps: EditText
    private lateinit var tagss : EditText
    private lateinit var chooseImage: TextView
    private lateinit var recipeImage: ImageView
    private lateinit var post: Button
    private lateinit var uploadAnimation : LottieAnimationView
    private lateinit var progressBar: ProgressBar
    private lateinit var spinner: Spinner
    private lateinit var firestore: FirebaseFirestore
    private lateinit var selectedItem : String
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private var flag : Boolean = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_upload, container, false)

        uploadAnimation = view.findViewById(R.id.uploadAnimationView)
        progressBar = view.findViewById(R.id.progressBarU)
        progressBar.visibility = View.INVISIBLE
        title = view.findViewById(R.id.title)
        ingredients = view.findViewById(R.id.ingredients)
        steps = view.findViewById(R.id.steps)
        chooseImage = view.findViewById(R.id.ChooseImage)
        recipeImage = view.findViewById(R.id.recipeImage)
        post = view.findViewById(R.id.postBtn)
        firestore = FirebaseFirestore.getInstance()
        spinner = view.findViewById(R.id.spinner)
        tagss = view.findViewById(R.id.tagsU)

        val spinnerItems = resources.getStringArray(R.array.spinner_items)
        val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Get the selected item from the spinner
                    selectedItem = spinnerItems[position]
                }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }




        chooseImage.setOnClickListener {
            openFileChooser()
        }

        post.setOnClickListener {
            uploadRecipe()
        }

        return view
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun startImageCropActivity(uri: Uri) {
        CropImage.activity(uri)
            .setAspectRatio(3, 2)
            .setOutputCompressQuality(90) // Adjust compression quality as needed
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            startImageCropActivity(imageUri!!)
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val croppedImageUri = result.uri
                recipeImage.setImageURI(croppedImageUri)
                imageUri = croppedImageUri // Update image URI to cropped image URI
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

     @RequiresApi(Build.VERSION_CODES.O)
     private fun uploadRecipe() {


        // Show progress bar
        progressBar.visibility = View.VISIBLE

        val titleText = title.text.toString().trim()
        val ingredientsText = ingredients.text.toString().trim()
        val stepsText = steps.text.toString().trim()
         val tags = tagss.text.toString().trim()

         val currentDate = LocalDate.now()

        if (titleText.isEmpty() || ingredientsText.isEmpty() || stepsText.isEmpty() || imageUri == null || selectedItem=="choose here") {
            Toast.makeText(
                requireContext(),
                "Please fill in all fields and choose an image",
                Toast.LENGTH_SHORT
            ).show()
            // Hide progress bar if validation fails
            progressBar.visibility = View.GONE
            return
        }

        // Get user ID and name from SharedPreferences
        val sharedPreferences =
            requireActivity().getSharedPreferences("login", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userid", "")
        val userName = sharedPreferences.getString("username", "")

        // Convert image URI to a Bitmap
        val bitmap =
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)

        // Compress the Bitmap
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            50,
            outputStream
        ) // Adjust compression quality as needed
        val compressedImage = ByteArrayInputStream(outputStream.toByteArray())

        // Get reference to Firebase Storage
        val storageReference =
            FirebaseStorage.getInstance().reference.child("recipe_images/${UUID.randomUUID()}")

        // Upload compressed image to Firebase Storage
        val uploadTask = storageReference.putStream(compressedImage)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Hide progress bar

        val saves = 0
            // Get download URL of uploaded image
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                // Store recipe data in Firestore with image URL
                val recipeData = hashMapOf(
                    "type" to selectedItem,
                    "title" to titleText,
                    "ingredients" to ingredientsText,
                    "steps" to stepsText,
                    "userId" to userId,
                    "userName" to userName,
                    "imageUrl" to uri.toString(),
                    "tags" to tags,
                    "date" to currentDate.toString(),
                    "day" to getCurrentDay().toString(),
                    "week" to getCurrentWeek().toString(),
                    "month" to getCurrentMonth().toString(),
                    "saves" to saves.toString()
                    // Add more fields if needed
                )

                // Add the recipe data to Firestore
                firestore.collection("Recipes").add(recipeData)
                    .addOnSuccessListener {
                        // Play Lottie animation when upload is successful
                        progressBar.visibility = View.GONE
                        uploadAnimation.playAnimation()
                        Toast.makeText(
                            requireContext(),
                            "Recipe uploaded successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Clear input fields after successful upload
                        title.setText("")
                        tagss.setText("")
                        ingredients.setText("")
                        steps.setText("")
                        spinner.setSelection(0)
                        recipeImage.setImageDrawable(null)
                        imageUri = null
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            requireContext(),
                            "Failed to upload recipe: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                uploadAnimation.cancelAnimation()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(
                requireContext(),
                "Failed to upload image: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            // Hide progress bar on failure
            progressBar.visibility = View.GONE
        }
    }



}
