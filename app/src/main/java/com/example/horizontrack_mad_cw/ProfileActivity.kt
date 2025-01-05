package com.example.horizontrack_mad_cw

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.activity_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvEmail: TextView = view.findViewById(R.id.tv_email)
        val logoutBtn: Button = view.findViewById(R.id.logout)

        // Populate user details
        val currentUser = mAuth.currentUser

        if(currentUser == null){
            val intent = Intent(requireContext(), LandingPageActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        currentUser?.let {
            tvName.text = it.displayName ?: "No Name"
            tvEmail.text = it.email ?: "No Email"
        }
        logoutBtn.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(requireContext(), LandingPageActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
