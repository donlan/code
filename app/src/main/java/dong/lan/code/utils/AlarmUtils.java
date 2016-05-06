package dong.lan.code.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import dong.lan.code.bean.Note;
import dong.lan.code.receiver.AlarmReceiver;

/**
 * 项目：code
 * 作者：梁桂栋
 * 日期： 5/6/2016  09:40.
 */
public class AlarmUtils {
    
    public static void addAlarm( Context context,  long triggerTime,  String info){
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("NOTE",info);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                0,intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pendingIntent);
    }
    public static void addAlarm( Context context,  long triggerTime,  Note info){
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("NOTE",info);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                0,intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pendingIntent);
    }

    public static void cancelAlarm(Context context,Intent intent){
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendIntent);
    }
}
