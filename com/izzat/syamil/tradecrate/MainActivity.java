package com.izzat.syamil.tradecrate;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.izzat.syamil.tradecrate.Inventory.NewInventory_Activity;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import model.InventoryItem;
import model.Tradeable;
import model.Trader;
import util.TabAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener
                                                                , ViewPager.OnPageChangeListener , View.OnClickListener{

    private BottomAppBar bottomAppBar;
    private ViewPager viewPager;
    private TabAdapter tabAdapter;
    private TabLayout tb_layout;
    private MenuItem filter, search, new_message, add_inventory;
    private FloatingActionButton newItemFAB, newMessage;
    private ArrayList<InventoryItem> myInventory;
    private ArrayList<Tradeable> currentTradeables;
    private Trader currentTrader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_drawer);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentTrader = new Trader(currentUser.getUid(),
                                    currentUser.getDisplayName(),
                                    currentUser.getPhotoUrl().toString());

        bottomAppBar = findViewById(R.id.bottomAppBar2);
        bottomAppBar.replaceMenu(R.menu.main_menu);
        tb_layout = findViewById(R.id.tabLayout);

        viewPager = findViewById(R.id.view_pager);

        CircleImageView image = findViewById(R.id.profile_pic);
        Picasso.with(this)
                .load(currentTrader.getImageUrl())
                .into(image);

        NavigationView navigationView = findViewById(R.id.main_naView);
        navigationView.setNavigationItemSelectedListener(this);

        TextView user_name_in_navi = navigationView.getHeaderView(0).findViewById(R.id.profile_name_drawer);
        user_name_in_navi.setText(currentTrader.getTraderName());

        CircleImageView picture_in_navi = navigationView.getHeaderView(0).findViewById(R.id.profile_pic_drawer);
        Picasso.with(this)
                .load(currentTrader.getImageUrl())
                .into(picture_in_navi);


        tabAdapter = new TabAdapter(getSupportFragmentManager());

        viewPager.setAdapter(tabAdapter);
        tb_layout.setupWithViewPager(viewPager);
        tb_layout.getTabAt(0).setIcon(R.drawable.ic_home_black_24dp);
        tb_layout.getTabAt(1).setIcon(R.drawable.ic_dashboard_black_24dp);
        tb_layout.getTabAt(2).setIcon(R.drawable.ic_assignment_black_24dp);
        tb_layout.getTabAt(3).setIcon(R.drawable.ic_mail_black_24dp);
        viewPager.addOnPageChangeListener(this);

        newItemFAB = findViewById(R.id.addItemFAB);
        newItemFAB.setOnClickListener(this);

        newMessage = findViewById(R.id.newMessageFAB);
        newMessage.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu );
        //add_inventory = menu.findItem(R.id.add_inventory);
        filter = menu.findItem(R.id.feed_filter);
        //new_message = menu.findItem(R.id.new_message);
        search = menu.findItem(R.id.search_stuff);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.logging_out:

                AuthUI.getInstance().signOut(MainActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        Intent i = new Intent(MainActivity.this, SignInActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                });
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.addItemFAB:
                Intent i = new Intent(this, NewInventory_Activity.class);
                startActivity(i);
        }

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        tabAdapter.getItem(tab.getPosition());
        viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        //tb_layout.set
        if(position == 3){

            hideFab(newItemFAB);
            revealFab(newMessage);

        }   else  if(newMessage.getVisibility() == View.VISIBLE)   {

                hideFab(newMessage);
                revealFab(newItemFAB);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void revealFab(FloatingActionButton fab){
        int cx = fab.getWidth()/2;
        int cy = fab.getHeight()/2;
        float finalRadius = (float)Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(fab, cx, cy, 0, finalRadius);
        fab.setVisibility(View.VISIBLE);
        anim.start();
    }

    private void hideFab(final FloatingActionButton fab){

        int cx = fab.getWidth()/2;
        int cy = fab.getHeight()/2;
        float finalRadius = (float)Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(fab, cx, cy, 0, finalRadius);
       /* anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fab.setVisibility(View.INVISIBLE);
            }
        });*/
        fab.setVisibility(View.INVISIBLE);
        anim.start();
    }

    public void setAllTradeables(ArrayList<Tradeable> tradeabls){
            currentTradeables = tradeabls;
    }


    public void setMyInventory(ArrayList<InventoryItem> myInvtry){
        this.myInventory = myInvtry;
    }

    public ArrayList<InventoryItem> getMyInventories(){
        return myInventory;
    }
}
