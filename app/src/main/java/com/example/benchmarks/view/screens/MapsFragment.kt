package com.example.benchmarks.view.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.benchmarks.R
import com.example.benchmarks.databinding.FragmentMapsBinding
import com.example.benchmarks.factory
import com.example.benchmarks.view.adapters.OperationListAdapter
import com.example.benchmarks.viewmodel.CollectionsSize
import com.example.benchmarks.viewmodel.Error
import com.example.benchmarks.viewmodel.Executing
import com.example.benchmarks.viewmodel.MapsFragmentViewModel
import com.example.benchmarks.viewmodel.Result


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
        setButtonListener()
    }

    private fun setButtonListener() {
        binding.buttonStart.setOnClickListener {
            val size = binding.etTimesAmount.text.toString().toInt()
            viewModel.startExecution(size)
        }
    }

    private fun observedViewModel() {
        with(binding) {
            viewModel.state.observe(viewLifecycleOwner) { state ->
                tilTimesAmount.error = null

                when (state) {
                    is Error -> {
                        tilTimesAmount.error = "Error. You need enter elements count."
                    }
                    is Executing -> {
                        if (state.isExecuting) {
                            buttonStart.text = getString(R.string.stop)
                            buttonStart.setBackgroundResource(R.drawable.button_stop_background)
                            buttonStart.setOnClickListener {
                                viewModel.stopExecution()
                            }
                        } else {
                            buttonStart.text = getString(R.string.start)
                            buttonStart.setBackgroundResource(R.drawable.button_start_background)
                            setButtonListener()
                        }
                    }
                    is Result -> {
                        operationAdapter.submitList(state.operations)
                    }
                    is CollectionsSize -> {
                        etTimesAmount.setText(state.size.toString())
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}