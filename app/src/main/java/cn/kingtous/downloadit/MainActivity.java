package cn.kingtous.downloadit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.kingtous.downloadit.Fragment.DataManagementFragment;
import cn.kingtous.downloadit.Fragment.DownloadFragment;
import cn.kingtous.downloadit.Fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private FrameLayout frameLayout;
    private SearchFragment searchFragment=new SearchFragment();
    private DownloadFragment downloadFragment=new DownloadFragment();
    private DataManagementFragment dataManagementFragment=new DataManagementFragment();

    FragmentManager manager=getSupportFragmentManager();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchFragment(searchFragment);
                    return true;
                case R.id.navigation_dashboard:
                    switchFragment(downloadFragment);
                    return true;
                case R.id.navigation_notifications:
                    switchFragment(dataManagementFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        frameLayout=findViewById(R.id.Frame);

        switchFragment(searchFragment);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    private void switchFragment(Fragment targetFragment){
        FragmentTransaction transaction=manager.beginTransaction();
        if (!targetFragment.isAdded()){
            transaction.add(R.id.Frame,targetFragment);
        }
        transaction.replace(R.id.Frame,targetFragment);
        transaction.commit();
    }

}
