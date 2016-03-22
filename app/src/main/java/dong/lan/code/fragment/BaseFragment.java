package dong.lan.code.fragment;

import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by Dooze on 2015/10/30.
 */
public class BaseFragment extends Fragment {

    public void Show(String s)
    {
        Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
    }
}
