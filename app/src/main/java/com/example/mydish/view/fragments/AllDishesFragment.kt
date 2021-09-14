package com.example.mydish.view.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mydish.R
import com.example.mydish.model.application.MyDishApplication
import com.example.mydish.databinding.FragmentAllDishesBinding
import com.example.mydish.model.entities.MyDishEntity
import com.example.mydish.utils.data.Constants
import com.example.mydish.utils.extensions.onNavigateBackToFragment
import com.example.mydish.utils.extensions.onResumeToFragment
import com.example.mydish.view.activities.AddUpdateDishActivity
import com.example.mydish.view.activities.MainActivity
import com.example.mydish.view.adapters.CustomHorizontalListItemAdapter
import com.example.mydish.view.adapters.MyDishAdapter
import com.example.mydish.viewmodel.MyDishViewModel
import com.example.mydish.viewmodel.MyDishViewModelFactory
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * This class shows all dishes list from dish that the user chose from RandomDishFragment
 * And show the option for action_add_dish
 * And show the option for action_filter_dish
 */
class AllDishesFragment : Fragment() {

    /** global variable for FragmentAllDishes View **/
    private lateinit var mBinding : FragmentAllDishesBinding

    /** global variable for MyDishAdapter Class **/
    private lateinit var mMyDishAdapter : MyDishAdapter

    /**
     * create the ViewModel used the viewModels delegate,
     * passing in an instance of our MyDishViewModelFactory.
     * This is constructed based on the repository retrieved from the MyDishApplication.
     *
     *  using in this class
     *  mMyDishViewModel.allDishesList
     *  mMyDishViewModel.delete(myDishEntity)
     *  mMyDishViewModel.filteredListDishes(filterItem)
     */
    private val mMyDishViewModel : MyDishViewModel by viewModels {
        MyDishViewModelFactory((requireActivity().application as MyDishApplication).myDishRepository)
    }

    /**
     * Report that this fragment would like to participate in populating the options menu
     * by receiving a call to onCreateOptionsMenu and related methods.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /*** Bind the mBinding with the FragmentAllDishesBinding for the class using for mBinding instance */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentAllDishesBinding.inflate(inflater, container,false)
        return mBinding.root
    }

    /**
     * Populating the rvDishesList layoutManager with the GridLayoutManager [0,1]
     * for present the card data as grid layout
     * if allDishesList get the empty content from MyDishEntity then show tvNoDishesAddedYet text
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*** set the LayoutManager that this RecyclerView will use. */
        mBinding.rvDishesList.layoutManager = LinearLayoutManager(requireActivity())
        /*** adapter class is initialized and list is passed in the param. */
        mMyDishAdapter = MyDishAdapter(this@AllDishesFragment)
        /*** adapter instance is set to the recyclerview to inflate the items. */
        mBinding.rvDishesList.adapter = mMyDishAdapter

        /*** set the category text list before add to the ui */
        /*** set/init the rvDishesCategory layoutManager to Linear view as horizontal view */
        mBinding.rvDishesCategory.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        /*** set the rvDishesCategory to visible */
        mBinding.rvDishesCategory.visibility = View.VISIBLE
        /*** set/init the adapter to mBinding.rvDishesCategory.adapter */
        val adapter = CustomHorizontalListItemAdapter(requireActivity(), this@AllDishesFragment, resources.getStringArray(R.array.dishTypes))
        mBinding.rvDishesCategory.adapter = adapter

        /*** observer on the LiveData returned by getAllDishesList.
         * method fires when the observed data changes and the activity is in the foreground. */
        mMyDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishesEntity ->
           dishPresentation(dishesEntity,"")
        }
    }

    /*** Will call from override fun onCreate */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Handle the upper section of the App on select item from add and filter dish
     * Handle the add dish with start Activity to AddUpdateDishActivity
     * Handle the filter dish by dish type presentation with the filterDishesListDialog
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_dish -> {
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Handle the navigation inside the Main activity
     * if the is that MainActivity then show the bottom lower bar navigation
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
     */
    fun showDishDetails(myDishEntity: MyDishEntity) {
        onNavigateBackToFragment(
            navDirections = AllDishesFragmentDirections.actionAllDishesToDishDetails(myDishEntity)
        )
    }

    /**
     * Method is used to show the Alert Dialog while deleting the dish details.
     * This method get dish entity as param, and call from MyDishAdapter
     * @param myDishEntity - Dish details that we want to delete.
     */
    fun deleteDish(myDishEntity: MyDishEntity) {
        deleteDishFromRepoPopUp(myDishEntity = myDishEntity, mMyDishViewModel)
    }

    /**
     * function to get the filter item selection and get the list from database accordingly.
     * if (filterItem == Constants.ALL_ITEMS) Filter the data by all dishes
     * else Filter list dishes by type
     */
    fun filterSelectionHorizontal(filterItem : String) {
        if (filterItem == Constants.ALL_ITEMS) {
            mMyDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
                dishPresentation(dishes,"")
            }
        } else {
            mMyDishViewModel.filteredListDishes(filterItem).observe(viewLifecycleOwner) { dishes ->
                dishPresentation(dishes, filterItem)
            }
        }
    }

    /**
     * dishes : List<MyDishEntity> from the data base entity
     * present the ui with dishes if the data base is have dishes
     * else -> set the no dish label
     */
    private fun dishPresentation(dishes: List<MyDishEntity>, notifyLabel: String) {
        dishes.let {
            if (it.isNotEmpty()) {
                mBinding.rvDishesList.visibility = View.VISIBLE
                mBinding.tvNoDishesAddedYet.visibility = View.GONE
                /*** present the dishes from MyDishEntity */
                mMyDishAdapter.dishesList(it)
            } else {
                mBinding.rvDishesList.visibility = View.GONE
                mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                val setMessage = if(notifyLabel.isNotEmpty()) {
                    resources.getString(R.string.dish_not_exists_label, notifyLabel)
                } else resources.getString(R.string.label_no_dishes_added_yet)
                mBinding.tvNoDishesAddedYet.text = setMessage
            }
        }
    }

    companion object {
        /**
         * Method is used to show the Alert Dialog while deleting the dish details.
         * This method get dish entity as param, and call from MyDishAdapter
         * @param myDishEntity - Dish details that we want to delete.
         */
        fun Fragment.deleteDishFromRepoPopUp(myDishEntity: MyDishEntity, model: MyDishViewModel) {
            val builder = AlertDialog.Builder(this.requireActivity())
                .setTitle(this.resources.getString(R.string.title_delete_dish))
                .setMessage(this.resources.getString(R.string.msg_delete_dish_dialog, myDishEntity.title))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(this.resources.getString(R.string.delete_dish_label_yes)) { dialog, _ ->
                    model.delete(myDishEntity)
                    dialog.dismiss()
                }.setNegativeButton(this.resources.getString(R.string.delete_dish_label_no)) { dialog, _ ->
                    dialog.dismiss()
                }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }
}