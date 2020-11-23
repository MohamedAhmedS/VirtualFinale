package com.example.virtualmeetingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.virtualmeetingapp.adapter.TabFragmentAdapter;
import com.example.virtualmeetingapp.fragments.UserLoginFragment;
import com.example.virtualmeetingapp.fragments.viewPagerFragment.VisitorRegister;
import com.google.android.material.tabs.TabLayout;

import carbon.widget.ViewPager;


public class ClientTypeActivity extends AppCompatActivity {

    TabLayout mTabs;
    View mIndicator;
    ViewPager mViewPager;

    private int indicatorWidth;

    private void init() {
//        spinnerClient = findViewById(R.id.client);
        mTabs = findViewById(R.id.tab);
        mIndicator = findViewById(R.id.indicator);
        mViewPager = findViewById(R.id.viewPager);
    }

    private void ViewPagerTabFragment() {
        //Set up the view pager and fragments
        TabFragmentAdapter adapter = new TabFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(VisitorRegister.newInstance(), "Visitor");
//        adapter.addFragment(OfficerLogin.newInstance(), "Officer");
//        adapter.addFragment(InmateLogin.newInstance(), "Inmate");
//        adapter.addFragment(AdminLogin.newInstance(), "Admin");
        adapter.addFragment(UserLoginFragment.newInstance(), "Users");
        mViewPager.setAdapter(adapter);
        mTabs.setupWithViewPager(mViewPager);

        //Determine indicator width at runtime
        mTabs.post(new Runnable() {
            @Override
            public void run() {
                indicatorWidth = mTabs.getWidth() / mTabs.getTabCount();

                //Assign new width
                FrameLayout.LayoutParams indicatorParams = (FrameLayout.LayoutParams) mIndicator.getLayoutParams();
                indicatorParams.width = indicatorWidth;
                mIndicator.setLayoutParams(indicatorParams);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            //To move the indicator as the user scroll, we will need the scroll offset values
            //positionOffset is a value from [0..1] which represents how far the page has been scrolled
            @Override
            public void onPageScrolled(int i, float positionOffset, int positionOffsetPx) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mIndicator.getLayoutParams();

                //Multiply positionOffset with indicatorWidth to get translation
                float translationOffset = (positionOffset + i) * indicatorWidth;
                params.leftMargin = (int) translationOffset;
                mIndicator.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

//    private void SpinnerArrayAdapter() {
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.clients, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerClient.setPrompt("Select Client Type . . .");
//
//        spinnerClient.setAdapter(
//                new NothingSelectedSpinnerAdapter(
//                        adapter,
//                        R.layout.contact_spinner_row_nothing_selected,
//                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
//                        this));
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_type_activity);

        init();
        ViewPagerTabFragment();
//        SpinnerArrayAdapter();

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (firebaseUser != null) {
//            Intent homeIntent = new Intent(this, MainActivity.class);
//            startActivity(homeIntent);
//            finish();
//        }
//    }
}