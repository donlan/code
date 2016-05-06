package dong.lan.code;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dong.lan.code.Interface.CodeDataListener;
import dong.lan.code.Interface.OnNoteChangeListener;
import dong.lan.code.Interface.onCodeLoadListener;
import dong.lan.code.activity.BaseMainActivity;
import dong.lan.code.adapter.MyViewPagerAdapter;
import dong.lan.code.bean.Code;
import dong.lan.code.db.DBManeger;
import dong.lan.code.fragment.FragmentCode;
import dong.lan.code.fragment.FragmentNote;
import dong.lan.code.utils.FileUtils;
import dong.lan.code.utils.SPUtils;
import dong.lan.code.view.LockView;
import dong.lan.code.view.MyDrawView;

public class MainActivity extends BaseMainActivity implements View.OnClickListener, LockView.LockPaintFinish, CodeDataListener {


    public static boolean LOCK_STATUS = true;
    private String PWD = "";
    private int lock = 0;
    private boolean reset = false;
    private int loop = 0;
    private MyHandler handler;

    private ToggleButton lockToggle;
    private TextView dataFrom;
    private TextView dataTo;
    private TextView lockHint;
    private RadioButton radioCode;
    private RadioButton radioNote;
    private FrameLayout lockLayout;
    private List<Code> codes = new ArrayList<>();
    private List<Code> codes1;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ViewPager viewPager;

    @Override
    public void onCodeDataGet(int Tag, List<Code> codes) {
        if (Tag == 1) {
            this.codes = codes;
        } else if (Tag == 0) {
            FileUtils.dataFromSD(MainActivity.this, dataFrom, codes1, codeLoadListener, codeFile, handler);
        }
    }


    static onCodeLoadListener codeLoadListener;

    public static void setCodeLoadListener(onCodeLoadListener loadListener) {
        codeLoadListener = loadListener;
    }

    static OnNoteChangeListener noteChangeListener;

    public static void setOnNoteChangeListenner(OnNoteChangeListener changeListener) {
        noteChangeListener = changeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBManeger.onInit(this);
        handler = new MyHandler();
        SharedPreferences preferences = this.getSharedPreferences("CODE_SP", MODE_PRIVATE);

        SPUtils.init(preferences);

        initView();

    }


    File sdRoot = Environment.getExternalStorageDirectory();
    File codeFile = new File(sdRoot, "myCode.code");


