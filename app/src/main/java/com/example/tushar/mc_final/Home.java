package com.example.tushar.mc_final;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class Home extends AppCompatActivity {



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //fragment1
                    break;
                case R.id.navigation_dashboard:
                    //fragment2
                    //setTitle("feed");
                    selectedFragment = new FeedFragment();
                    android.support.v4.app.FragmentManager fragmentManager2 = getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                    fragmentTransaction2.replace(R.id.content,selectedFragment);
                    fragmentTransaction2.commit();
                    break;
                case R.id.navigation_notifications:
                    //fragment3
                    //setTitle("chat");
                    selectedFragment = new ChatFragment();
                    android.support.v4.app.FragmentManager fragmentManager3 = getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
                    fragmentTransaction3.replace(R.id.content,selectedFragment);
                    fragmentTransaction3.commit();
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
