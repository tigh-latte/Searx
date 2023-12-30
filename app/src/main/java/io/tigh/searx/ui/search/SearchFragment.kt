package io.tigh.searx.ui.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Scene
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import io.tigh.searx.R
import io.tigh.searx.api.SearxngApiService
import io.tigh.searx.config.Config
import io.tigh.searx.ui.searchresults.SearchResultsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mSearchBar: LinearLayout
    private lateinit var mSearchBarInput: AutoCompleteTextView
    private lateinit var mSearchIcon: ImageView
    private lateinit var mSearchLogo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sceneRoot: ViewGroup = view.findViewById(R.id.fl_scene_root)
        val selectedScene: Scene = Scene.getSceneForLayout(sceneRoot, R.layout.fragment_search_highlighted, requireActivity())

        mSearchBar = view.findViewById(R.id.ll_searchbar)
        mSearchLogo = view.findViewById(R.id.iv_searxng_logo)
        mSearchBarInput = view.findViewById(R.id.actv_search_bar)

        mSearchBarInput.setOnItemClickListener { parent, view, position, id ->
            val item = parent.adapter.getItem(position)
            val i = with(Intent(requireActivity(), SearchResultsActivity::class.java)) {
                putExtra("searxng_search_term", item.toString())
            }
            startActivity(i)
        }
        mSearchBarInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                CoroutineScope(Dispatchers.IO).launch {
                    val resp = Retrofit.Builder()
                        .baseUrl(Config.baseURL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(SearxngApiService::class.java)
                        .autocomplete(s.toString(), Config.autocompleteProvider)

                    if (!resp.isSuccessful) {
                        return@launch
                    }

                    withContext(Dispatchers.Main) {
                        val adapter = ArrayAdapter(requireActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, resp.body()!!)
                        mSearchBarInput.setAdapter(adapter)
                        if (!mSearchBarInput.isPopupShowing) {
                            mSearchBarInput.showDropDown()
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        Picasso.get()
            .load(Uri.parse(Config.baseURL+"/static/themes/simple/img/searxng.png"))
            .into(mSearchLogo)

        mSearchIcon = view.findViewById(R.id.image_view_search_icon)
        mSearchIcon.setOnTouchListener { v, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    false
                }
                MotionEvent.ACTION_UP -> {
                    val i = with(Intent(requireActivity(), SearchResultsActivity::class.java)) {
                        putExtra("searxng_search_term", mSearchBarInput.text.toString())
                    }
                    startActivity(i)
                    false
                }
                else -> false
            }
        }

        mSearchBarInput.setOnEditorActionListener { v, actionId, event ->
            when(actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    val i = with(Intent(requireActivity(), SearchResultsActivity::class.java)) {
                        putExtra("searxng_search_term", mSearchBarInput.text.toString())
                    }
                    startActivity(i)
                    return@setOnEditorActionListener true
                }
            }
            false
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}