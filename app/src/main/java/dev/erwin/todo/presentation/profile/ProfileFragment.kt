package dev.erwin.todo.presentation.profile

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.erwin.todo.databinding.FragmentProfileBinding
import dev.erwin.todo.presentation.MainViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpProfile()
        btnListener()
    }

    private fun btnListener(): Unit = with(binding) {
        icEmail.setOnClickListener {
            shareEmailAddress(email = mainViewModel.studentProfile.emailAddress)
        }
        email.setOnClickListener {
            shareEmailAddress(email = mainViewModel.studentProfile.emailAddress)
        }
    }

    private fun setUpProfile(): Unit = with(binding) {
        profilePicture.setImageDrawable(mainViewModel.studentProfile.profilePicture)
        fullName.text = mainViewModel.studentProfile.fullName
        studentId.text = mainViewModel.studentProfile.studentId
        email.text = mainViewModel.studentProfile.emailAddress
        studentClass.text = mainViewModel.studentProfile.`class`
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun shareEmailAddress(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        }
        startActivity(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Intent.createChooser(intent, "Send Email")
            } else {
                intent
                    .also {
                        requireActivity().packageManager?.resolveActivity(
                            intent,
                            PackageManager.MATCH_DEFAULT_ONLY
                        )
                    }
            })

    }

}