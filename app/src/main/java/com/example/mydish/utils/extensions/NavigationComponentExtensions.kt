package com.example.mydish.utils.extensions

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.mydish.view.activities.MainActivity

/**
 * When back to Main Activity
 * Show the bottom navigation again
 */
fun Fragment.onResumeToFragment() {
    (requireActivity() as? MainActivity)?.showBottomNavigationView()
}

/**
 * myDishEntity this will be populate from the MyDishAdapter
 * Navigation component section
 * onBindViewHolder -> holder.itemView.setOnClickListener {}
 * Navigate via the given {@link NavDirections} from the xml
 * directions that describe this navigation operation
 * Present to ui the favorite dishes selection
 * Hide the bottom nav and open the dish details
 */
fun Fragment.onNavigateBackToFragment(navDirections: NavDirections) {
    (requireActivity() as? MainActivity)?.hideBottomNavigationView().also {
        findNavController().navigate(navDirections)
    }
}