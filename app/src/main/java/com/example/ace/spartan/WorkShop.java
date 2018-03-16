package com.example.ace.spartan;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class WorkShop extends AppCompatActivity {
    private TextView mTextMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_work_shop);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fr;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fr=new Home();
                        pushFragment(fr);
                        return true;
                    case R.id.navigation_dashboard:
                        fr=new Search_Workshop();
                        pushFragment(fr);
                        return true;
                    case R.id.navigation_notifications:
                        fr=new Notification();
                        pushFragment(fr);
                        return true;
                }
                return false;
            }
        });
        Fragment fr=new Search_Workshop();
        FragmentTransaction fragmentManager=getFragmentManager().beginTransaction();
fragmentManager.replace(R.id.MAIN_FRAME,fr).commit();
navigation.setSelectedItemId(R.id.navigation_dashboard);
    }
    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;
       FragmentManager fragmentManager =getFragmentManager();
        if (fragmentManager != null) {
          FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {

                ft.replace(R.id.MAIN_FRAME, fragment);
                ft.commit();


            }
        }
    }

}
