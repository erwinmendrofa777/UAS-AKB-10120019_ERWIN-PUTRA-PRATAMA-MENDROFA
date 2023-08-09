package dev.erwin.todo.presentation.login

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
import dev.erwin.todo.databinding.FragmentLoginBinding
import dev.erwin.todo.presentation.utils.createSpannableText
import dev.erwin.todo.presentation.utils.hideKeyboard
import dev.erwin.todo.presentation.utils.isEmail
import dev.erwin.todo.presentation.utils.isPasswordSecure
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    // Google Sign In was successful, authenticate with firebase
                    val account = task.getResult(ApiException::class.java)!!
                    loginViewModel.doLoginWithGoogle(account.idToken)
                } catch (exception: ApiException) {
                    Log.d(TAG, exception.message.toString())
                }
            }
        }

    @Inject
    lateinit var googleSignIn: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSignInForm()
        val signUpText = getString(R.string.sign_up_text)
        val spannableText = createSpannableText(
            signUpText,
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK,
            if (Locale.getDefault().displayLanguage == "Indonesia") 18 else 23,
            signUpText.length,
        )
        binding.signUp.text = spannableText
        btnListener()
        updateProgressBarState()
        loginStateHandler()
    }

    private fun btnListener() = with(binding) {
        signUp.setOnClickListener {
            val navigateToRegister = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(navigateToRegister)
        }
        emailSignInBtn.setOnClickListener {
            onEmailLogin()
        }
        googleSignInBtn.setOnClickListener {
            val intent = googleSignIn.signInIntent
            resultLauncher.launch(intent)
        }
    }

    private fun updateProgressBarState(): Unit = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.emailLoginState.collectLatest { uiState ->
                    emailProgressBar.isVisible = uiState is UiState.Loading
                    emailSignInBtn.text =
                        if (uiState is UiState.Loading) null else getString(R.string.login)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.googleLoginState.collectLatest { uiState ->
                    googleProgressBar.isVisible = uiState is UiState.Loading
                    googleSignInBtn.text =
                        if (uiState is UiState.Loading) null else getString(R.string.sign_in_with_google)
                }
            }
        }
    }

    private fun loginStateHandler() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.emailLoginState.collectLatest { uiState ->
                    when (uiState) {
                        is UiState.Initialize -> {
                            binding.emailSignInBtn.isClickable = true
                        }

                        is UiState.Loading -> {
                            binding.emailSignInBtn.isClickable = false
                        }

                        is UiState.Success -> {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.login_successfully),
                                Snackbar.LENGTH_SHORT
                            ).show()
                            binding.emailSignInBtn.isClickable = true
                        }

                        is UiState.Error -> {
                            uiState.error?.message?.let {
                                Snackbar.make(
                                    binding.root,
                                    it,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                            binding.emailSignInBtn.isClickable = true
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.googleLoginState.collectLatest { uiState ->
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

    private fun setUpSignInForm(): Unit = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.emailAddress
                    .collectLatest {
                        emailInputLayout.helperText =
                            if (!it.isEmail()) getString(R.string.invalid_email_address)
                            else null
                    }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.password
                    .collectLatest {
                        passwordInputLayout.helperText = if (!it.isPasswordSecure())
                            getString(R.string.invalid_password)
                        else null
                    }
            }
        }
        edEmail.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let(loginViewModel::onEmailAddressChanged)
        }
        edPassword.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let(loginViewModel::onPasswordChanged)
        }
        edPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                onEmailLogin()
                true
            } else false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onEmailLogin(): Unit = with(binding) {
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
        loginViewModel.doLoginWithEmailAndPassword()
        hideKeyboard()
    }

    private companion object {
        val TAG = LoginFragment::class.simpleName
    }
}