package com.example.mydish.view.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.mydish.R
import com.example.mydish.model.application.MyDishApplication
import com.example.mydish.databinding.FragmentRandomDishBinding
import com.example.mydish.model.service.webservice.EndPoint
import com.example.mydish.model.entities.MyDishEntity
import com.example.mydish.model.service.webservice.Recipe
import com.example.mydish.utils.data.Constants
import com.example.mydish.utils.extensions.*
import com.example.mydish.viewmodel.MyDishViewModel
import com.example.mydish.viewmodel.MyDishViewModelFactory
import com.example.mydish.viewmodel.RandomDishViewModel
import com.example.mydish.viewmodel.ResourceState
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.io.IOException

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
        //mRandomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)
        /** call random dish api **/
        mRandomDishViewModel.getRandomDishesRecipeAPINew(EndPoint.DESSERT)
        /** Observe data after the getRandomDishFromRecipeAPI activate and ui presentation **/
        initRandomDishesViewModelObserver()
        /*** on refresh listener create another api call */
        callRandomDishOnRefreshListener()
    }

    /*** return the view instance to null and refresh the view model */
    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
        mRandomDishViewModel.refresh()
    }

    /** SwipeRefreshLayout.OnRefreshListener that is invoked when the user performs a swipe gesture. */
    private fun callRandomDishOnRefreshListener() {
        mBinding!!.srlRandomDish.setOnRefreshListener {
            /** method performs the actual data-refresh operation ,calls setRefreshing(false) when it's finished.**/
            /** Present the recipe on the view with random dish **/
            val status = mRandomDishViewModel.getRandomDishesRecipeAPINew(EndPoint.DESSERT)
            if (status == 0) {
                Timber.i("call dish service again to back up")
                mRandomDishViewModel.getRandomDishesRecipeAPINew(EndPoint.DESSERT)
            }
        }
    }

    /**
     * Service call method with observer
     * ResourceState.Service - will take care to set the ui with the new dish
     * ResourceState.Error - take card on the error service response
     * ResourceState.Load - take care of loading dish status
     */
    private fun initRandomDishesViewModelObserver() {
        /*** Calling the dish data from service */
        lifecycleScope.launchWhenStarted {
            mRandomDishViewModel.getRandomDishState().collect {
                when (it) {
                    is ResourceState.Load -> {
                        Timber.d("dish loading state: ${it.load}")
                        refreshingHandler(1000)
                        setShimmer(
                            listOf(mBinding!!.shimmerImage, mBinding!!.tvTitleShimmer),
                            listOf(mBinding!!.ivDishImage, mBinding!!.tvTitle),
                            if(it.load) 1000 else 1500
                        )
                    }
                    is ResourceState.Service -> {
                        Timber.d("dish service call")
                        it.randomDishApi?.let { response ->
                            response.recipes[0].apply {
                                Timber.i("dish response: $this")
                                setRandomResponseInUi(this)
                            }
                        }
                    }
                    is ResourceState.Errors -> {
                        Timber.d("dish error state: ${it.error}")
                        setMinimumUiPresentation(it.error)
                        errorPopUpNavigateBackToAllDishes()
                    }
                    else -> Unit
                }
            }
        }
    }

    /**
     * Service call method to dish data then
     * mRandomDishViewModel.randomDishResponse.observe - will take care to set the ui with the new dish
     * mRandomDishViewModel.randomDishLoadingError.observe - take card on the error service response
     * mRandomDishViewModel.loadRandomDish.observe - take care of loading dish only from the service
     */
    private fun initRandomDishesViewModelObserverTwo() {
        /*** Calling the dish data from service */
        mRandomDishViewModel.getRandomDishState().asLiveData().observe(viewLifecycleOwner) {
            when (it) {
                is ResourceState.Load -> {
                    Timber.i("dish loading state: ${it.load}")
                    refreshingHandler(500)
                    setShimmer(mBinding!!.shimmerImage,mBinding!!.ivDishImage, 1500)
                }
                is ResourceState.Service -> {
                    it.randomDishApi?.let { response ->
                        response.recipes[0].apply {
                            Timber.i("dish response: $this")
                            setRandomResponseInUi(this)
                        }
                    }
                }
                is ResourceState.Errors -> {
                    Timber.e("dish error state: ${it.error}")
                    setMinimumUiPresentation(it.error)
                    errorPopUpNavigateBackToAllDishes()
                }
                else -> Unit
            }
        }
    }

    /**
     * load dish image and recipe data to ui
     * using the RandomDish.Recipe to have bind with the ui presentation
     * finally set the dish to data base room storage
     */
    private fun setRandomResponseInUi(recipe : Recipe) {
        setPicture(recipe.image)
        //Set the dish title
        mBinding!!.tvTitle.text = recipe.title
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

    /*** Default Dish Type if the dish types are empty */
    private fun setDishType(recipe: Recipe): String {
        val dishType = if (recipe.dishTypes.isNotEmpty()) {
            recipe.dishTypes[0]
        } else "empty"
        mBinding!!.tvType.text = dishType
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

        recipe.extendedIngredients.forEach {
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

            /*** validate and updated the indications for the dish status new/exists */
            var isNewDish = true
            val titles = arrayListOf<String>()
            myDishViewModel.allDishesList.observe(viewLifecycleOwner) {
                it.forEach { item -> titles.add(item.title) }
                if (titles.contains(recipe.title)) isNewDish = false
            }

            if (isNewDish) {
                /*** insert the new randomDishDetails MyDishEntity object to room data base */
                myDishViewModel.insert(randomDishDetails)
                /*** present the drawable image with selected favorite dish */
                setImageDrawable(mBinding!!.ivFavoriteDish, R.drawable.ic_favorite_selected)
                /*** recipe title + the dish_is_selected label will present toast message about new dish added to favorite dishes */
                toast(requireActivity(),recipe.title+" "+resources.getString(R.string.dish_is_selected)).show()
                Timber.i("${recipe.title} is entered to room data base")
            } else {
                /*** recipe title + the dish all ready label exists in the favorite dishes */
                toast(requireActivity(),"${recipe.title} dish is all ready in your favorite dishes").show()
                Timber.i("${recipe.title} all ready exists in room data base")
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun setMinimumUiPresentation(isIgnoreFullUiPresentation: String) {
        if (isIgnoreFullUiPresentation.isNotEmpty()) {
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

    private fun errorPopUpNavigateBackToAllDishes() {
        val builder = AlertDialog.Builder(this.requireActivity())
            .setTitle(resources.getString(R.string.dish_error_pop_up_title))
            .setMessage(resources.getString(R.string.dish_error_pop_up_message))
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                val navDirection = RandomDishFragmentDirections.actionNavigationRandomDishToNavigationAllDishes()
                findNavController().navigate(directions = navDirection)
            }

        builder.create().let {
            it.setCancelable(false)
            it.show()
        }

//        val alertDialog: AlertDialog = builder.create()
//        alertDialog.setCancelable(false)
//        alertDialog.show()
    }

    /** Implement the listeners to get the bitmap. Load the dish image in the image view **/
    private fun setPicture(image: String) {
        try {
            Glide.with(this@RandomDishFragment)
                .load(image)
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?, model: Any?, target: Target<Drawable>?,
                        isFirstResource: Boolean): Boolean {
                        Timber.e("loading image "+ if (e != null) e.message else model.toString())
                        errorPopUpNavigateBackToAllDishes()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable, model: Any?, target: Target<Drawable>?,
                        dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        Timber.i("loading image ${model.toString()}")
                        setPalette(mBinding!!.rlDishDetailMain, resource, mBinding!!.tvTitle)
                        return false
                    }
                })
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(mBinding!!.ivDishImage)
        } catch (e: Exception) {
            when(e) {
                is IOException -> Timber.e("io exception loading image ${e.message}")
                is NullPointerException -> Timber.e("null exception loading image ${e.message}")
                else -> Timber.e("exception loading image ${e.message}")
            }
        }
    }
}