package com.cureya.intern.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cureya.intern.UserDetail
import com.cureya.intern.databinding.UsersListItemBinding

/**
 * @author Udhaya
 * Created on 27-05-2022
 */

class UsersAdapter(
    private val onClicked: (UserDetail) -> Unit,
) : ListAdapter<UserDetail, UsersAdapter.ViewHolder>(DIFF_CALLBACK) {
    companion object {
        object DIFF_CALLBACK : DiffUtil.ItemCallback<UserDetail>() {
            override fun areItemsTheSame(oldItem: UserDetail, newItem: UserDetail): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserDetail, newItem: UserDetail): Boolean {
                return oldItem == newItem
            }

        }
    }

    class ViewHolder(
        private val binding: UsersListItemBinding,
        private val onClicked: (UserDetail) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserDetail) {
            binding.mail.text = item.email
            binding.root.setOnClickListener {
                onClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            UsersListItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false),
            onClicked
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}