package com.example.kotlinplayground.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinplayground.R
import com.example.kotlinplayground.model.NewArrivals
import com.example.kotlinplayground.user.AdapterUser
import com.example.kotlinplayground.user.HomeViewModel
import com.example.kotlinplayground.user.HomeViewModelFactory
import com.example.kotlinplayground.user.UIEventManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(), UIEventManager {

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModelFactory = HomeViewModelFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        init()
    }

    private fun setDataToRecyclerView(userList: List<NewArrivals>) {
        view?.findViewById<RecyclerView>(R.id.recyclerViewMain)?.apply {

            layoutManager = LinearLayoutManager(this.context)
            adapter = AdapterUser(userList)
        }
    }

    private fun init() {
        view?.findViewById<Button>(R.id.btn_clear)?.setOnClickListener {
            setDataToRecyclerView(listOf())
        }

        view?.findViewById<Button>(R.id.btn_load_data)?.setOnClickListener {
            loadData()
        }
    }

    private fun loadData() {
        viewModel.loadDataFromWeb().observe(viewLifecycleOwner, Observer {
            setDataToRecyclerView(it)
        })
    }

    override fun showToast(text: String) {
        Toast.makeText(this.context, text, Toast.LENGTH_LONG).show()
    }

    override fun showProgressBar() {
        view?.findViewById<ProgressBar>(R.id.progress_bar)?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        view?.findViewById<ProgressBar>(R.id.progress_bar)?.visibility = View.GONE
    }
}