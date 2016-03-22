package dong.lan.code.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dong.lan.code.Interface.NoteItemClickListener;
import dong.lan.code.Interface.OnNoteChangeListener;
import dong.lan.code.MainActivity;
import dong.lan.code.R;
import dong.lan.code.adapter.NoteAdapter;
import dong.lan.code.bean.Note;
import dong.lan.code.db.DBManeger;
import dong.lan.code.utils.DividerItemDecoration;
import dong.lan.code.utils.TimeUtil;

/**
 * 项目：code
 * 作者：梁桂栋
 * 日期： 2015/10/31  12:43.
 */
public class FragmentNote extends BaseFragment implements View.OnClickListener, NoteItemClickListener,OnNoteChangeListener {

    public static final String NO_NOTE = "添加一个记事";
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private NoteAdapter rAdapter;
    private TextView addNote;
    private EditText searchText;

    private List<Note> notes = new ArrayList<>();
    private boolean resetAdapter;
    private boolean isSearch;
    private boolean isCopy=false;


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
        addNote = (TextView) getView().findViewById(R.id.note_add);
        searchText = (EditText) getView().findViewById(R.id.note_search_et);
        addNote.setOnClickListener(this);
        adapter = new NoteAdapter(getActivity(),null);
        rAdapter = new NoteAdapter(getActivity(), null);
        rAdapter.setNoteItemClickListener(new NoteItemClickListener() {
            @Override
            public void onNoteClick(Note note, int pos) {
                isCopy=true;
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
                    c = DBManeger.getInstance().getSeachNotes(searchText.getText().toString());
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
        notes = DBManeger.getInstance().getAllNote();
        if (notes == null || notes.isEmpty()) {
            Show("没有记事笔记 !!!");
            adapter = new NoteAdapter(getActivity(), null);
            recyclerView.setAdapter(adapter);
            adapter.setNoteItemClickListener(this);
        } else {
            adapter = new NoteAdapter(getActivity(), notes);
            recyclerView.setAdapter(adapter);
            //设置item的点击监听
            adapter.setNoteItemClickListener(this);
        }
        //设置布局管理
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        //设置分割线divider
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        //设置item的添加删除的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    AlertDialog dialog;

    private void detailDialog(final Note n, final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.detail_note_dialog, null);
        final EditText text = (EditText) view.findViewById(R.id.dNote_des);
        TextView type = (TextView) view.findViewById(R.id.dNote_type);
        TextView time = (TextView) view.findViewById(R.id.dNote_time);
        Button done = (Button) view.findViewById(R.id.reedit_button);
        text.setText(n.getNote());
        text.setSelection(n.getNote().length());
        time.setText(n.getTime());
        type.setText(n.getType());
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text.getText().toString().equals(n.getNote())) {
                    Note note = new Note("记事", TimeUtil.dateToString(new Date(), TimeUtil.FORMAT_DATA_TIME_SECOND_1), text.getText().toString());
                    if(isCopy)
                    rAdapter.updateNote(n.getTime(), note, pos);
                    else
                        adapter.updateNote(n.getTime(), note, pos);
                }
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.show();
    }

    private void addDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_note_dialog, null);
        final EditText text = (EditText) view.findViewById(R.id.add_note_des);
        TextView done = (TextView) view.findViewById(R.id.add_note_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().toString().equals("")) {
                    Show("没有笔记!!!");
                    return;
                }
                Note n = new Note();
                n.setType("记事");
                n.setNote(text.getText().toString());
                n.setTime(TimeUtil.dateToString(new Date(), TimeUtil.FORMAT_DATA_TIME_SECOND_1));
                adapter.addNote(n);
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
        isCopy=false;
        if (note == null) {
            addDialog();
        } else
            detailDialog(note, pos);
    }

    @Override
    public void onNoteLongClick(Note note, int pos) {
        adapter.deleteNote(pos);
    }


    @Override
    public void onNoteChange(int Tag) {
        if(Tag ==1)
        {
            searchText.setText("");
        }
    }
}
