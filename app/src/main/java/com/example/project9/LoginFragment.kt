package com.example.project9

import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.project9.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

/**
 *
 */
class LoginFragment : Fragment() {
    private val TAG = "LoginFragment"

    private var _binding :  FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GlobalViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        val auth = viewModel.auth

        binding.viewLogin.visibility = View.VISIBLE
        binding.viewSignUp.visibility = View.GONE

        if (auth.currentUser != null)
        {
            toImagesScreen()
        }

        binding.textviewNewUser.setOnClickListener {
            binding.viewLogin.visibility = View.GONE
            binding.viewSignUp.visibility = View.VISIBLE
        }

        binding.textviewBackToLogin.setOnClickListener {
            binding.viewLogin.visibility = View.VISIBLE
            binding.viewSignUp.visibility = View.GONE
        }

        val buttonSignIn = binding.buttonSignIn
        buttonSignIn.setOnClickListener {
            buttonSignIn.isEnabled = false
            val email = binding.editTextLoginEmail.text.toString().trim()
            val password = binding.editTextLoginPassword.text.toString().trim()
            Log.v(TAG, "Email: $email \nPassword: $password")
            if (email.isBlank() || password.isBlank())
            {
                Toast.makeText(this.context, "Email/password cannot be empty", Toast.LENGTH_SHORT).show()
                buttonSignIn.isEnabled = true
                return@setOnClickListener
            }
            // Firebase authentication check
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                buttonSignIn.isEnabled = true
                if (task.isSuccessful)
                {
                    Toast.makeText(this.context, "Success!", Toast.LENGTH_SHORT).show()
                    toImagesScreen()
                }
                else
                {
                    Log.e(TAG, "signInWithEmail failed", task.exception)
                    Toast.makeText(this.context, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val buttonSignUp = binding.buttonSignUp
        buttonSignUp.setOnClickListener {
            buttonSignUp.isEnabled = false
            val email = binding.editTextSignUpEmail.text.toString().trim()
            val password = binding.editTextSignUpPassword.text.toString().trim()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this.context, "Email/password cannot be empty", Toast.LENGTH_SHORT).show()
                buttonSignUp.isEnabled = true
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                buttonSignUp.isEnabled
                if (it.isSuccessful)
                {
                    Toast.makeText(this.context, "User Created Successfully", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this.context, "Unable to create user", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val callback = object : OnBackPressedCallback(true)
        {
            override fun handleOnBackPressed() { viewModel.signOut()}
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return view
    }

    private fun toImagesScreen()
    {
        view?.findNavController()?.navigate(R.id.action_loginFragment_to_galleryFragment)
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }
}