    private void initView(){
        viewPager = (ViewPager) findViewById(R.id.viewPaper);
        lockHint = (TextView) findViewById(R.id.lockHint);
        dataFrom = (TextView) findViewById(R.id.dataFromSD);
        MyDrawView drawerLayout = (MyDrawView) findViewById(R.id.drawerLayout);
        LockView lockView = (LockView) findViewById(R.id.lockView);
        lockToggle = (ToggleButton) findViewById(R.id.lockSwitcher);
        lockToggle.setOnClickListener(this);
        lockToggle.setChecked(SPUtils.getLockStatus());
        lockView.setOnLockPaintFinish(this);
        dataFrom.setOnClickListener(this);
        dataTo = (TextView) findViewById(R.id.dataToSD);
        dataTo.setOnClickListener(this);
        findViewById(R.id.lockSwitcher).setOnClickListener(this);
        findViewById(R.id.reset_pass).setOnClickListener(this);
        lockLayout = (FrameLayout) findViewById(R.id.lockLayout);
        radioCode = (RadioButton) findViewById(R.id.bar_code);
        radioNote = (RadioButton) findViewById(R.id.bar_note);
        radioNote.setOnClickListener(this);
        radioCode.setOnClickListener(this);
        radioCode.setTextColor(Color.BLACK);
        radioNote.setTextColor(Color.WHITE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
        FragmentCode fragmentCode = new FragmentCode();
        fragmentCode.setCodeDataListener(this);
        FragmentNote fragmentNote = new FragmentNote();
        fragments.add(fragmentCode);
        fragments.add(fragmentNote);
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        radioCode.setChecked(true);
                        radioCode.setTextColor(Color.BLACK);
                        radioNote.setTextColor(Color.WHITE);
                        break;
                    case 1:
                        radioNote.setChecked(true);
                        radioCode.setTextColor(Color.WHITE);
                        radioNote.setTextColor(Color.BLACK);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (SPUtils.isFirstUse()) {
            SPUtils.setFirstUse(false);
            new AlertDialog.Builder(this).setTitle("欢迎使用密码管家")
                    .setIcon(getResources().getDrawable(R.mipmap.logo_60))
                    .setMessage(R.string.help_tip)
                    .setPositiveButton("开始使用", null).show();
        }

        if (SPUtils.getLockStatus() && !SPUtils.LockPWD().equals(""))
            lockLayout.setVisibility(View.VISIBLE);
        else
            lockLayout.setVisibility(View.GONE);
    }

    public void HelpClick(View v) {
        new AlertDialog.Builder(this).setTitle("使用帮助").setMessage(R.string.help).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_code:
                viewPager.setCurrentItem(0);
                radioCode.setTextColor(Color.BLACK);
                radioNote.setTextColor(Color.WHITE);
                break;
            case R.id.bar_note:
                viewPager.setCurrentItem(1);
                radioCode.setTextColor(Color.WHITE);
                radioNote.setTextColor(Color.BLACK);
                break;
            case R.id.dataFromSD:
                FileUtils.dataFromSD(MainActivity.this, dataFrom, codes1, codeLoadListener, codeFile, handler);
                break;
            case R.id.dataToSD:
                FileUtils.dataToSD(MainActivity.this, codeFile, dataTo);
                break;
            case R.id.reset_pass:
                reset = true;
                lockLayout.setVisibility(View.VISIBLE);
                if (SPUtils.LockPWD().equals("") && loop == 0) {
                    lockHint.setText("还没有设置密码，请设置你的密码");
                } else if (!SPUtils.LockPWD().equals("") && loop == 0) {
                    lockHint.setText("请输入你的原密码");
                    loop = -1;
                }
                break;
            case R.id.lockSwitcher:
                if (!lockToggle.isChecked()) {
                    if (SPUtils.LockPWD().equals("")) {
                        lockLayout.setVisibility(View.VISIBLE);
                        lockHint.setText("还没有设置密码，请设置你的密码");
                        lock = 1;
                    } else {
                        lockLayout.setVisibility(View.VISIBLE);
                        lock = 2;
                        lockHint.setText("输入已设置的密码");
                    }
                } else {
                    if (SPUtils.LockPWD().equals("")) {
                        lockLayout.setVisibility(View.VISIBLE);
                        lockHint.setText("还没有设置密码，请设置你的密码");
                        lock = 1;
                    } else {
                        if (!SPUtils.getLockStatus()) {
                            SPUtils.saveLockStatus(true);
                            Show("已开启应用锁");
                        }
                    }
                }
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (LOCK_STATUS && SPUtils.getLockStatus() && !SPUtils.LockPWD().equals(""))
            lockLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLockPaintFinish(String pwd) {
        if (pwd.equals("ERROR")) {
            lockHint.setText("请重新解锁");
            return;
        }
        if (!reset && pwd.equals(SPUtils.LockPWD())) {
            lockHint.setText("");
            lockLayout.setVisibility(View.GONE);
        } else {
            lockHint.setText("密码不对 ^_^ ");
            return;
        }
        if (lock == 1) {
            reset = true;
        } else if (lock == 2 && SPUtils.LockPWD().equals(pwd)) {
            lockLayout.setVisibility(View.GONE);
            SPUtils.saveLockStatus(false);
            lockHint.setText("");
            lock = 0;
            loop = 0;
        }

        if (reset) {
            if (loop == -1) {
                if (pwd.equals(SPUtils.LockPWD())) {
                    lockHint.setText("设置你的新密码");
                    loop++;
                } else {
                    lockHint.setText("与已设置密码不一致");
                }
            } else if (loop == 0) {
                loop++;
                PWD = pwd;
                lockHint.setText("再次输入密码");
            } else if (loop == 1) {
                if (PWD.equals(pwd)) {
                    SPUtils.saveLockStatus(true);
                    SPUtils.saveLock2SP(pwd);
                    lockLayout.setVisibility(View.GONE);
                    reset = false;
                    loop = 0;
                    lock = 0;
                    Show("密码已重置");
                    lockHint.setText("");
                } else {
                    lockHint.setText("两次密码不一致");
                }
            }

        }

    }


    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FileUtils.FROM:
                    if (codes1 == null || codes1.isEmpty()) {
                        codeLoadListener.onCodeChange(4, null);
                        Show("没有数据导入");
                    } else {
                        codes = new ArrayList<>(codes1);
                        codeLoadListener.onCodeChange(1, codes);
                        Show("导入完成");
                    }
                    dataFrom.setEnabled(true);
                    break;
                case FileUtils.TO:
                    dataTo.setEnabled(true);
                    break;
                case FileUtils.FROM_START:
                    Show("开始导入");
                    break;
                case FileUtils.FROM_BAD:
                    Show("导入失败");
                    codeLoadListener.onCodeChange(4, null);
                case FileUtils.FROM_DONE:
                    dataFrom.setEnabled(true);
                    codeLoadListener.onCodeChange(4, null);
                    break;

                case FileUtils.TO_DONE:
                    dataTo.setEnabled(true);
                    codeLoadListener.onCodeChange(4, null);
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private static long firstTime;

    @Override
    public void onBackPressed() {
        if (firstTime + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(false);
            super.onBackPressed();
        } else {
            Show("再按一次退出程序");
        }
        firstTime = System.currentTimeMillis();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            codeLoadListener.onCodeChange(2, null);
            noteChangeListener.onNoteChange(1);
        }
        return super.onKeyDown(keyCode, event);
    }

}
