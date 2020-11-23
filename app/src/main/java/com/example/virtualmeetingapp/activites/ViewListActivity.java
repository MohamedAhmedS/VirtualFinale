package com.example.virtualmeetingapp.activites;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.virtualmeetingapp.R;
import com.example.virtualmeetingapp.fragments.OfficersListFragment;
import com.example.virtualmeetingapp.fragments.PrisonersListFragment;
import com.example.virtualmeetingapp.fragments.VisitorsListFragment;
import com.example.virtualmeetingapp.utils.Constants;
import com.example.virtualmeetingapp.utils.SystemPrefs;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import carbon.widget.ViewPager;

public class ViewListActivity extends BaseActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void initXML() {

    }

    @Override
    public void initVariables() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

//        db.collection(Constants.USERS_COLLECTION).document(str_email).get().addOnCompleteListener(fetchUserTask -> {
//            if (fetchUserTask.isSuccessful() && fetchUserTask.getResult() != null) {
//            }
//        });


            if (new SystemPrefs().getUserType().equals(Constants.USER_TYPE_VISITOR)) {
                adapter.addFragment(new OfficersListFragment(), "Officers List");
                adapter.addFragment(new PrisonersListFragment(), "Prisoners List");
            } else {
                adapter.addFragment(new OfficersListFragment(), "Officers List");
                adapter.addFragment(new PrisonersListFragment(), "Prisoners List");
                adapter.addFragment(new VisitorsListFragment(), "Visitors List");
            }

//        adapter.addFragment(new OfficersListFragment(), "Officers List");
//        adapter.addFragment(new PrisonersListFragment(), "Prisoners List");
//        adapter.addFragment(new VisitorsListFragment(), "Visitors List");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mList = new ArrayList<>();
        private final List<String> mTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
            return mList.get(i);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mList.add(fragment);
            mTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }
    }

}