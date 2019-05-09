package util;

import com.izzat.syamil.tradecrate.Home.Home_Fragment;
import com.izzat.syamil.tradecrate.Inventory.Inventory_Fragment;
import com.izzat.syamil.tradecrate.Message.Message_Fragment;
import com.izzat.syamil.tradecrate.Trade_Inbox.TradeInboxFragment;

import java.util.ArrayList;

import javax.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabAdapter extends FragmentStatePagerAdapter {

    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private final ArrayList<String> fragmentTitleList = new ArrayList<>();

    public TabAdapter(FragmentManager fm){

        super(fm);
        fragmentArrayList.add(new Home_Fragment());
        fragmentArrayList.add(new Inventory_Fragment());
        fragmentArrayList.add(new TradeInboxFragment());
        fragmentArrayList.add(new Message_Fragment());

        for(int i = 0; i < fragmentArrayList.size(); i++){
            fragmentTitleList.add("");
        }
    }

    @Override
    public Fragment getItem(int position){

       return fragmentArrayList.get(position);
    }

    public void addFragment(Fragment fragment, String title){

        fragmentArrayList.add(fragment);
        fragmentTitleList.add(title);

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position){

        return fragmentTitleList.get(position);

    }

    @Override
    public int getCount(){

        return fragmentArrayList.size();

    }
}
