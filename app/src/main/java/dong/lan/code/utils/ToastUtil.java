package dong.lan.code.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 项目：code
 * 作者：梁桂栋
 * 日期： 4/29/2016  05:33.
 */
public class ToastUtil {

    public static void Show(Context context,String str)
    {
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
    }
}
