package com.example.mydish.view.fragments

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.example.mydish.R
import com.example.mydish.model.application.MyDishApplication
import com.example.mydish.databinding.FragmentRandomDishBinding
import com.example.mydish.model.service.webservice.EndPoint
import com.example.mydish.model.entities.MyDishEntity
import com.example.mydish.model.service.webservice.Recipe
import com.example.mydish.utils.data.Constants
import com.example.mydish.utils.data.Tags.DISH_INFO
import com.example.mydish.utils.extensions.*
import com.example.mydish.viewmodel.MyDishViewModel
import com.example.mydish.viewmodel.MyDishViewModelFactory
import com.example.mydish.viewmodel.RandomDishViewModel
import com.example.mydish.viewmodel.RandomDishViewModel.RandomDishState
import com.example.mydish.viewmodel.RandomDishViewModel.With
import kotlinx.coroutines.flow.collect

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
    //private lateinit var mRandomDishViewModel : RandomDishViewModel

    /** global variable for Random dish ViewModel class **/
    private val mRandomDishViewModel : RandomDishViewModel by viewModels()

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

        /** Initialize the mRandomDishViewModel variable to fragment life cycle. **/
        //mRandomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)
        /** Present the recipe on the view with random dish **/
        mRandomDishViewModel.getRandomRecipeApiCall(With.COROUTINE, EndPoint.DESSERT)
        /** Observe data after the getRandomDishFromRecipeAPI activate **/
        getRandomDishObserver(With.COROUTINE)
        /** SwipeRefreshLayout.OnRefreshListener that is invoked when the user performs a swipe gesture. */
        mBinding!!.srlRandomDish.setOnRefreshListener {
            /** method performs the actual data-refresh operation ,calls setRefreshing(false) when it's finished.**/
            /** Present the recipe on the view with random dish **/
            mRandomDishViewModel.getRandomRecipeApiCall(With.COROUTINE, EndPoint.DESSERT)
        }
    }

    /*** return the view instance to null and refresh the view model */
    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    private fun getRandomDishObserver(with: With) {
        when(with) {
            With.COROUTINE -> initRandomDishesViewModelObserverOne()
            With.RX -> { initRandomDishViewModelObserverThree() }
        }
    }

    /**
     * Service call method to dish data then
     * mRandomDishViewModel.randomDishResponse.observe - will take care to set the ui with the new dish
     * mRandomDishViewModel.randomDishLoadingError.observe - take card on the error service response
     * mRandomDishViewModel.loadRandomDish.observe - take care of loading dish only from the service
     */
    private fun initRandomDishesViewModelObserverOne() : Boolean {
        /*** Calling the dish data from service */
        var successes = false
        lifecycleScope.launchWhenStarted {
            mRandomDishViewModel.getRandomDishState().collect {
                when (it) {
                    is RandomDishState.Load -> {
                        Log.i(DISH_INFO, "dish loading state: ${it.load}")
                        refreshingHandler(500)
                        setShimmer(listOf(mBinding!!.shimmerImage), listOf(mBinding!!.ivDishImage), if (it.load) 1000 else 1150)
                    }
                    is RandomDishState.Service -> {
                        it.randomDishApi?.let { response ->
                            response.recipes[0].apply {
                                Log.i(DISH_INFO, "dish response: $this")
                                setRandomResponseInUi(this)
                                setMinimumUiPresentation(false)
                                successes = true
                            }
                        }
                    }
                    is RandomDishState.Errors -> {
                        Log.i(DISH_INFO, "dish error state: ${it.error}")
                        toast(requireActivity(), "Unable to load dish, try later").show()
                    }
                    else -> Unit
                }
            }
        }
        return successes
    }

    private fun initRandomDishesViewModelObserverTwo(): Boolean {
        var successes = false
        /*** Calling the dish data from service */
        mRandomDishViewModel.getRandomDishState().asLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is RandomDishState.Load -> {
                    Log.i(DISH_INFO, "dish loading state: ${it.load}")
                    refreshingHandler(500)
                    setShimmer(listOf(mBinding!!.shimmerImage), listOf(mBinding!!.ivDishImage), if (it.load) 1000 else 1150)
                }
                is RandomDishState.Service -> {
                    it.randomDishApi?.let { response ->
                        response.recipes[0].apply {
                            Log.i(DISH_INFO, "dish response: $this")
                            setRandomResponseInUi(this)
                            setMinimumUiPresentation(false)
                            successes = true
                        }
                    }
                }
                is RandomDishState.Errors -> {
                    Log.i(DISH_INFO, "dish error state: ${it.error}")
                    toast(requireActivity(), "Unable to load dish, try later").show()
                    this.requireActivity().finish()
                }
                else -> Unit
            }
        }
        return successes
    }

    /**
     * Service call method to dish data then
     * mRandomDishViewModel.randomDishResponse.observe - will take care to set the ui with the new dish
     * mRandomDishViewModel.randomDishLoadingError.observe - take card on the error service response
     * mRandomDishViewModel.loadRandomDish.observe - take care of loading dish only from the service
     */
    private fun initRandomDishViewModelObserverThree(): Boolean {
        var successes = false
        /*** Calling the dish data from service */
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner, { dishResponse ->
            dishResponse?.let {
                val response = it.recipes.random()
                Log.i(DISH_INFO,"dish response: $response")
                setRandomResponseInUi(response)
                setMinimumUiPresentation(false)
                successes = true
            }
        })

        /*** On error response from services */
        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner, { error ->
            error.let {
                Log.i(DISH_INFO,"Dish response error: $it")
                toast(requireActivity(),"Unable to load dish, try later").show()
                this.requireActivity().finish()
            }
        })

        /** This is the loading process on load data **/
        mRandomDishViewModel.randomDishLoading.observe(viewLifecycleOwner, { loadDish ->
            loadDish.let {
                Log.i(DISH_INFO, "Dish loading random dish response: $it")
                refreshingHandler(500)
                val timeOut : Long = if (loadDish) 1000 else 1500
                setShimmer(listOf(mBinding!!.shimmerImage), listOf(mBinding!!.ivDishImage), timeOut)
            }
        })
        return successes
    }

    /**
     * load dish image and recipe data to ui
     * using the RandomDish.Recipe to have bind with the ui presentation
     * finally set the dish to data base room storage
     */
    private fun setRandomResponseInUi(recipe : Recipe) {
        setShimmer(listOf(mBinding!!.shimmerImage), listOf(mBinding!!.ivDishImage),1000)
        setPicture(this@RandomDishFragment, recipe.image, mBinding!!.ivDishImage, true, mBinding!!.tvTitle)
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
    private fun setDishType(recipe: Recipe) : String {
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
        mBinding!!.tvCategory.setTextColor(Color.BLACK)
    }

    /**
     * Separate the ingredients preparing to the presenter - mBinding!!.tvIngredients.text
     * Check if the ingredients as new line then create \n for each new line of the ingredients text
     */
    private fun setDishIngredients(recipe: Recipe) : String {
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
    private fun setDishCookingDirection(recipe: Recipe) {
        val downLine = "\\.\n"
        val dot = "\\.\\s?".toRegex()
        val instructions = recipe.instructions
        val setCookDirection = mBinding!!.tvCookingDirection

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setCookDirection.text = Html.fromHtml(instructions, Html.FROM_HTML_MODE_COMPACT).replace(dot, downLine)
        } else {
            setCookDirection.text = Html.fromHtml(instructions).replace(dot, downLine)
        }
    }

    /*** Set lbl_estimate_cooking_time label with recipe.readyInMinutes text */
    private fun setEstimateCookingTime(recipe: Recipe) {
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
    private fun setNewDishInsertDataToDataBase(recipe: Recipe, dishType: String, ingredients: String) {
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

            var isNewDish = true
            myDishViewModel.allDishesList.observe(viewLifecycleOwner) {
                it.forEach { item ->
                    if (item.title == recipe.title) {
                        isNewDish = false
                    }
                }
            }

            if (isNewDish) {
                /*** insert the new randomDishDetails MyDishEntity object to room data base */
                myDishViewModel.insert(randomDishDetails)
                /*** present the drawable image with selected favorite dish */
                setImageDrawable(mBinding!!.ivFavoriteDish, R.drawable.ic_favorite_selected)
                /*** recipe title + the dish_is_selected label will present toast message about new dish added to favorite dishes */
                toast(requireActivity(),recipe.title+" "+resources.getString(R.string.dish_is_selected)).show()
            } else {
                /*** recipe title + the dish all ready label exists in the favorite dishes */
                toast(requireActivity(),"${recipe.title} dish is all ready in your favorite dishes").show()
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun setMinimumUiPresentation(isIgnoreFullUiPresentation: Boolean) {
        if (isIgnoreFullUiPresentation) {
            repeat(listOf(
                mBinding!!.tvIngredientsLabel,
                mBinding!!.tvIngredients,
                mBinding!!.tvCookingDirectionLabel,
                mBinding!!.tvCookingDirection,
                mBinding!!.tvCookingTime).size) {
                View.GONE
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun refreshingHandler(timeOut: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (mBinding!!.srlRandomDish.isRefreshing) {
                mBinding!!.srlRandomDish.isRefreshing = false
            }
        }, timeOut)
    }
}