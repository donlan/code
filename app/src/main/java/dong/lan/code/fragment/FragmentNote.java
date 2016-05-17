package dong.lan.code.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dong.lan.code.Interface.NoteItemClickListener;
import dong.lan.code.Interface.OnNoteChangeListener;
import dong.lan.code.MainActivity;
import dong.lan.code.R;
import dong.lan.code.activity.NoteAlarmActivity;
import dong.lan.code.adapter.MyPagerAdapter;
import dong.lan.code.adapter.NoteAdapter;
import dong.lan.code.bean.Note;
import dong.lan.code.db.DBManager;
import dong.lan.code.utils.AlarmUtils;
import dong.lan.code.utils.DividerItemDecoration;
import dong.lan.code.utils.TimeUtil;
import dong.lan.code.utils.ToastUtil;

/**
 * 项目：code
 * 作者：梁桂栋
 * 日期： 2015/10/31  12:43.
 */
public class FragmentNote extends BaseFragment implements View.OnClickListener, NoteItemClickListener, OnNoteChangeListener {

    public static final String NO_NOTE = "添加一个记事";
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private NoteAdapter rAdapter;
    private EditText searchText;

    private boolean resetAdapter;
    private boolean isSearch;
    private boolean isCopy = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragement_note_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        MainActivity.setOnNoteChangeListenner(this);
        recyclerView = (RecyclerView) getView().findViewById(R.id.note_recyclerView);
        TextView addNote = (TextView) getView().findViewById(R.id.note_add);
        searchText = (EditText) getView().findViewById(R.id.note_search_et);
        addNote.setOnClickListener(this);
        adapter = new NoteAdapter(getActivity(), null);
        rAdapter = new NoteAdapter(getActivity(), null);
        rAdapter.setNoteItemClickListener(new NoteItemClickListener() {
            @Override
            public void onNoteClick(Note note, int pos) {
                isCopy = true;
                detailDialog(note, pos);
            }

            @Override
            public void onNoteLongClick(Note note, int pos) {
                rAdapter.deleteNote(pos);
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!searchText.getText().toString().equals("")) {
                    List<Note> c = new ArrayList<>();
                    c = DBManager.getInstance().getSearchNotes(searchText.getText().toString());
                    if (c != null) {
                        rAdapter.delAddAll(c);
                        recyclerView.setAdapter(rAdapter);
                        isSearch = true;
                        resetAdapter = false;
                    } else {
                        Show("没有搜索结果");
                        resetAdapter = false;
                    }
                } else if (isSearch) {
                    // rAdapter.delAddAll(notes);
//                    recyclerView.setAdapter(rAdapter);
                    isSearch = false;
                    resetAdapter = true;
                }

                if (resetAdapter) {
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        List<Note> notes = DBManager.getInstance().getAllNote();
        if (notes == null || notes.isEmpty()) {
            adapter = new NoteAdapter(getActivity(), null);
            recyclerView.setAdapter(adapter);
            adapter.setNoteItemClickListener(this);
        } else {
            adapter = new NoteAdapter(getActivity(), notes);
            recyclerView.setAdapter(adapter);
            adapter.setNoteItemClickListener(this);
        }
        //设置布局管理
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    AlertDialog dialog;

    private void detailDialog(final Note n, final int pos) {
        MainActivity.LOCK_STATUS = false;
        Intent intent = new Intent(getActivity(), NoteAlarmActivity.class);
        intent.putExtra("NOTE", n);
        intent.putExtra("POS", pos);
        startActivityForResult(intent, 0);

    }

    private void addDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_note_dialog, null);
        final EditText text = (EditText) view.findViewById(R.id.add_note_des);
        final EditText type = (EditText) view.findViewById(R.id.add_note_type);
        TextView done = (TextView) view.findViewById(R.id.add_note_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().toString().equals("")) {
                    Show("没有笔记!!!");
                    return;
                }
                String typeText = (type.getText().toString());
                if (typeText.length() > 10) {
                    ToastUtil.Show(getActivity(), "只能是10个汉字以内的自定义类型");
                    return;
                }
                Note n = new Note();
                if (typeText.equals(""))
                    n.setType("记事");
                else {
                    n.setType(typeText);
                }
                n.setNote(text.getText().toString());
                n.setTime(TimeUtil.dateToString(new Date(), TimeUtil.FORMAT_DATA_TIME_SECOND_1));
                adapter.addNote(n);
                recyclerView.scrollToPosition(0);
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.note_add:
                addDialog();
                break;
        }
    }

