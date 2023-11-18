package com.example.project9

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project9.databinding.FragmentGalleryBinding

import com.example.project9.model.Image
import kotlin.math.sqrt

/**
 *
 */
class GalleryFragment : Fragment(), SensorEventListener
{
    private val TAG = "GalleryFragment"
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GlobalViewModel by activityViewModels()
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    private var accelerometerData = floatArrayOf(SensorManager.GRAVITY_EARTH, SensorManager.GRAVITY_EARTH, 0f)

    private val accelThreshold = 8

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?)
    {
        val x: Float = event?.values?.get(0) ?: 0f
        val y: Float = event?.values?.get(1) ?: 0f
        val z: Float = event?.values?.get(2) ?: 0f
        accelerometerData[1] = accelerometerData[0]
        accelerometerData[0] = sqrt((x * x).toDouble() + y * y + z * z).toFloat()
        val delta: Float = accelerometerData[0] - accelerometerData[1]
        accelerometerData[2] = accelerometerData[2] * 0.9f + delta
        if (accelerometerData[2] > accelThreshold)
        {
            goToCamera()
        }
    }

    private fun goToCamera()
    {
        view?.findNavController()?.navigate(R.id.action_galleryFragment_to_cameraFragment)
    }

    override fun onResume()
    {
        super.onResume()
        accelerometer?.also { accel -> sensorManager?.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL)}
        viewModel.refreshImages()
    }

    override fun onPause()
    {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel.refreshImages()

        val adapter = ImageItemAdapter(requireContext(),{image : Uri -> viewModel.onImageClicked(image)})
        binding.recyclerviewGallery.adapter = adapter
        binding.recyclerviewGallery.layoutManager = GridLayoutManager(context, 2)
        viewModel.images.observe(viewLifecycleOwner, Observer
        {
            it?.let{
                adapter.submitList(it)
            }
        })



        viewModel.goToImage.observe(viewLifecycleOwner, Observer {
            if (it == true)
            {
                view.findNavController().navigate(R.id.action_galleryFragment_to_imageFragment)
            }
        })


        val callback = object : OnBackPressedCallback(true)
        {
            override fun handleOnBackPressed()
            {
                viewModel.signOut()
                view.findNavController().navigate(R.id.action_galleryFragment_to_loginFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return view
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }
}