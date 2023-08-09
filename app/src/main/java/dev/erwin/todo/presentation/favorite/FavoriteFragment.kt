package dev.erwin.todo.presentation.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.erwin.todo.databinding.FragmentFavoriteBinding
import dev.erwin.todo.presentation.MainViewModel
import dev.erwin.todo.presentation.adapters.NoteAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
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
                searchView.editText.text?.toString()?.let(favoriteViewModel::onQueryChanged)
                searchView.hide()
                true
            } else false
        }
    }

    private fun btnListener() = with(binding) {
        noteAdapter.onItemClickListener = { noteId ->
            val navigateToNote =
                FavoriteFragmentDirections.actionFavoriteFragmentToNoteFragment(noteId)
            findNavController().navigate(navigateToNote)
        }
        searchBar.setNavigationOnClickListener {
            mainViewModel.updaterDrawerState(isOpen = true)
        }
    }

    private fun setUpNotes(): Unit = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                favoriteViewModel.isLoading.collectLatest { isLoading ->
                    progressBar.isVisible = isLoading
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                favoriteViewModel.notes.collectLatest { notes ->
                    noteAdapter.submitList(notes)
                    favoriteViewModel.isLoading.collectLatest { isLoading ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}