    @Override
    public void onNoteClick(Note note, int pos) {
        isCopy = false;
        if (note == null) {
            addDialog();
        } else {
            detailDialog(note, pos);
        }
    }

    @Override
    public void onNoteLongClick(Note note, final int pos) {
        dialog = new AlertDialog.Builder(getActivity()).setTitle("选择你需要的操作")
                .setPositiveButton("闹钟提醒", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        pickerTime(adapter.getNoteAt(pos));
                    }
                })
                .setNegativeButton("删除记事", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.deleteNote(pos);
                    }
                }).show();
    }

    public void pickerTime(final Note info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_picker_time, null);
        final ViewPager pager = (ViewPager) view.findViewById(R.id.datePager);
        final RadioButton dateText = (RadioButton) view.findViewById(R.id.bar_date);
        final RadioButton timeText = (RadioButton) view.findViewById(R.id.bar_time);
        dateText.setTextColor(Color.BLACK);
        timeText.setTextColor(Color.WHITE);
        View dateView = LayoutInflater.from(getActivity()).inflate(R.layout.view_date_picker, null);
        View timeView = LayoutInflater.from(getActivity()).inflate(R.layout.view_time_picker, null);
        final DatePicker datePicker = (DatePicker) dateView.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) timeView.findViewById(R.id.timePicker);
        List<View> views = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 21) {
            datePicker.setMinDate(System.currentTimeMillis());
        }
        views.add(dateView);
        views.add(timeView);
        pager.setAdapter(new MyPagerAdapter(views));
        pager.setCurrentItem(0);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    dateText.setChecked(true);
                    dateText.setTextColor(Color.BLACK);
                    timeText.setTextColor(Color.WHITE);
                } else {
                    timeText.setChecked(true);
                    dateText.setTextColor(Color.WHITE);
                    timeText.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateText.setChecked(true);
                dateText.setTextColor(Color.BLACK);
                timeText.setTextColor(Color.WHITE);
                pager.setCurrentItem(0);
            }
        });
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeText.setChecked(true);
                dateText.setTextColor(Color.WHITE);
                timeText.setTextColor(Color.BLACK);
                pager.setCurrentItem(1);
            }
        });
        Button ok = (Button) view.findViewById(R.id.pick_ok);
        datePicker.setSpinnersShown(false);
        timePicker.setIs24HourView(true);
        final Calendar calendar = Calendar.getInstance();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                long triggerTime = calendar.getTimeInMillis() - System.currentTimeMillis();
                if (triggerTime <= 0) {
                    Show("提醒时间不能设置在当前时间之前");
                    return;
                }
                AlarmUtils.addAlarm(getActivity(), triggerTime, info);
                dialog.dismiss();
            }
        });

        builder.setView(view);
        dialog = builder.show();
    }

    @Override
    public void onNoteChange(int Tag) {
        if (Tag == 1) {
            searchText.setText("");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == 1) {

            Note note = data.getParcelableExtra("NOTE");
            int pos = data.getIntExtra("POS", -1);
            if (pos != -1 && note != null) {
                if (isCopy)
                    rAdapter.updateNote(note.getTime(), note, pos);
                else
                    adapter.updateNote(note.getTime(), note, pos);
            }
        }
    }
}
