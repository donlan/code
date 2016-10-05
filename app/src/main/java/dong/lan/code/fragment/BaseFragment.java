package dong.lan.code.fragment;

import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by 梁桂栋 on 2015/10/30 ： 下午5:42.
 * Email:       760625325@qqcom
 * GitHub:      github.com/donlan
 * description: code
 */
public class BaseFragment extends Fragment {

    public void Show(String s)
    {
        Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
    }
}
