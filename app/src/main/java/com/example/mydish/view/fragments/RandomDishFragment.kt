package com.example.mydish.view.fragments

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.mydish.R
import com.example.mydish.application.MyDishApplication
import com.example.mydish.databinding.FragmentRandomDishBinding
import com.example.mydish.model.api.webservice.EndPoint
import com.example.mydish.model.api.webservice.RandomDish
import com.example.mydish.model.entities.MyDishEntity
import com.example.mydish.utils.Constants
import com.example.mydish.utils.Tags.DISH_INFO
import com.example.mydish.utils.extensions.setImageDrawable
import com.example.mydish.utils.extensions.setShimmer
import com.example.mydish.utils.setPicture
import com.example.mydish.utils.toast
import com.example.mydish.viewmodel.MyDishViewModel
import com.example.mydish.viewmodel.MyDishViewModelFactory
import com.example.mydish.viewmodel.RandomDishViewModel

/**
 * This class hold the random dish presentation
 * RandomDishFragment <-> fragment_random_dish is an fragment that bind to the mobile_navigation_xml
 * Use mBinding var to get one instance from fragment_random_dish to get easy accesses
 * Use the RandomDishViewModel for access the service and dara base data for populating the RandomDishFragment ui
 * Use the mProgressDialog to while loading data from the service
 */
class RandomDishFragment : Fragment() {

    /** global variable for RandomDishDetails View **/
    private var mBinding : FragmentRandomDishBinding? = null

    /** global variable for Random dish ViewModel class **/
    private lateinit var mRandomDishViewModel : RandomDishViewModel

    /*** delegate the object MyDishViewModel with repository data base MyDishDao */
    private val myDishViewModel: MyDishViewModel by viewModels {
        MyDishViewModelFactory((requireActivity().application as MyDishApplication).myDishRepository)
    }

