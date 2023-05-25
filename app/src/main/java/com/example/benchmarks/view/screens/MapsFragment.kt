package com.example.benchmarks.view.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.benchmarks.databinding.FragmentMapsBinding
import com.example.benchmarks.factory
import com.example.benchmarks.view.adapters.OperationListAdapter
import com.example.benchmarks.viewmodel.MapsFragmentViewModel


class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding: FragmentMapsBinding
        get() = _binding ?: throw RuntimeException("FragmentMapsBinding == null")

    private val viewModel by lazy {
        ViewModelProvider(this, factory())[MapsFragmentViewModel::class.java]
    }

    private val operationAdapter: OperationListAdapter by lazy {
        OperationListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observedViewModel()
        binding.recycler.adapter = operationAdapter
        binding.buttonStart.setOnClickListener {
            val size = binding.etTimesAmount.text.toString().toInt()
            viewModel.startCollections(size)
        }
    }

    private fun observedViewModel() {
        viewModel.operations.observe(viewLifecycleOwner) {
            operationAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}