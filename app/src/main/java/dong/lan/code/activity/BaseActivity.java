package dong.lan.code.activity;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Dooze on 2015/9/20.
 */
public class BaseActivity extends AppCompatActivity {

    public void Show(String s)
    {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
}
