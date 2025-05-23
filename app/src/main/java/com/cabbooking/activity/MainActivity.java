package com.cabbooking.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.cabbooking.R;
import com.cabbooking.databinding.ActivityMainBinding;
import com.cabbooking.fragement.HomeFragment;
import com.cabbooking.utils.Common;
import com.cabbooking.utils.ConnectivityReceiver;
import com.cabbooking.utils.SessionManagment;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Common common;
    ActivityMainBinding binding;
    ActionBarDrawerToggle toggle;
    Toolbar mytoolbar;
    RelativeLayout lin_toolbar;
    LinearLayout lin_back_main,lin_only_back;
    SessionManagment sessionManagment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initView();
        allClick();

        loadFragment(new HomeFragment());
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_framelayout);
                if (fragment != null && fragment.getClass() != null) {
                    String frgmentName = fragment.getClass().getSimpleName();
                    if (frgmentName.contains("HomeFragment")) {
                        mytoolbar.setVisibility(View.VISIBLE);
                        lin_toolbar.setVisibility(View.VISIBLE);
                        lin_back_main.setVisibility(View.GONE);
                        lin_only_back.setVisibility(View.GONE);
                        toggle.syncState();
                        mytoolbar.setNavigationIcon(R.drawable.menu);
                    } else if(frgmentName.contains("DestinationFragment")) {
                        toggle.syncState();
                        mytoolbar.setNavigationIcon(null);
                        mytoolbar.setVisibility(View.VISIBLE);
                        lin_toolbar.setVisibility(View.GONE);
                        lin_back_main.setVisibility(View.VISIBLE);
                        lin_only_back.setVisibility(View.GONE);
                       // setMap(false);
                    }else {
                        mytoolbar.setVisibility(View.GONE);
                        toggle.syncState();
                        mytoolbar.setNavigationIcon(null);
                        lin_toolbar.setVisibility(View.GONE);
                        lin_back_main.setVisibility(View.GONE);
                        lin_only_back.setVisibility(View.VISIBLE);
                       // setMap(false);
                    }
                }
            }
        });

    }

    public void setTitle(String title){
        TextView tv_title= binding.appBar.findViewById(R.id.tv_title);
         tv_title.setText(title);
    }
    private void allClick() {
        binding.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showLogoutDialog();
            }
        });
        binding.appBar.findViewById(R.id.iv_backarrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        }); binding.appBar.findViewById(R.id.iv_back_only).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void showLogoutDialog()
    {
        Dialog dialog;

        dialog = new Dialog (MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView (R.layout.dialog_logout);
        Button btn_no,btn_yes;
        btn_yes=dialog.findViewById (R.id.btn_yes);
        btn_no=dialog.findViewById (R.id.btn_no);

        btn_no.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                dialog.dismiss ();
            }
        });

        btn_yes.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                dialog.dismiss ();
                sessionManagment.logout(MainActivity.this);
            }
        });
        dialog.setCanceledOnTouchOutside (false);
        dialog.show ();
    }
    public void loadFragment(Fragment fragment) {
        Bundle bundle=new Bundle();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().
                addToBackStack(null).add(R.id.main_framelayout,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
    }

    private void initView() {
        sessionManagment=new SessionManagment(MainActivity.this);
        common=new Common(MainActivity.this);
        common.subscribeToTopic();
        setSupportActionBar(binding.appBar.findViewById(R.id.mytoolbar));
         mytoolbar=binding.appBar.findViewById(R.id.mytoolbar);
         lin_toolbar=binding.appBar.findViewById(R.id.lin_toolbar);
         lin_back_main=binding.appBar.findViewById(R.id.lin_back_main);
        lin_only_back=binding.appBar.findViewById(R.id.lin_only_back);

        toggle = new ActionBarDrawerToggle(this, binding.drawer, mytoolbar, R.string.drawer_open, R.string.drawer_close);
        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        mytoolbar.setNavigationIcon(R.drawable.menu);
        mytoolbar.setNavigationOnClickListener(view -> {
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                binding.drawer.closeDrawer(GravityCompat.START);
            } else {
                binding.drawer.openDrawer(GravityCompat.START);
            }
        });
    }


}