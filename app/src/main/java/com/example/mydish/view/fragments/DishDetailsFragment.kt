package com.example.mydish.view.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.mydish.R
import com.example.mydish.application.MyDishApplication
import com.example.mydish.databinding.FragmentDishDetailsBinding
import com.example.mydish.model.entities.MyDishEntity
import com.example.mydish.utils.Constants
import com.example.mydish.utils.extensions.setImageDrawable
import com.example.mydish.utils.extensions.setPicture
import com.example.mydish.utils.replaceFirstCharToLocalRoot
import com.example.mydish.utils.toast
import com.example.mydish.viewmodel.MyDishViewModel
import com.example.mydish.viewmodel.MyDishViewModelFactory
import java.util.*

class DishDetailsFragment : Fragment() {

    /*** a global variable for the data base Entity */
    private var mMyDishEntity : MyDishEntity? = null

    /*** a global variable for FragmentDishDetails View */
    private var mBinding : FragmentDishDetailsBinding? = null

    /**
     * To create the ViewModel we used the viewModels delegate, passing in an instance
     * of our FavDishViewModelFactory.
     * This is constructed based on the repository retrieved from the FavDishApplication.
     */
    private val mMyDishViewModel : MyDishViewModel by viewModels {
        MyDishViewModelFactory(((requireActivity().application) as MyDishApplication).myDishRepository)
    }

    /**
     * setHasOptionsMenu(boolean hasMenu)
     * Report that this fragment would like to participate in populating the options menu by receiving a call to
     * onCreateOptionsMenu(Menu, MenuInflater) and related methods.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /*** Inflate the layout for this fragment */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    /**
     * on view created create val args : DishDetailsFragmentArgs by navArgs()
     * for get the MyDishDao data pass from the DishDetailsFragment fragment
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**Lazy delegate to access the Fragment's arguments as an [//Args//] instance. */
        val args : DishDetailsFragmentArgs by navArgs()
        /*** init data to the mMyDishEntity with the dish data past from Fragment arguments */
        mMyDishEntity = args.dishDetails

        /*** dish data set inside the text views of the dish detail fragment */
        args.let {
            val data = it.dishDetails
            setPicture(data.image, mBinding!!.ivDishImage, mBinding!!.rlDishDetailMain, mBinding!!.tvCategory)
            setTextWithDishData(data.title, replaceFirstCharToLocalRoot(data.type), data.category, data.ingredients)
            setDishInstruction(data.direction_to_cook)
            setEstimateCookingTime(data.cooking_time)
            setDishStatusOnDishPresentation(data.favoriteDish)
        }

        /*** on click the ivFavoriteDish button */
        mBinding!!.ivFavoriteDish.setOnClickListener {
            args.let {
                val data = it.dishDetails
                /*** update the flag entity, from true to false or the opposite */
                data.favoriteDish = !data.favoriteDish
                /*** update the args with old values from dishDetails screen args with view model */
                mMyDishViewModel.update(it.dishDetails)
                /*** if the dish market as favorite dish */
                setDishStatusAfterUserChose(data.title, data.favoriteDish)
            }
        }
    }

    /**
     * setHasOptionsMenu(boolean hasMenu)
     * Report that this fragment would like to participate in populating the options menu by receiving a call to
     * onCreateOptionsMenu(Menu, MenuInflater) and related methods.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share,menu)
        super.onCreateOptionsMenu(menu,inflater)
    }

    /**
     * this will take care of the share dish option
     * in case the user clicked on share dish button
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_share_dish -> {
                val type = "text/plain"
                val subject = "Check out dish recipe"
                var extraText = ""
                val shareWith = "Share with"

                /** if the image is upload from the service it will assign to MyDishEntity */
                var image = ""

                /** cockingInstruction assign from MyDishEntity and wrapped with Html android class */
                var cockingInstruction: String

                /*** mMyDishEntity = args.dishDetails create on the OnViewCreate , verify not null */
                mMyDishEntity?.let{

                    /**
                     * verify it.imageSource == Constants.DISH_IMAGE_SOURCE_ONLINE
                     * DISH_IMAGE_SOURCE_ONLINE is called from RandomDishFragment
                     * after enter data to room data base from the fragment to MyDishEntity
                     * */
                    if (it.imageSource == Constants.DISH_IMAGE_SOURCE_ONLINE) image = it.image

                    /*** preparing the html format */
                    cockingInstruction = Html.fromHtml(it.direction_to_cook, Html.FROM_HTML_MODE_COMPACT).toString()

                    /*** preparing the message format */
                    extraText = messageFormat(
                        image, it.title, it.type, it.category, it.ingredients, cockingInstruction, it.cooking_time)
                }

                /*** send the share message */
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = type
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(Intent.EXTRA_TEXT, extraText)
                startActivity(Intent.createChooser(intent, shareWith))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /*** return mBinding to null session destroy */
    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    /*** Set texts data on TextViews */
    private fun setTextWithDishData(title: String,type: String,category: String,ingredients: String) {
        mBinding!!.tvTitle.text = title
        mBinding!!.tvType.text = type.replaceFirstChar {
            if (it.isLowerCase()) {
                it.titlecase(Locale.ROOT)
            } else it.toString()
        }
        mBinding!!.tvCategory.text = category
        mBinding!!.tvIngredients.text = ingredients
    }

    /**
     * The instruction or you can say the Cooking direction text is in the HTML
     * format so we will you the fromHtml to populate it in the TextView.
     */
    private fun setDishInstruction(directionToCook: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding!!.tvCookingDirection.text = Html.fromHtml(directionToCook, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            mBinding!!.tvCookingDirection.text = Html.fromHtml(directionToCook)
        }
    }

    /*** set the lbl_estimate_cooking_time text */
    private fun setEstimateCookingTime(cookingTime: String) {
        mBinding!!.tvCookingTime.text = resources.getString(R.string.lbl_estimate_cooking_time, cookingTime)
        mBinding!!.tvCookingTime.setTextColor(Color.BLACK)
    }

    /**
     * If the dish was selected all ready, information came from the
     * MyDishDao data pass from the DishDetailsFragment fragment
     */
    private fun setDishStatusOnDishPresentation(isFavoriteDish: Boolean) {
        val drawable : Int = if (isFavoriteDish)
             R.drawable.ic_favorite_selected
        else R.drawable.ic_favorite_unselected
        setImageDrawable(mBinding!!.ivFavoriteDish, drawable)
    }

    /*** Set favorite dish status from selected to unselected or the opposite */
    private fun setDishStatusAfterUserChose(title: String, isFavoriteDish: Boolean) {
        val setData = if (isFavoriteDish)
            Pair(R.drawable.ic_favorite_selected, resources.getString(R.string.dish_is_selected))
        else Pair(R.drawable.ic_favorite_unselected, resources.getString(R.string.dish_is_un_selected))

        setImageDrawable(mBinding!!.ivFavoriteDish, setData.first)
        toast(requireActivity(),title + " " + setData.second)
    }

    /*** message format for the share dish data with other 3Party application */
    private fun messageFormat(
        image: String,
        title: String,
        type: String,
        category: String,
        ingredients: String,
        cockingInstruction: String,
        cookingTime: String
    ): String {

        return "$image \n" +
                "\n Title: $title " +
                "\n\n Type: $type " +
                "\n\n Category: $category" +
                "\n\n Ingredients: " + "\n $ingredients " +
                "\n\n Instructions To Cook: \n $cockingInstruction" +
                "\n\n Time required to cook the dish approx $cookingTime minutes."
    }
}