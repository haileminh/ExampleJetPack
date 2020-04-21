package com.example.exampledemo.ui.demo.chat

import com.example.exampledemo.R
import com.example.exampledemo.app.BaseFragment
import com.example.exampledemo.databinding.FragmentListChatBinding

class ChatFragment : BaseFragment<FragmentListChatBinding, ChatViewModel>() {
    override fun getLayoutID(): Int {
        return R.layout.fragment_list_chat
    }

    override fun getViewModelClass(): Class<ChatViewModel> {
        return ChatViewModel::class.java
    }

    override fun onBinding() {

    }
}
