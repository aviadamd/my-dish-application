package com.example.mydish.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mydish.application.MyDishApplication
import com.example.mydish.databinding.FragmentFavoriteDishesBinding
import com.example.mydish.model.entities.MyDishEntity
import com.example.mydish.view.adapters.MyDishAdapter
import com.example.mydish.utils.extensions.onNavigateBackToFragment
import com.example.mydish.utils.extensions.onResumeToFragment
import com.example.mydish.viewmodel.MyDishViewModel
import com.example.mydish.viewmodel.MyDishViewModelFactory

class FavoriteDishesFragment : Fragment() {

    /*** A global variable for FragmentFavoriteDishes View */
    private var mBinding : FragmentFavoriteDishesBinding? = null

    /*** View model delegate */
    private val mMyDishViewModel : MyDishViewModel by viewModels {
        MyDishViewModelFactory((requireActivity().application as MyDishApplication).myDishRepository)
    }

    /*** init mBinding fot the FavoriteDishes fragment */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    /**
     * Set up the favoriteDishes list from the
     * mMyDishViewModel.favoriteDishes live data list of MyDishEntity to the ui
     * call mMyDishViewModel to get the fa vorite dishes data
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding!!.rvFavoriteDishesLists.layoutManager = LinearLayoutManager(requireActivity())
        val myDishAdapter = MyDishAdapter(this)
        mBinding!!.rvFavoriteDishesLists.adapter = myDishAdapter

        mMyDishViewModel.favoriteDishes.observe(viewLifecycleOwner) {
            it.let {
                val views = setDishViews()
                /*** if the the view model favorite dishes is not empty */
                if (it.isNotEmpty()) {
                    views.first.visibility = View.VISIBLE
                    views.second.visibility = View.GONE
                    myDishAdapter.dishesList(it)
                } else {
                    views.first.visibility = View.GONE
                    views.second.visibility = View.VISIBLE
                }
            }
        }
    }

    /*** set mBinding to null on destroy */
    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    /**
     * When back to Main Activity
     * Show the bottom navigation again
     */
    override fun onResume() {
        super.onResume()
        onResumeToFragment()
    }

    /**
     * A function to navigate to the Dish Details Fragment.
     * Hide the bottom lower navigation when have an dish presentation to the screen
     * @param myDishEntity this will be populate from the MyDishAdapter
     * Navigation component section
     * onBindViewHolder -> holder.itemView.setOnClickListener {}
     * Navigate via the given {@link NavDirections} from the xml
     * directions that describe this navigation operation
     * Present to ui the favorite dishes selection
     * Hide the bottom nav and open the dish details
     */
    fun showDishDetails(myDishEntity: MyDishEntity) {
        onNavigateBackToFragment(
            navDirections = FavoriteDishesFragmentDirections.actionFavoriteDishesToDishDetails(myDishEntity)
        )
    }

    private fun setDishViews(): Pair<RecyclerView,TextView> {
        return Pair(mBinding!!.rvFavoriteDishesLists, mBinding!!.tvNoFavoriteDishesAvailable)
    }
}