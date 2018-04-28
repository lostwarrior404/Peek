package com.example.tushar.mc_final;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity {
    FragmentManager mFragment_manager = getSupportFragmentManager();



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //fragment1
                    selectedFragment = new FriendFragment();
                    mFragment_manager.beginTransaction().replace(R.id.mainframe,selectedFragment).commit();
                    break;
                case R.id.navigation_dashboard:
                    //fragment2
                    //setTitle("feed");
                    selectedFragment = new FeedFragment();
                    mFragment_manager.beginTransaction().replace(R.id.mainframe,selectedFragment).commit();
                    break;
                case R.id.navigation_notifications:
                    //fragment3
                    //setTitle("chat");
                    selectedFragment = new ChatFragment();
                    mFragment_manager.beginTransaction().replace(R.id.mainframe,selectedFragment).commit();
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
