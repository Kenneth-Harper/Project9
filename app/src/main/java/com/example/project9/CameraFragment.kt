package com.example.project9

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.project9.databinding.FragmentCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 *
 */
class CameraFragment : Fragment() {

    private val TAG = "CameraFragment"

    var _binding: FragmentCameraBinding? = null
    val binding get() = _binding!!
    private val viewModel: GlobalViewModel by activityViewModels()
    private var imageCapture: ImageCapture? = null
    private var cameraLoaded = false

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value) {permissionGranted = false}
            }
            if (!permissionGranted) {
                Toast.makeText(this.requireContext(), "Permission request denied", Toast.LENGTH_SHORT).show()
            }
            else
            {
                startCamera()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val view = binding.root

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }


        startCamera()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view.findNavController().navigate(R.id.action_cameraFragment_to_galleryFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return view
    }

    private fun requestPermissions()
    {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() : Boolean
    {
        return REQUIRED_PERMISSIONS.all { ContextCompat.checkSelfPermission(this.requireContext(), it) == PackageManager.PERMISSION_GRANTED }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this.requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewview.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .build()
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                cameraLoaded = true
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

            binding.buttonTakeSelfie.setOnClickListener {
                if (!cameraLoaded) {
                    Toast.makeText(context, "Camera still loading. Try again in a few moments", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val imageFile = File.createTempFile(
                    "selfie_${SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault()).format(Date())}",
                    ".jpg",
                    context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                )
                val outputOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()
                imageCapture!!.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
                    object : ImageCapture.OnImageSavedCallback
                    {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults)
                        {
                            val savedUri = Uri.fromFile(imageFile)
                            viewModel.saveImage(savedUri)
                            { uploadUri ->
                                if (uploadUri != null)
                                {
                                    Toast.makeText(context, "Selfie saved successfully", Toast.LENGTH_SHORT).show()
                                    viewModel.refreshImages()
                                    view?.findNavController()?.navigate(R.id.action_cameraFragment_to_galleryFragment)
                                } else {
                                    Toast.makeText(context, "Selfie save failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        override fun onError(exception: ImageCaptureException)
                        {
                            Toast.makeText(context, "Selfie capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

        }, ContextCompat.getMainExecutor(this.requireContext()))
    }


    companion object
    {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.CAMERA).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}