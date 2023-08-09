package dev.erwin.todo.presentation.about

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dev.erwin.todo.R
import dev.erwin.todo.databinding.FragmentAboutBinding
import dev.erwin.todo.presentation.MainViewModel
import dev.erwin.todo.presentation.adapters.AboutAdapter

@AndroidEntryPoint
class AboutFragment : Fragment() {
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AboutAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            materialToolbar.setNavigationOnClickListener {
                mainViewModel.updaterDrawerState(isOpen = true)
            }
            adapter = AboutAdapter(requireActivity() as AppCompatActivity)
            viewPager.adapter = adapter
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = getString(TAB_TITLES[position])
                tab.icon = AppCompatResources.getDrawable(requireActivity(), TAB_ICONS[position])
            }.attach()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        @IntegerRes
        val TAB_TITLES = listOf(R.string.profile, R.string.description)

        @DrawableRes
        val TAB_ICONS = arrayOf(R.drawable.ic_person_24dp, R.drawable.ic_about_24dp)
    }
}