package com.example.benchmarks.view.screens

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import com.example.benchmarks.R
import com.example.benchmarks.databinding.PartElementsInputBinding

typealias ValidateDialogListener = (requestKey: String, inputNumber: Int) -> Unit

class ValidateDialogFragment : DialogFragment() {

    private val inputNumber: Int
        get() = requireArguments().getInt(ARG_INPUT_NUMBER)
    private val requestKey: String
        get() = requireArguments().getString(ARG_REQUEST_KEY) ?: ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBinding = PartElementsInputBinding.inflate(layoutInflater)
        dialogBinding.inputNumberEditText.setText(inputNumber.toString())

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_title))
            .setMessage(getString(R.string.dialog_message))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.confirm), null)
            .create()

        dialog.setOnShowListener {
            dialogBinding.inputNumberEditText.requestFocus()
            showKeyBoard(dialogBinding.inputNumberEditText)

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val enteredText = dialogBinding.inputNumberEditText.text.toString()
                if (enteredText.isBlank()) {
                    dialogBinding.inputNumberEditText.error = "Collection size is empty"
                    return@setOnClickListener
                }
                val inputNumber = enteredText.toIntOrNull()
                if (inputNumber == null || inputNumber < 1_000_000 || inputNumber > 10_000_000) {
                    dialogBinding.inputNumberEditText.error =
                        "Input number is not entered in recommended range"
                    return@setOnClickListener
                }
                parentFragmentManager.setFragmentResult(
                    requestKey, bundleOf(
                        KEY_INPUT_NUMBER_RESPONSE to inputNumber
                    )
                )
                dismiss()
            }
        }

        dialog.setOnDismissListener { hideKeyboard(dialogBinding.inputNumberEditText) }
        return dialog
    }

    private fun showKeyBoard(view: View) {
        view.post {
            getInputMethodManager(view).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyboard(view: View) {
        getInputMethodManager(view).hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getInputMethodManager(view: View): InputMethodManager {
        val context = view.context
        return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    companion object {
        private val TAG = ValidateDialogFragment::class.java.simpleName
        private const val ARG_REQUEST_KEY = "ARG_REQUEST_KEY"
        private const val ARG_INPUT_NUMBER = "ARG_INPUT_NUMBER"
        private const val KEY_INPUT_NUMBER_RESPONSE = "KEY_INPUT_NUMBER_RESPONSE"

        fun show(manager: FragmentManager, inputNumber: Int, requestKey: String) {
            val dialogFragment = ValidateDialogFragment()
            dialogFragment.arguments = bundleOf(
                ARG_REQUEST_KEY to requestKey,
                ARG_INPUT_NUMBER to inputNumber
            )
            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            requestKey: String,
            listener: ValidateDialogListener
        ) {
            manager.setFragmentResultListener(requestKey,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    listener.invoke(key, result.getInt(KEY_INPUT_NUMBER_RESPONSE))
                })
        }
    }
}