package io.tigh.searx.ui.searchresults

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.tigh.searx.R
import io.tigh.searx.api.Searxng
import io.tigh.searx.api.SearxngApiService
import io.tigh.searx.api.SearxngResult
import io.tigh.searx.api.SearxngResultItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
 * Use the [SearchResultsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchResultsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mResultsRecyclerView: RecyclerView

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
        return inflater.inflate(R.layout.fragment_search_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val term = requireActivity().intent?.getStringExtra("searxng_search_term")!!

        mResultsRecyclerView = view.findViewById(R.id.rv_search_results)
        mResultsRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        CoroutineScope(Dispatchers.IO).launch {
            Log.d(javaClass.name, "OH WOW HOLY HELL")
            Searxng("http://192.168.250.13").apply {
                q = term
                format = "json"
            }.search(requireActivity())
            val result = Retrofit.Builder()
                    .baseUrl("http://192.168.250.13")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(SearxngApiService::class.java)
                    .search(term, "json")

            if (result.isSuccessful) {
                val data = result.body()!!.results
                withContext(Dispatchers.Main) {
                    val adapter =  SearchResultsAdapter(data)
                    adapter.setOnTouchListener(object: SearchResultsAdapter.OnTouchListener {
                        override fun onTouch(uri: Uri): Boolean {
                            startActivity(Intent(Intent.ACTION_VIEW, uri))
                            return true
                        }
                    })
                    mResultsRecyclerView.adapter = adapter
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchResultsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchResultsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}