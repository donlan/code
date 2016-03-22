package dong.lan.code.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Dooze on 2015/10/31.
 */
public class MyViewPagerAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragments;
    public MyViewPagerAdapter(FragmentManager fm,ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments  = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
