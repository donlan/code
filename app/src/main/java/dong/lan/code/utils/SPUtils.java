package dong.lan.code.utils;

import android.content.SharedPreferences;

/**
 * 项目：code
 * 作者：梁桂栋
 * 日期： 2015/10/3  10:30.
 */
public class SPUtils {
    public static final String LOCK_PWD ="LOCK_PWD";
    public static final String IS_LOCK ="IS_LOCK";
    public static SharedPreferences sp;
    public static void init(SharedPreferences sharedPreferences)
    {
        sp = sharedPreferences;
    }

    public static void saveLock2SP(String pwd)
    {
        sp.edit().putString(LOCK_PWD,pwd).apply();
    }

    public static String LockPWD()
    {
        return sp.getString(LOCK_PWD,"");
    }

    public static void saveLockStatus(boolean isLock)
    {
        sp.edit().putBoolean(IS_LOCK,isLock).apply();
    }
    public static boolean getLockStatus()
    {
        return sp.getBoolean(IS_LOCK,false);
    }

    public static void setFirstUse(boolean use)
    {
        sp.edit().putBoolean("firstUse",use).apply();
    }
    public static boolean isFirstUse()
    {
        return sp.getBoolean("firstUse",true);
    }
}
