package dong.lan.code.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * 项目：  code
 * 作者：  梁桂栋
 * 日期：  2015/10/31  20:56.
 * Email: 760625325@qq.com
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
