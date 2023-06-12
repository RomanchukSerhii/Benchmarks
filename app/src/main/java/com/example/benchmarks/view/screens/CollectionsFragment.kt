package com.example.benchmarks.view.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.benchmarks.R
import com.example.benchmarks.view.adapters.OperationListAdapter
import com.example.benchmarks.databinding.FragmentCollectionsBinding
import com.example.benchmarks.factory
import com.example.benchmarks.viewmodel.CollectionsFragmentViewModel
import com.example.benchmarks.viewmodel.CollectionsSize
import com.example.benchmarks.viewmodel.Executing
import com.example.benchmarks.viewmodel.Result
import com.example.benchmarks.viewmodel.Error

class CollectionsFragment : Fragment() {

    private var _binding: FragmentCollectionsBinding? = null
    private val binding: FragmentCollectionsBinding
        get() = _binding ?: throw RuntimeException("FragmentCollectionsBinding == null")

    private val viewModel by lazy {
        ViewModelProvider(this, factory())[CollectionsFragmentViewModel::class.java]
    }

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
        observedViewModel()
        binding.recycler.adapter = operationAdapter
        setButtonListener()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val enteredText = binding.etTimesAmount.text.toString()
        val size = enteredText.toIntOrNull()
        if (size != null) {
            outState.putInt(ARG_COLLECTIONS_SIZE, size)
        }
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

    companion object {
        private const val ARG_COLLECTIONS_SIZE = "ARG_COLLECTIONS_SIZE"
    }
}