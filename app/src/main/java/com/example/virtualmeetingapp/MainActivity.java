package com.example.virtualmeetingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.example.virtualmeetingapp.SpaceTabLayout.SpaceTabLayoutUserType.SpaceTabLayout1;
import com.example.virtualmeetingapp.SpaceTabLayout.SpaceTabLayoutUserType.SpaceTabLayout2;
import com.example.virtualmeetingapp.SpaceTabLayout.fragments.AddOfficerFragment;
import com.example.virtualmeetingapp.SpaceTabLayout.fragments.ChatListFragment;
import com.example.virtualmeetingapp.SpaceTabLayout.fragments.FragmentB;
import com.example.virtualmeetingapp.SpaceTabLayout.fragments.FragmentC;
import com.example.virtualmeetingapp.SpaceTabLayout.fragments.FragmentD;
import com.example.virtualmeetingapp.SpaceTabLayout.fragments.UsersFragment;
import com.example.virtualmeetingapp.models.User;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import carbon.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    SpaceTabLayout1 tabLayout1;
    SpaceTabLayout2 tabLayout2;
    private User user;
    FirebaseAuth auth;
    DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        tabLayout1 = (SpaceTabLayout1) findViewById(R.id.spaceTabLayout1);
//        tabLayout2 = (SpaceTabLayout2) findViewById(R.id.spaceTabLayout2);
        auth = FirebaseAuth.getInstance();

//        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
//        editor.putString("profileid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
//        editor.apply();

//        Intent intent = getIntent();
//        intent.getStringExtra("uid");
//        Bundle intent = getIntent().getExtras();
//        String user = intent.getString("uid", auth.getCurrentUser().getUid());
//        if (intent != null) {
//
//
//            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
//            editor.putString(TextUtil.PREF_LATEST_USER_ID, user);
//            editor.apply();
//
//            List<Fragment> fragmentList = new ArrayList<>();
//            fragmentList.add(new FragmentA());
//
//        }
        //get user object from shared preference
//        user = (User) new SystemPrefs(this).getOjectData(Constants.USER, User.class);
//        user = new SystemPrefs().getUserSession();

    user = (User) new SystemPrefs().getUserSession();
        String userType = user.getUserType();
//        String id = user.getId();
        String id = user.getUserEmail();
//        String userType = "admin";

        //other info of user
//        String id = user.getId();
//        String userName = user.getUserName();
        Log.d("userType", userType);


//            if (userType != null) {

        if (userType.equals("visitor")) {
            tabLayout1.setVisibility(View.VISIBLE);
            tabLayout2.setVisibility(View.GONE);
            visitor();
        } else if (userType.equals("officer")) {
            tabLayout1.setVisibility(View.GONE);
            tabLayout2.setVisibility(View.VISIBLE);
            officer();
        } else if (userType.equals("admin")) {
            tabLayout1.setVisibility(View.GONE);
            tabLayout2.setVisibility(View.VISIBLE);
            admin();
        } else {
//            tabLayout1.setVisibility(View.VISIBLE);
//            tabLayout2.setVisibility(View.GONE);
            prisoner();
        }
    }


    @Override
    public void onBackPressed() {

        finish();

    }

    private void visitor() {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main);
        List<Fragment> fragmentList = new ArrayList<>();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);


        fragmentList.add(new UsersFragment());
        fragmentList.add(new FragmentB());
        fragmentList.add(new ChatListFragment());

        tabLayout1.initialize(viewPager, getSupportFragmentManager(), fragmentList);

        tabLayout1.setTabOneOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Welcome to SpaceTabLayout", Snackbar.LENGTH_SHORT);

                snackbar.show();
            }
        });

        tabLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "" + tabLayout1.getCurrentPosition(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void officer() {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main);
        List<Fragment> fragmentList = new ArrayList<>();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        fragmentList.add(new UsersFragment());
        fragmentList.add(new AddOfficerFragment());
        fragmentList.add(new FragmentC());
        fragmentList.add(new FragmentD());
        fragmentList.add(new ChatListFragment());

        tabLayout2.initialize(viewPager, getSupportFragmentManager(), fragmentList);

        tabLayout2.setTabOneOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Welcome to SpaceTabLayout", Snackbar.LENGTH_SHORT);

                snackbar.show();
            }
        });

        tabLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "" + tabLayout1.getCurrentPosition(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void admin() {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main);
        List<Fragment> fragmentList = new ArrayList<>();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        fragmentList.add(new UsersFragment());
        fragmentList.add(new AddOfficerFragment());
        fragmentList.add(new FragmentC());
        fragmentList.add(new FragmentD());
        fragmentList.add(new ChatListFragment());

        tabLayout2.initialize(viewPager, getSupportFragmentManager(), fragmentList);

        tabLayout2.setTabOneOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Welcome to SpaceTabLayout", Snackbar.LENGTH_SHORT);

                snackbar.show();
            }
        });

        tabLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "" + tabLayout1.getCurrentPosition(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prisoner() {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main);
        List<Fragment> fragmentList = new ArrayList<>();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

//        fragmentList.add(new UsersFragment());
//        fragmentList.add(new FragmentB());
        fragmentList.add(new ChatListFragment());

//        tabLayout1.initialize(viewPager, getSupportFragmentManager(), fragmentList);
//
//        tabLayout1.setTabOneOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar snackbar = Snackbar
//                        .make(coordinatorLayout, "Welcome to SpaceTabLayout", Snackbar.LENGTH_SHORT);
//
//                snackbar.show();
//            }
//        });

//        tabLayout1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplication(), "" + tabLayout1.getCurrentPosition(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}