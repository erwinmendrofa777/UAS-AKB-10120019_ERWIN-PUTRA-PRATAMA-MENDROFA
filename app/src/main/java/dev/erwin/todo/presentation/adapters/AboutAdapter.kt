package dev.erwin.todo.presentation.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.erwin.todo.presentation.description.DescriptionFragment
import dev.erwin.todo.presentation.profile.ProfileFragment

class AboutAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment =
        if (position == 0) ProfileFragment()
        else DescriptionFragment()
}