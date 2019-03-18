package cn.kingtous.downloadit;

import android.Manifest;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import cn.kingtous.downloadit.Fragment.DataManagementFragment;
import cn.kingtous.downloadit.Fragment.DownloadFragment;
import cn.kingtous.downloadit.Fragment.SearchFragment;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {





    int WRITE_PERMISSION=1;

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

        frameLayout=findViewById(R.id.Frame);


        switchFragment(searchFragment);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        checkPermission();
    }


    private void checkPermission(){
        String[] permission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this,permission)){
            EasyPermissions.requestPermissions(this,"储存歌曲需要读写SD卡权限",WRITE_PERMISSION,permission);
        }

    }


    private void switchFragment(Fragment targetFragment){
        FragmentTransaction transaction=manager.beginTransaction();
        if (!targetFragment.isAdded()){
            transaction.add(R.id.Frame,targetFragment);
        }
        transaction.replace(R.id.Frame,targetFragment);
        transaction.commit();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @androidx.annotation.NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @androidx.annotation.NonNull List<String> perms) {
        if (requestCode==WRITE_PERMISSION){
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0]==-1){
            //只申请一个权限，所以直接设置为0
            List<String> list= Arrays.asList(permissions);
            onPermissionsDenied(requestCode,list);
        }
        else {
            List<String> list= Arrays.asList(permissions);
            onPermissionsGranted(requestCode,list);
        }
        return;
    }
}
