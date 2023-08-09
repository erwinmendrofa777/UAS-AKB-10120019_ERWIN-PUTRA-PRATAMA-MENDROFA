package dev.erwin.todo.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.erwin.todo.databinding.FragmentHomeBinding
import dev.erwin.todo.presentation.MainViewModel
import dev.erwin.todo.presentation.adapters.NoteAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnListener()
        setUpNotes()
        setUpSearchBar()
    }

    private fun setUpSearchBar(): Unit = with(binding) {
        searchView.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchBar.text = searchView.text
                searchView.editText.text?.toString()?.let(homeViewModel::onQueryChanged)
                searchView.hide()
                true
            } else false
        }
    }

    private fun setUpNotes(): Unit = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.isLoading.collectLatest { isLoading ->
                    progressBar.isVisible = isLoading
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.notes.collectLatest { notes ->
                    noteAdapter.submitList(notes)
                    homeViewModel.isLoading.collectLatest { isLoading ->
                        emptyNotes.isVisible = notes.isEmpty() and !isLoading
                    }
                }
            }
        }
        val layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        notes.layoutManager = layoutManager
        notes.adapter = noteAdapter
        notes.setHasFixedSize(true)
    }


    private fun btnListener() = with(binding) {
        noteAdapter.onItemClickListener = { noteId ->
            val navigateToNote =
                HomeFragmentDirections.actionHomeFragmentToAddNoteFragment(noteId)
            findNavController().navigate(navigateToNote)
        }
        searchBar.setNavigationOnClickListener {
            mainViewModel.updaterDrawerState(isOpen = true)
        }
        floatingActionButton.setOnClickListener {
            val navigateToNote = HomeFragmentDirections.actionHomeFragmentToAddNoteFragment(null)
            findNavController().navigate(navigateToNote)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}