    /*** inflate the FragmentRandomDish xml */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentRandomDishBinding.inflate(inflater)
        return mBinding!!.root
    }

    /** init view model with ViewModelProvider and call the getRandomDishFromRecipeAPI from service **/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** Initialize the ViewModel variable. **/
        mRandomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)

        /** Present the recipe on the view with random dish **/
        mRandomDishViewModel.getRandomDishesFromRecipeAPI(EndPoint.DESSERT)

        /** Observe data after the getRandomDishFromRecipeAPI activate **/
        initRandomDishViewModelObserver()

        /** SwipeRefreshLayout.OnRefreshListener that is invoked when the user performs a swipe gesture. */
        mBinding!!.srlRandomDish.let {
            it.setOnRefreshListener {
                /** method performs the actual data-refresh operation ,calls setRefreshing(false) when it's finished.**/
                /** Present the recipe on the view with random dish **/
                mRandomDishViewModel.getRandomDishesFromRecipeAPI(EndPoint.DESSERT)
            }
            if (it.isRefreshing) it.isRefreshing = false
        }
    }

    /*** return the view instance to null */
    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    /**
     * Service call method to dish data then
     * mRandomDishViewModel.randomDishResponse.observe - will take care to set the ui with the new dish
     * mRandomDishViewModel.randomDishLoadingError.observe - take card on the error service response
     * mRandomDishViewModel.loadRandomDish.observe - take care of loading dish only from the service
     */
    private fun initRandomDishViewModelObserver() {
        val observer = mRandomDishViewModel.randomViewModelLiveDataObserver

        /*** Calling the dish data from service */
        observer.recipesData.observe(viewLifecycleOwner, { dishResponse ->
            dishResponse?.let {
                val randomRecipe = dishResponse.recipes.random()
                Log.i(DISH_INFO, "random recipe response: $randomRecipe")
                setRandomResponseInUi(randomRecipe)
                setMinimumUiPresentation(false)
            }
        })

        /*** On error response from services */
        observer.errors.observe(viewLifecycleOwner, { errorResponse ->
            errorResponse?.let {
                Log.i(DISH_INFO,"has random dish response error: $errorResponse")
                mBinding!!.srlRandomDish.isRefreshing.let {
                    mBinding!!.srlRandomDish.isRefreshing = false
                }
            }
        })

        /** This is the custom dialog presentation on load data **/
        observer.loadData.observe(viewLifecycleOwner, { loadRandomDish ->
            loadRandomDish?.let {
                Log.i(DISH_INFO,"has loading random dish response: $loadRandomDish")
                if (loadRandomDish) {
                    setShimmer(listOf(mBinding!!.shimmerImage), listOf(mBinding!!.ivDishImage), 1500)
                }
            }
        })
    }

    /**
     * load dish image and recipe data to ui
     * using the RandomDish.Recipe to have bind with the ui presentation
     * finally set the dish to data base room storage
     */
    private fun setRandomResponseInUi(recipe : RandomDish.Recipe) {
        setPicture(this@RandomDishFragment,recipe.image, mBinding!!.ivDishImage, mBinding!!.srlRandomDish, mBinding!!.tvTitle)

        //Set the dish title
        setRecipeTitle(recipe.title)
        //Default Dish Type
        val dishType = setDishType(recipe)
        //There is not category params present in the response so we will define it as Other.
        setDishCategory(resources.getString(R.string.other_dish))
        //Separate the ingredients preparing to the presenter - mBinding!!.tvIngredients.text
        val ingredients = setDishIngredients(recipe)
        //The Cooking direction text is in the HTML format, use fromHtml to populate it in the TextView.
        setDishCookingDirection(recipe)
        //Set lbl_estimate_cooking_time label with recipe.readyInMinutes text
        setEstimateCookingTime(recipe)
        //Present the drawable image with un_selected as default
        setImageDrawable(mBinding!!.ivFavoriteDish, R.drawable.ic_favorite_unselected)
        //Insert the data to the data base
        setNewDishInsertDataToDataBase(recipe, dishType, ingredients)
    }

    /**
     * Set the recipe title get xml mBinding
     * mBinding!!.tvTitle.text = title
     */
    private fun setRecipeTitle(title : String) {
        mBinding!!.tvTitle.text = title
    }

    /*** Default Dish Type if the dish types are empty */
    private fun setDishType(recipe: RandomDish.Recipe) : String {
        var dishType = "empty"
        if (recipe.dishTypes.isNotEmpty()) {
            dishType = recipe.dishTypes[0]
            mBinding!!.tvType.text = dishType
        }
        return dishType
    }

    /*** There is not category params present in the response so we will define it as Other. */
    private fun setDishCategory(dishCategory: String) {
        mBinding!!.tvCategory.text = dishCategory
    }

    /**
     * Separate the ingredients preparing to the presenter - mBinding!!.tvIngredients.text
     * Check if the ingredients as new line then create \n for each new line of the ingredients text
     */
    private fun setDishIngredients(recipe: RandomDish.Recipe) : String {
        var ingredients = ""
        for (it in recipe.extendedIngredients) {
            ingredients = if (ingredients.isEmpty()) {
                it.original
            } else ingredients + ", \n" + it.original
        }
        mBinding!!.tvIngredients.text = ingredients
        return ingredients
    }

    /**
     * The instruction or you can say the Cooking direction text is
     * in the HTML format so we will you the fromHtml to populate it in the TextView.
     */
    @Suppress("DEPRECATION")
    private fun setDishCookingDirection(recipe: RandomDish.Recipe) {
        val instructions = recipe.instructions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding!!.tvCookingDirection.text = Html
                .fromHtml(instructions, Html.FROM_HTML_MODE_COMPACT)
                .replace("\\.\\s?".toRegex(), "\\.\n")
        } else {
            mBinding!!.tvCookingDirection.text = Html
                .fromHtml(instructions)
                .replace("\\.\\s?".toRegex(), "\\.\n")
        }
    }

    /*** Set lbl_estimate_cooking_time label with recipe.readyInMinutes text */
    private fun setEstimateCookingTime(recipe: RandomDish.Recipe) {
        mBinding!!.tvCookingTime.let {
            val cookTime = R.string.lbl_estimate_cooking_time
            val recipeData = recipe.readyInMinutes.toString()
            it.text = resources.getString(cookTime, recipeData)
            it.setTextColor(Color.BLACK)
        }
    }

    /**
     * present the drawable image with selected after the user click
     * create the view model var with connection to room data base, for insert the MyEntityDish object with dish data
     */
    private fun setNewDishInsertDataToDataBase(recipe: RandomDish.Recipe, dishType: String, ingredients: String) {
        mBinding!!.ivFavoriteDish.setOnClickListener {

            /*** create the new randomDishDetails MyDishEntity */
            val randomDishDetails = MyDishEntity(
                recipe.image,
                Constants.DISH_IMAGE_SOURCE_ONLINE,
                recipe.title,
                dishType,
                "Other",
                ingredients,
                recipe.readyInMinutes.toString(),
                recipe.instructions,
                true
            )

            /*** insert the new randomDishDetails MyDishEntity object to room data base */
            myDishViewModel.insert(randomDishDetails)

            /*** present the drawable image with selected favorite dish */
            setImageDrawable(mBinding!!.ivFavoriteDish, R.drawable.ic_favorite_selected)

            /*** recipe title + the dish_is_selected label will present toast message about new dish added to favorite dishes */
            val titleSelected = recipe.title +" "+ resources.getString(R.string.dish_is_selected)
            toast(requireActivity(), titleSelected).show()
        }
    }

    private fun setMinimumUiPresentation(@Suppress("SameParameterValue") isIgnoreFullUiPresentation: Boolean) {
        if (isIgnoreFullUiPresentation) {
            repeat(listOf(
                mBinding!!.tvIngredientsLabel,
                mBinding!!.tvIngredients,
                mBinding!!.tvCookingDirectionLabel,
                mBinding!!.tvCookingDirection,
                mBinding!!.tvCookingTime).size) { View.GONE }
        }
    }
}