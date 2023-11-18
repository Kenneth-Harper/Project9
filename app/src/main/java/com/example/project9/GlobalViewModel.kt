package com.example.project9

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.component1
import com.google.firebase.storage.component2
import kotlinx.coroutines.channels.awaitClose

class GlobalViewModel : ViewModel()
{
    val TAG = "GlobalViewModel"

    val auth = FirebaseAuth.getInstance()

    private var storageInstance = Firebase.storage("gs://project9-selfies.appspot.com")
    private var storageReference = storageInstance.reference
    private val firestore: FirebaseFirestore = Firebase.firestore
    var userId: String? = null

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> get() = _errorMessage

    private val _images = MutableLiveData<MutableList<Uri>>()
    val images : LiveData<List<Uri>> get() = _images as LiveData<List<Uri>>

    var currentImage : Uri? = null

    private val _goToImage = MutableLiveData<Boolean>()
    val goToImage : LiveData<Boolean> get() = _goToImage


    fun saveImage(imageUri: Uri, onCompleteListener: (Uri?) -> Unit)
    {
        val fileReference = storageReference.child("images/${imageUri.lastPathSegment}")
        val saveTask = fileReference.putFile(imageUri)
        saveTask.addOnSuccessListener{
            onCompleteListener(imageUri)
            }
    }

    fun refreshImages()
    {
        val storage = Firebase.storage
        val listRef = storage.reference.child("/images")
        val newImages = mutableListOf<Uri>()
        listRef.listAll()
            .addOnSuccessListener { (prefixes, items) ->
                for (item in items)
                {
                    Log.v(TAG, item.path)
                    newImages.add(item.downloadUrl.result)
                }
            }

        _images.value = newImages
        Log.v(TAG, "_images.size = ${_images.value?.size}")
    }


    fun onImageClicked(image: Uri)
    {
        currentImage = image
        _goToImage.value = true
    }

    fun onGoToImage()
    {
        _goToImage.value = false
    }

    fun signOut()
    {
        auth.signOut()
    }
}
