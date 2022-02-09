package com.example.mydish.view.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.mydish.databinding.ItemCategoryBinding
import com.example.mydish.view.fragments.AllDishesFragment

class
CustomHorizontalListItemAdapter(
    private val activity: Activity,
    private val fragment: Fragment?,
    private val listItems: Array<String>)
    : RecyclerView.Adapter<CustomHorizontalListItemAdapter.ViewHolder>() {

    /**
     * Inflates the item views which is designed in xml layout file
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
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
     *
     * Call the AllDishesFragment filterSelectionHorizontal to apply filter selection presentation
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvText.text = item

        holder.tvText.setOnClickListener {
            (fragment as AllDishesFragment).filterSelectionHorizontal(item)
        }
    }

    /*** Gets the number of items in the list */
    override fun getItemCount(): Int { return listItems.size }

    /*** A ViewHolder describes an item view and metadata about its place within the RecyclerView. */
    inner class ViewHolder(view : ItemCategoryBinding) : RecyclerView.ViewHolder(view.root) {
        val tvText = view.categoryText
    }

}