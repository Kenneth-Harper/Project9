package com.example.project9

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.project9.databinding.FragmentImageBinding

/**
 *
 */
class ImageFragment : Fragment() {
    private val TAG = "GalleryFragment"
    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GlobalViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel.onGoToImage()

        Glide.with(requireContext()).load(viewModel.currentImage).into(binding.imageviewSelfie)


        val callback = object : OnBackPressedCallback(true)
        {
            override fun handleOnBackPressed()
            {
                viewModel.currentImage = null
                view.findNavController().navigate(R.id.action_imageFragment_to_galleryFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return view
    }

}