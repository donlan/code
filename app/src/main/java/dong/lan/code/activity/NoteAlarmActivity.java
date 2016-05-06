package dong.lan.code.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

import dong.lan.code.MainActivity;
import dong.lan.code.R;
import dong.lan.code.bean.Note;
import dong.lan.code.utils.TimeUtil;

/**
 * 项目：code
 * 作者：梁桂栋
 * 日期： 5/6/2016  12:04.
 */
public class NoteAlarmActivity extends BaseActivity {

    private Note note;
    private int POS;
    private EditText text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_alarm);
        if (getIntent().hasExtra("NOTE")) {
            note = getIntent().getParcelableExtra("NOTE");
        }
        POS = getIntent().getIntExtra("POS", -1);
        if (note == null) {
            Show("没有记事信息！！！");
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.alarmBar);
        toolbar.setTitle("详情");
        text = (EditText) findViewById(R.id.dNote_des);
        TextView type = (TextView) findViewById(R.id.dNote_type);
        TextView time = (TextView) findViewById(R.id.dNote_time);
        Button done = (Button) findViewById(R.id.reedit_button);

        text.setText(note.getNote());
        type.setText(note.getType());
        time.setText(note.getTime());
        if (POS != -1) {
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!text.getText().toString().equals(note.getNote())) {
                        note.setTime(TimeUtil.dateToString(new Date(), TimeUtil.FORMAT_DATA_TIME_SECOND_1));
                        note.setNote(text.getText().toString());
                        Intent intent = new Intent(NoteAlarmActivity.this, MainActivity.class);
                        intent.putExtra("NOTE", note);
                        intent.putExtra("POS", POS);
                        setResult(1, intent);
                        finish();
                    }
                }
            });
        } else {
//            long vib[] = new long[]{1000, 500, 1000, 500,1000, 500, 1000, 500};
//            final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//            v.vibrate(vib, 1);
            toolbar.setTitle("提醒");
            done.setVisibility(View.GONE);
//            text.post(new Runnable() {
//                @Override
//                public void run() {
//
//                    final Intent alarmIntent = getIntent().getParcelableExtra("INTENT");
//                    new AlertDialog.Builder(NoteAlarmActivity.this).setTitle("提醒")
//                            .setMessage(note.getNote())
//                            .setPositiveButton("关闭提醒", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    AlarmUtils.cancelAlarm(NoteAlarmActivity.this, alarmIntent);
//                                    v.cancel();
//                                }
//                            }).show();
//                }
//            });


        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.LOCK_STATUS = true;
    }
}
