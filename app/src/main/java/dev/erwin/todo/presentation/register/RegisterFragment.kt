package dev.erwin.todo.presentation.register

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.erwin.todo.R
import dev.erwin.todo.core.utils.UiState
import dev.erwin.todo.databinding.FragmentRegisterBinding
import dev.erwin.todo.presentation.utils.createSpannableText
import dev.erwin.todo.presentation.utils.hideKeyboard
import dev.erwin.todo.presentation.utils.isEmail
import dev.erwin.todo.presentation.utils.isPasswordSecure
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels()
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    // Google Sign In was successful, authenticate with firebase
                    val account = task.getResult(ApiException::class.java)!!
                    registerViewModel.doRegisterWithGoogle(account.idToken)
                } catch (exception: ApiException) {
                    Log.d(TAG, exception.message.toString())
                }
            }
        }

    @Inject
    lateinit var googleSignIn: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSignUpForm()
        val signUpText = resources.getString(R.string.sign_in_text)
        val spannableText = createSpannableText(
            signUpText,
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK,
            if (Locale.getDefault().displayLanguage == "Indonesia") 18 else 17,
            signUpText.length,
        )
        binding.signIn.text = spannableText
        btnListener()
        updateProgressBarState()
        registerStateHandler()
    }

    private fun btnListener() = with(binding) {
        signIn.setOnClickListener {
            findNavController().navigateUp()
        }
        emailSignUpBtn.setOnClickListener {
            onEmailRegister()
        }
        googleSignUpBtn.setOnClickListener {
            val intent = googleSignIn.signInIntent
            resultLauncher.launch(intent)
        }
    }

    private fun updateProgressBarState(): Unit = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.emailRegisterState.collectLatest { uiState ->
                    emailProgressBar.isVisible = uiState is UiState.Loading
                    emailSignUpBtn.text =
                        if (uiState is UiState.Loading) null else resources.getString(R.string.register)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.googleRegisterState.collectLatest { uiState ->
                    googleProgressBar.isVisible = uiState is UiState.Loading
                    googleSignUpBtn.text =
                        if (uiState is UiState.Loading) null else getString(R.string.sign_up_with_google)
                }
            }
        }
    }

    private fun registerStateHandler() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.emailRegisterState.collectLatest { uiState ->
                    when (uiState) {
                        is UiState.Initialize -> {
                            binding.emailSignUpBtn.isClickable = true
                        }

                        is UiState.Loading -> {
                            binding.emailSignUpBtn.isClickable = false
                        }

                        is UiState.Success -> {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.login_successfully),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            binding.emailSignUpBtn.isClickable = true
                        }

                        is UiState.Error -> {
                            uiState.error?.message?.let {
                                Snackbar.make(
                                    binding.root,
                                    it,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                            binding.emailSignUpBtn.isClickable = true
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.googleRegisterState.collectLatest { uiState ->
                    when (uiState) {
                        is UiState.Success -> {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.login_successfully),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        is UiState.Error -> {
                            uiState.error?.message?.let {
                                Snackbar.make(
                                    binding.root,
                                    it,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun setUpSignUpForm(): Unit = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.emailAddress
                    .collectLatest {
                        emailInputLayout.helperText =
                            if (!it.isEmail()) getString(R.string.invalid_email_address)
                            else null
                    }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.password
                    .collectLatest {
                        passwordInputLayout.helperText = if (!it.isPasswordSecure())
                            getString(R.string.invalid_password)
                        else null
                    }
            }
        }
        edEmail.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let(registerViewModel::onEmailAddressChanged)
        }
        edPassword.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let(registerViewModel::onPasswordChanged)
        }
        edPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                onEmailRegister()
                true
            } else false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onEmailRegister(): Unit = with(binding) {
        if (!edEmail.text.toString().isEmail()) {
            Snackbar.make(
                binding.root,
                getString(R.string.invalid_email_address),
                Snackbar.LENGTH_SHORT
            ).show()
            return@with
        }
        if (!edPassword.text.toString().isPasswordSecure()) {
            Snackbar.make(
                binding.root,
                getString(R.string.invalid_password),
                Snackbar.LENGTH_SHORT
            ).show()
            return@with
        }
        registerViewModel.doRegisterWithEmailAndPassword()
        hideKeyboard()
    }

    private companion object {
        val TAG = RegisterFragment::class.simpleName
    }
}