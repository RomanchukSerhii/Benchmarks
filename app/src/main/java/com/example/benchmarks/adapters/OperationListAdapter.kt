package com.example.benchmarks.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.benchmarks.databinding.OperationItemBinding
import com.example.benchmarks.model.Operation

class OperationListAdapter : ListAdapter<Operation, OperationViewHolder>(DiffCallback){

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