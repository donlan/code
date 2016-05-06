package dong.lan.code.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import dong.lan.code.R;
import dong.lan.code.activity.NoteAlarmActivity;
import dong.lan.code.bean.Note;

/**
 * 项目：code
 * 作者：梁桂栋
 * 日期： 5/6/2016  09:26.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Note note = intent.getParcelableExtra("NOTE");
        Intent alertIntent = new Intent(context, NoteAlarmActivity.class);
        alertIntent.putExtra("NOTE",note);
        alertIntent.putExtra("INTENT",intent);
        alertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        context,
                        note.getTime().hashCode(),
                        alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.logo_60)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo))
                .setOnlyAlertOnce(true)
                .setContentTitle("有新的"+note.getType())
                .setContentText(note.getNote());
        mBuilder.setSound(RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE));
        mBuilder.setContentIntent(notifyPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setVibrate(new long[]{1000, 500, 1000,500,1});
        mNotificationManager.notify(note.getTime().hashCode(), mBuilder.build());
    }
}
