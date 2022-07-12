package com.example.mccchatapp.ui.userlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mccchatapp.activities.ChatActivity;
import com.example.mccchatapp.activities.UsersActivity;
import com.example.mccchatapp.adapters.UserListAdapter;
import com.example.mccchatapp.databinding.FragmentUserListBinding;
import com.example.mccchatapp.listeners.UserListListener;
import com.example.mccchatapp.models.User;
import com.example.mccchatapp.models.UserList;
import com.example.mccchatapp.utilities.Constants;
import com.example.mccchatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserListFragment extends Fragment implements UserListListener {

    private FragmentUserListBinding binding;
    private PreferenceManager preferenceManager;
    private List<UserList> userLists;
    private UserListAdapter userListAdapter;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUserListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());

        init();
        listenConversations();
        setListener();

        return root;
    }

    private void setListener() {
        binding.btnSearch.setOnClickListener(view -> startActivity(new Intent(getContext().getApplicationContext(), UsersActivity.class)));
    }

    private void init()  {

        userLists = new ArrayList<>();
        userListAdapter = new UserListAdapter(userLists, this);
        binding.userListRecyclerViewer.setAdapter(userListAdapter);

    }


    private void listenConversations() {

        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    UserList userList = new UserList();
                    userList.senderId = senderId;
                    userList.receiverId = receiverId;
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        userList.conversationImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        userList.conversationName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        userList.conversationId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    } else {
                        userList.conversationImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        userList.conversationName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        userList.conversationId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    userLists.add(userList);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < userLists.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (userLists.get(i).senderId.equals(senderId) && userLists.get(i).receiverId.equals(receiverId)) {
                            if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                                userLists.get(i).conversationImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                                userLists.get(i).conversationName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                            } else {
                                userLists.get(i).conversationImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                                userLists.get(i).conversationName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                            }
                            break;
                        }
                    }
                }
            }
            Collections.sort(userLists, (obj1, obj2) -> obj1.conversationName.compareTo(obj2.conversationName));
            userListAdapter.notifyDataSetChanged();
            binding.userListRecyclerViewer.smoothScrollToPosition(0);
            binding.userListRecyclerViewer.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onUserListClicked(User user) {
        Intent intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }

}