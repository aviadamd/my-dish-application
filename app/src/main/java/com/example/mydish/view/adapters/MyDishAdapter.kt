package com.example.mydish.view.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.mydish.R
import com.example.mydish.databinding.ItemDishLayoutBinding
import com.example.mydish.model.entities.MyDishEntity
import com.example.mydish.utils.data.Constants
import com.example.mydish.utils.extensions.setPicture
import com.example.mydish.view.activities.AddUpdateDishActivity
import com.example.mydish.view.fragments.AllDishesFragment
import com.example.mydish.view.fragments.FavoriteDishesFragment
import com.facebook.shimmer.ShimmerFrameLayout

/*** will inflate theirs view wit this adapter */
class MyDishAdapter(private val fragment: Fragment) : RecyclerView.Adapter<MyDishAdapter.ViewHolder>() {

    /** create list instance from MyDishEntity **/
    private var dishes: List<MyDishEntity> = listOf()

    /**
     * Inflates the item views which is designed in xml layout file
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDishLayoutBinding.inflate(
            LayoutInflater.from(fragment.context),
            parent,
            false
        ))
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        //Load the dish image in the ImageView with glide

        setShimmer(listOf(holder.shimmerImage, holder.shimmerTitle), listOf(holder.ivDishImage, holder.tvTitle),500)
        setPicture(fragment, dish.image,holder. ivDishImage, false,null)
        holder.tvTitle.text = dish.title

        //Navigation component section
        holder.itemView.setOnClickListener {
            //Navigation to all dishes fragment
            when(fragment) {
                is AllDishesFragment -> fragment.showDishDetails(dish)
                is FavoriteDishesFragment -> fragment.showDishDetails(dish)
            }
        }

        //When need to change dish details or delete
        if (fragment is AllDishesFragment) {
            holder.ibMore.setOnClickListener {
                setPopUpPresentation(holder, dish)
            }
        }

        //Set AllDishesFragment the ibMore - EDIT DISH/DELETE DISH option to visible
        if (fragment is AllDishesFragment) {
            holder.ibMore.visibility = View.VISIBLE
        }

        //Set FavoriteDishesFragment the ibMore - to Invisible
        if (fragment is FavoriteDishesFragment) {
            holder.ibMore.visibility = View.GONE
        }
    }

    /*** Gets the number of items in the list */
    override fun getItemCount(): Int { return dishes.size }

    /**
     * Hold dishes entity data then notify data as changed
     * AllDishesFragment,FavoriteDishesFragment
     **/
    @SuppressLint("NotifyDataSetChanged")
    fun dishesList(list: List<MyDishEntity>) {
        dishes = list
        notifyDataSetChanged()
    }

    private fun setPopUpPresentation(holder: ViewHolder, dish: MyDishEntity) {
        val popup = PopupMenu(fragment.context, holder.ibMore)
        popup.menuInflater.inflate(R.menu.menu_adapter, popup.menu)

        /*** pop up with delete and edit dish options */
        popup.setOnMenuItemClickListener {
            when(it.itemId) {
                /*** start activity inside fragment for edit dish */
                R.id.action_edit_list -> {
                    val intent = Intent(fragment.requireActivity(), AddUpdateDishActivity::class.java)
                    intent.putExtra(Constants.EXTRA_DISH_DETAILS, dish)
                    fragment.requireActivity().startActivity(intent)
                }
                /*** delete dish using AllDishesFragment with mMyDishViewModel */
                R.id.action_delete_list -> (fragment as AllDishesFragment).deleteDish(dish)
            }
            true
        }

        popup.show()
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     * Holds the views that will be add each item to
     */
    class ViewHolder(view : ItemDishLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        val ivDishImage = view.ivDishImage
        val tvTitle = view.tvDishTitle
        val ibMore = view.ibMore
        val shimmerImage = view.shimmerImage
        val shimmerTitle = view.shimmerTitle
    }

    companion object {
        fun setShimmer(shimmer: List<ShimmerFrameLayout>, viewToBeVisible: List<View>, delay : Long) {
            Handler(Looper.getMainLooper()).postDelayed({
                shimmer.let { items ->
                    items.forEach {
                        it.visibility = View.VISIBLE
                        it.startShimmer()
                        it.hideShimmer()
                        it.visibility = View.GONE
                    }
                }
                viewToBeVisible.let { view -> view.forEach { it.visibility = View.VISIBLE } }
            }, delay
            )
        }
    }
}
