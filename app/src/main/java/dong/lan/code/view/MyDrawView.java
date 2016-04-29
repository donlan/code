package dong.lan.code.view;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import dong.lan.code.BuildConfig;

/**
 * 项目：code
 * 作者：梁桂栋
 * 日期： 4/24/2016  23:18.
 */
public class MyDrawView extends DrawerLayout {
    public MyDrawView(Context context) {
        this(context,null);
    }

    public MyDrawView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        if (BuildConfig.DEBUG) Log.d("MyDrawView", context.getPackageCodePath());
    }

    public MyDrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (BuildConfig.DEBUG) Log.d("MyDrawView", context.getPackageCodePath());
    }

    float x;
    float y;

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (BuildConfig.DEBUG) Log.d("MyDrawView", "onGenericMotionEvent:" + event.getAction());
        return super.onGenericMotionEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (BuildConfig.DEBUG) Log.d("MyDrawView", "e.getAction():" + e.getAction());
        switch (e.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x = e.getRawX();
                y = e.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                x = x - e.getRawX();
                y = y - e.getRawY();
                if (BuildConfig.DEBUG) Log.d("MyDrawView", "x:" + x+"  y:"+y);
                if(x > 15 && Math.abs(y)<10)
                {
                    return true;
                }
        }
        return super.onInterceptTouchEvent(e);
    }

    public void open()
    {
        this.openDrawer(GravityCompat.START);
    }
}
