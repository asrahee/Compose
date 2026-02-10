package com.example.compose.ui.main

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.compose.R
import com.example.compose.ui.main.FragmentViewmodelFragment.Companion.ARG_PARAM_NAME

class FragmentViewmodelFragment : Fragment() {

    companion object {
        private const val ARG_PARAM_NAME = "param_name"

        fun newInstance(name: String) : FragmentViewmodelFragment {
            val fragment = FragmentViewmodelFragment()
            val args = Bundle().apply {
                putString(ARG_PARAM_NAME, name)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }
}