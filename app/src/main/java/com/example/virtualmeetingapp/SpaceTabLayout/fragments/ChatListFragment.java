package com.example.virtualmeetingapp.SpaceTabLayout.fragments;

import androidx.fragment.app.Fragment;

public class ChatListFragment extends Fragment {
//
//    //firebase auth
//    private FirebaseAuth firebaseAuth;
//    private RecyclerView recyclerView;
//    private List<ConversationModel> chatlistList;
//    private List<User> userList;
//    private DatabaseReference reference;
//    private FirebaseUser currentUser;
//    private ConversationAdapter conversationAdapter;
//    private EditText search_bar;
//    private ImageView btnClose;
//    private TextView noUser;
//
////    public ChatListFragment() {
////        // Required empty public constructor
////    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.activity_conversations, container, false);
//
//        //init
//        firebaseAuth = FirebaseAuth.getInstance();
//        noUser = view.findViewById(R.id.noUser);
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        search_bar = view.findViewById(R.id.search_bar);
//        btnClose = view.findViewById(R.id.btnClose);
//
//        recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        noUser.setVisibility(View.VISIBLE);
//
//        btnClose.setVisibility(View.GONE);
//
//
//        if(btnClose.getVisibility() == View.GONE) {
//            search_bar.setPadding(10, 0, 10, 0);
//        }
//
//        chatlistList = new ArrayList<>();
//
//        checkUserStatus();
//        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                chatlistList.clear();
//                noUser.setVisibility(View.VISIBLE);
//
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    ConversationModel chatlist = ds.getValue(ConversationModel.class);
//                    chatlistList.add(chatlist);
//                    noUser.setVisibility(View.GONE);
//                }
//                loadChats();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        search_bar.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                filter(editable.toString());
//                btnClose.setVisibility(View.VISIBLE);
//                if(search_bar.getText().toString().matches(""))
//                {
//                    btnClose.setVisibility(View.GONE);
//                }
//                btnClose.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        search_bar.setText("");
//                        btnClose.setVisibility(View.GONE);
//                    }
//                });
//
//            }
//        });
//        return view;
//    }
//
//    private void filter(String text) {
//        ArrayList<User> filteredList = new ArrayList<>();
//        noUser.setVisibility(View.VISIBLE);
//        for (User user : userList) {
//            if (user.getUserName().toLowerCase().contains(text.toLowerCase())) {
//                filteredList.add(user);
//                noUser.setVisibility(View.GONE);
//            }
//        }
//        conversationAdapter.filterList(filteredList);
//    }
//
//    private void loadChats() {
//        userList = new ArrayList<>();
//        reference = FirebaseDatabase.getInstance().getReference(Constants.COLLECTION_USER);
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userList.clear();
//                noUser.setVisibility(View.VISIBLE);
//
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    User user = ds.getValue(User.class);
//                    for (ConversationModel chatlist : chatlistList) {
//                        assert user != null;
////                        if (user.getId() != null && user.getId().equals(chatlist.getId())) {
//                        if (user.getUserEmail() != null && user.getUserEmail().equals(chatlist.getId())) {
//                            userList.add(user);
//                            noUser.setVisibility(View.GONE);
//                            break;
//                        }
//                    }
//                    //adapter
//                    conversationAdapter = new ConversationAdapter(getContext(), userList);
//                    //setadapter
//                    recyclerView.setAdapter(conversationAdapter);
//
//                    //set last message
//                    for (int i = 0; i < userList.size(); i++) {
////                        lastMessage(userList.get(i).getId());
//                        lastMessage(userList.get(i).getUserEmail());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void lastMessage(final String userId) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String theLastMessage = "default";
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    MessageModel chat = ds.getValue(MessageModel.class);
//                    if (chat == null) {
//                        continue;
//                    }
//                    String sender = chat.getSender();
//                    String receiver = chat.getReceiver();
//                    if (sender == null || receiver == null) {
//                        continue;
//                    }
//                    if (chat.getReceiver().equals(currentUser.getUid()) &&
//                            chat.getSender().equals(userId) ||
//                            chat.getReceiver().equals(userId) &&
//                                    chat.getSender().equals(currentUser.getUid())) {
//                        //instead of displaying url in message show "sent photo"
//                        if (chat.getType().equals("image")) {
//                            theLastMessage = "Sent a photo";
//                        } else {
//                            theLastMessage = chat.getMessage();
//                        }
//                    }
//                }
//                conversationAdapter = new ConversationAdapter(getContext(), userList);
//
//                conversationAdapter.setLastMessageMap(userId, theLastMessage);
//                recyclerView.setAdapter(conversationAdapter);
//                recyclerView.setItemViewCacheSize(1024);
//                conversationAdapter.notifyDataSetChanged();
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//
//    private void checkUserStatus() {
//        //get current user
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if (user != null) {
//            //user is signed in stay here
//            //set email of logged in user
//            //mProfileTv.setText(user.getEmail());
//        } else {
//            //user not signed in, go to main acitivity
//            startActivity(new Intent(getActivity(), MainActivity.class));
////            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//        }
//    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        setHasOptionsMenu(true);//to show menu option in fragment
//        super.onCreate(savedInstanceState);
//    }

    /*inflate options menu*/
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
//        //inflating menu
//        inflater.inflate(R.menu.menu_main, menu);
//
//        super.onCreateOptionsMenu(menu, inflater);
//    }
}