package com.example.benchmarks.adapters

import androidx.recyclerview.widget.RecyclerView
import com.example.benchmarks.databinding.OperationItemBinding
import com.example.benchmarks.model.Operation

class OperationViewHolder(
    private var binding: OperationItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(operation: Operation) {

    }
}