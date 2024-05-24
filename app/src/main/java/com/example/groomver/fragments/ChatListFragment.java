// ChatListFragment.java
package com.example.groomver.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.groomver.R;
import com.example.groomver.models.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    private RecyclerView recyclerViewChats;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        recyclerViewChats = view.findViewById(R.id.recyclerViewChats);
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList, this::openChat);

        recyclerViewChats.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChats.setAdapter(chatAdapter);

        // Load chats from database or any other source
        loadChats();

        return view;
    }

    private void loadChats() {
        // Dummy data for demonstration
        chatList.add(new Chat("Chat 1", "Last message 1"));
        chatList.add(new Chat("Chat 2", "Last message 2"));
        chatAdapter.notifyDataSetChanged();
    }

    private void openChat(Chat chat) {
        // Open the chat fragment with the selected chat
        Bundle args = new Bundle();
        args.putString("chatName", chat.getChatName());
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.bottomNavigationView, chatFragment)
                .addToBackStack(null)
                .commit();
    }
}
