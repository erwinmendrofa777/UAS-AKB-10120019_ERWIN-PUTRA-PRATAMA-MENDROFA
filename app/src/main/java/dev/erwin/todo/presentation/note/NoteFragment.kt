package dev.erwin.todo.presentation.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.get
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.erwin.todo.R
import dev.erwin.todo.databinding.FragmentNoteBinding
import dev.erwin.todo.presentation.utils.hideKeyboard
import dev.erwin.todo.presentation.utils.showKeyboard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteFragment : Fragment() {
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = NoteFragmentArgs.fromBundle(arguments as Bundle).id
        noteViewModel.onNoteIdChanged(noteId = id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpNoteDetail()
        btnListener()
    }

    private fun setUpNoteDetail(): Unit = with(binding) {
        noteBody.requestFocus()
        requireActivity().showKeyboard(noteBody)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                noteViewModel.isFavorite
                    .collectLatest { isFavorite ->
                        toolbar.menu[1].icon = AppCompatResources.getDrawable(
                            requireActivity(),
                            if (isFavorite) R.drawable.round_favorite_24dp
                            else R.drawable.ic_round_favorite_border_24dp
                        )
                    }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            noteViewModel.noteDetail
                .drop(1)
                .first()
                ?.let { noteDetail ->
                    noteTitle.setText(noteDetail.title)
                    noteBody.setText(noteDetail.body)
                    noteBody.setSelection(noteDetail.body.length)
                }
        }
        noteTitle.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let(noteViewModel::onTitleChanged)
        }
        noteBody.doOnTextChanged { text, _, _, _ ->
            text?.toString()?.let(noteViewModel::onBodyChanged)
        }
    }

    private fun btnListener(): Unit = with(binding) {
        toolbar.setNavigationOnClickListener {
            noteTitle.clearFocus()
            noteBody.clearFocus()
            hideKeyboard()
            findNavController().navigateUp()
        }
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.readMode -> {
                    noteTitle.clearFocus()
                    noteBody.clearFocus()
                    hideKeyboard()
                    true
                }

                R.id.favoriteNote -> {
                    noteViewModel.onFavoriteChanged(isFavorite = !noteViewModel.isFavorite.value)
                    true
                }

                R.id.removeNote -> {
                    noteTitle.clearFocus()
                    noteBody.clearFocus()
                    hideKeyboard()
                    findNavController().navigateUp()
                    noteViewModel.deleteNote()
                    true
                }

                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!noteViewModel.isRemoveClicked.value) {
            noteViewModel.modifyNote(
                newTitle = noteViewModel.title.value,
                newBody = noteViewModel.body.value
            )
        }
        _binding = null
    }
}