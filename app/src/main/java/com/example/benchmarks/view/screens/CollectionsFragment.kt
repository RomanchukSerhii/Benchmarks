package com.example.benchmarks.view.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.benchmarks.view.adapters.OperationListAdapter
import com.example.benchmarks.databinding.FragmentCollectionsBinding
import com.example.benchmarks.model.enums.CollectionsName
import com.example.benchmarks.model.Operation
import com.example.benchmarks.model.OperationsService
import com.example.benchmarks.model.enums.OperationsName

class CollectionsFragment : Fragment() {

    private var _binding: FragmentCollectionsBinding? = null
    private val binding: FragmentCollectionsBinding
        get() = _binding ?: throw RuntimeException("FragmentCollectionsBinding == null")

    private val operationAdapter: OperationListAdapter by lazy {
        OperationListAdapter()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCollectionsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        operationAdapter.submitList(OperationsService().getOperationList())
        binding.recycler.adapter = operationAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}