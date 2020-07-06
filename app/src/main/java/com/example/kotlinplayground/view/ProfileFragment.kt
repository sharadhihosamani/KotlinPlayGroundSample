package com.example.kotlinplayground.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinplayground.R
import com.example.kotlinplayground.databinding.FragmentProfileBinding
import com.example.kotlinplayground.user.*

class ProfileFragment : Fragment() , UIEventManager{

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentProfileBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        val viewModelFactory = ProfileViewModelFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
        loadData();
    }

    private fun loadData() {
        viewModel.loadDataFromWeb().observe(viewLifecycleOwner, Observer {setdata()
        })
    }

    private fun setdata()
    {

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