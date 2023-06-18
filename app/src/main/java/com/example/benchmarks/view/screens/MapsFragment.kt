package com.example.benchmarks.view.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.benchmarks.App
import com.example.benchmarks.R
import com.example.benchmarks.databinding.FragmentMapsBinding
import com.example.benchmarks.view.MainActivity
import com.example.benchmarks.view.adapters.OperationListAdapter
import com.example.benchmarks.viewmodel.CollectionsSize
import com.example.benchmarks.viewmodel.Error
import com.example.benchmarks.viewmodel.Executing
import com.example.benchmarks.viewmodel.MapsFragmentViewModel
import com.example.benchmarks.viewmodel.Result
import com.example.benchmarks.viewmodel.ViewModelFactory
import javax.inject.Inject


class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding: FragmentMapsBinding
        get() = _binding ?: throw RuntimeException("FragmentMapsBinding == null")

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MapsFragmentViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as App).component
    }

    private val operationAdapter: OperationListAdapter by lazy {
        OperationListAdapter()
    }

    private val collectionsSize: String
        get() = requireArguments().getString(MainActivity.ARG_COLLECTIONS_SIZE) ?: ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        component.inject(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setValueInEditText(savedInstanceState)
        observedViewModel()
        binding.recycler.adapter = operationAdapter
        setButtonListener()
        setupValidateDialogListener()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val enteredText = binding.etTimesAmount.text.toString()
        outState.putString(ARG_MAPS_SIZE, enteredText)
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopExecution()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setValueInEditText(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val size = savedInstanceState.getString(ARG_MAPS_SIZE) ?: ""
            binding.etTimesAmount.setText(size)
            viewModel.validateCollectionSize(size)
        } else {
            binding.etTimesAmount.setText(collectionsSize)
            viewModel.validateCollectionSize(collectionsSize)
        }
    }

    private fun setButtonListener() {
        binding.buttonStart.setOnClickListener {
            val enteredText = binding.etTimesAmount.text.toString()
            viewModel.startExecution(enteredText)
        }
    }

    private fun observedViewModel() {
        with(binding) {
            viewModel.state.observe(viewLifecycleOwner) { state ->
                tilTimesAmount.error = null

                when (state) {
                    is Error -> {
                        showValidateDialogFragment(state.size)
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

    private fun showValidateDialogFragment(size: Int) {
        ValidateDialogFragment.show(parentFragmentManager, size, MAPS_FRAGMENT_REQUEST_KEY)
    }

    private fun setupValidateDialogListener() {
        val listener: ValidateDialogListener = { requestKey, collectionsSize ->
            if (requestKey == MAPS_FRAGMENT_REQUEST_KEY) {
                binding.etTimesAmount.setText(collectionsSize.toString())
            }
        }
        ValidateDialogFragment.setupListener(
            parentFragmentManager,
            viewLifecycleOwner,
            MAPS_FRAGMENT_REQUEST_KEY,
            listener
        )
    }

    companion object {
        private const val ARG_MAPS_SIZE = "ARG_MAPS_SIZE"
        private const val MAPS_FRAGMENT_REQUEST_KEY = "MAPS_FRAGMENT_REQUEST_KEY"
    }
}