package com.cabbooking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabbooking.R;
import com.cabbooking.databinding.ActivityMainBinding;
import com.cabbooking.databinding.ActivityMapBinding;
import com.cabbooking.fragement.DestinationFragment;
import com.cabbooking.fragement.HomeFragment;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.SessionManagment;

public class MapActivity extends AppCompatActivity {
    Common common;
    ActivityMapBinding binding;
    SessionManagment sessionManagment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        initView();
        allClick();
        loadFragment(new DestinationFragment());
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_framelayout);
                if (fragment != null && fragment.getClass() != null) {
                    String frgmentName = fragment.getClass().getSimpleName();
                     if(frgmentName.contains("DestinationFragment")) {
                        binding.mytoolbar.setNavigationIcon(null);
                        binding.mytoolbar.setVisibility(View.VISIBLE);
                        binding.linBackMain.setVisibility(View.VISIBLE);
                        binding.linOnlyBack.setVisibility(View.GONE);
                        // setMap(false);
                         binding.map.setVisibility(View.GONE);
                           }else {
                         common.setMap(false,true,150,binding.map.findViewById(R.id.iv_map),binding.map.findViewById(R.id.lin_search));
                         binding.mytoolbar.setVisibility(View.GONE);
                         binding.mytoolbar.setNavigationIcon(null);
                         binding.linBackMain.setVisibility(View.GONE);
                         binding.linOnlyBack.setVisibility(View.VISIBLE);
                        // setMap(false);
                         binding.map.setVisibility(View.VISIBLE);
                     }
                }
            }
        });

    }

    public void setTitle(String title){
         binding.tvTitle.setText(title);
    }
    private void allClick() {
       
        binding.ivBackarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_framelayout);
                if (fragment != null && fragment.getClass() != null) {
                    String frgmentName = fragment.getClass().getSimpleName();
                    if (frgmentName.contains("DestinationFragment")) {
                        Intent intent=new Intent(MapActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        onBackPressed();
                    }
                }

            }
        }); 
        binding.ivBackOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_framelayout);
        if (fragment != null && fragment.getClass() != null) {
            String frgmentName = fragment.getClass().getSimpleName();
            if (frgmentName.contains("DestinationFragment")) {
                Intent intent=new Intent(MapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                super.onBackPressed();
            }
        }
    }

    public void loadFragment(Fragment fragment) {

        Bundle bundle=new Bundle();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().
                addToBackStack(null).add(R.id.main_framelayout,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    private void initView() {
        sessionManagment=new SessionManagment(MapActivity.this);
        common=new Common(MapActivity.this);

    }
}