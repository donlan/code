package dong.lan.code.activity;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * 项目：code
 * 作者：梁桂栋
 * 日期： 3/16/2016  18:59.
 */
public class BaseMainActivity extends AppCompatActivity {
    public void Show(String s)
    {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
}
