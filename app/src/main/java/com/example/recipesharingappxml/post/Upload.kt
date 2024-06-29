package com.example.recipesharingappxml.post

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.example.recipesharingappxml.R

import com.example.recipesharingappxml.services.RetrofitClient
import com.example.recipesharingappxml.services.UploadRequest
import com.example.recipesharingappxml.services.UploadResponse
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class Upload : Fragment() {

    private lateinit var title: EditText
    private lateinit var ingredients: EditText
    private lateinit var steps: EditText
    private lateinit var tagss: EditText
    private lateinit var chooseImage: TextView
    private lateinit var recipeImage: ImageView
    private lateinit var post: Button
    private lateinit var uploadAnimation: LottieAnimationView
    private lateinit var progressBar: ProgressBar
    private lateinit var spinner: Spinner
    private lateinit var firestore: FirebaseFirestore
    private lateinit var selectedItem: String
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

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
                selectedItem = spinnerItems[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
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
            .setOutputCompressQuality(90)
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



//    private fun openFileChooser() {
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
//    }
//
//    private fun startImageCropActivity(uri: Uri) {
//        // Implement image cropping activity if needed
//        // Example: CropImage.activity(uri).setAspectRatio(3, 2).start(requireContext(), this)
//        recipeImage.setImageURI(uri)
//        imageUri = uri // Update image URI
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
//            val uri = data.data!!
//            startImageCropActivity(uri)
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadRecipe() {
        progressBar.visibility = View.VISIBLE

        val titleText = title.text.toString().trim()
        val ingredientsText = ingredients.text.toString().trim()
        val stepsText = steps.text.toString().trim()
        val tagsText = tagss.text.toString().trim()

        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val formattedDateStr = currentDate.format(formatter)

        val pref: SharedPreferences = requireActivity().getSharedPreferences("login", MODE_PRIVATE)
        val UserName = pref.getString("username", "NotFound")
        val Email = pref.getString("email", "NotFound")

        if (titleText.isEmpty() || ingredientsText.isEmpty() || stepsText.isEmpty() || imageUri == null || selectedItem == "choose here" || UserName == null || Email == null)  {
            Toast.makeText(
                requireContext(),
                "Please fill in all fields and choose an image",
                Toast.LENGTH_SHORT
            ).show()
            progressBar.visibility = View.GONE
            return
        }

        // Convert image URI to a Bitmap
        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)

        // Compress the Bitmap
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val compressedImage = ByteArrayInputStream(outputStream.toByteArray())

        // Get reference to Firebase Storage
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("recipe_images/${UUID.randomUUID()}")

        // Upload compressed image to Firebase Storage
        val uploadTask: UploadTask = storageReference.putStream(compressedImage)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Image uploaded successfully, get download URL
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                // Now, use Retrofit to send recipe data with image URL to MongoDB
                val baseUrl = "https://recipe-sharing-backend.onrender.com/"
                val recipeRequest = UploadRequest(
                    titleText,
                    selectedItem,
                    ingredientsText,
                    stepsText,
                    tagsText,
                    0,
                    uri.toString(), // Use the download URL from Firebase Storage
                    formattedDateStr,
                    Email,
                    UserName
                )

                Log.d("Login", "Request Body: $recipeRequest")
                val apiService = RetrofitClient.getClient(baseUrl)

                apiService.uploadRecipe(recipeRequest).enqueue(object : Callback<UploadResponse> {
                    override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                        if (response.isSuccessful && response.body() != null) {
                            val uploadResponse = response.body()!!
                            if (uploadResponse.success) {
                                uploadAnimation.playAnimation()
                                progressBar.visibility = View.GONE
                                // Recipe uploaded successfully
                                Toast.makeText(
                                    requireContext(),
                                    "Recipe uploaded successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Clear input fields after successful upload
                                title.setText("")
                                ingredients.setText("")
                                steps.setText("")
                                tagss.setText("")
                                recipeImage.setImageDrawable(null)
                                imageUri = null
                            } else {
                                // Handle unsuccessful upload
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to upload recipe: ${uploadResponse.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            progressBar.visibility = View.GONE
                            // Handle unsuccessful response
                            Toast.makeText(
                                requireContext(),
                                "Failed to upload recipe: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                        // Handle network failure
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Failed to upload recipe: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }.addOnFailureListener { e ->
            // Handle image upload failure
            progressBar.visibility = View.GONE
            Toast.makeText(
                requireContext(),
                "Failed to upload image: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
