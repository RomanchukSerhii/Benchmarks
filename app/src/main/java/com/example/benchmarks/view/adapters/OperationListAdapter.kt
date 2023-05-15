package com.example.benchmarks.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.benchmarks.R
import com.example.benchmarks.databinding.OperationItemBinding
import com.example.benchmarks.model.Operation

class OperationListAdapter : ListAdapter<Operation, OperationListAdapter.OperationViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationViewHolder {
        val binding = OperationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OperationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OperationViewHolder, position: Int) {
        val currentOperation = getItem(position)
        holder.bind(currentOperation, holder.itemView.context)
    }

    class OperationViewHolder(
        private var binding: OperationItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(operation: Operation, context: Context) {
            with(binding) {
                if (operation.isExecuted) {
                    progressExecuted.visibility = View.VISIBLE
                    tvResult.setTextColor(
                        ContextCompat.getColor(context, R.color.gray_transparent)
                    )
                } else {
                    progressExecuted.visibility = View.GONE
                    tvResult.setTextColor(
                        ContextCompat.getColor(context, R.color.black)
                    )
                }
                tvResult.text = context.getString(
                    R.string.average_execution_time,
                    operation.operationName,
                    operation.collectionName,
                    operation.averageExecutionTime
                )
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Operation>() {
            override fun areItemsTheSame(oldItem: Operation, newItem: Operation): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Operation, newItem: Operation): Boolean {
                return oldItem.operationName == newItem.operationName
            }
        }
    }
}