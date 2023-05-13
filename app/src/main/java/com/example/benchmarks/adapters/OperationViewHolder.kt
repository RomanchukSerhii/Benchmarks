package com.example.benchmarks.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.benchmarks.R
import com.example.benchmarks.databinding.OperationItemBinding
import com.example.benchmarks.model.Operation

class OperationViewHolder(
    private var binding: OperationItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(operation: Operation, context: Context) {
        binding.tvResult.text = context.getString(
            R.string.average_execution_time,
            operation.operationName,
            operation.collectionName,
            operation.averageExecutionTime
        )
    }
}