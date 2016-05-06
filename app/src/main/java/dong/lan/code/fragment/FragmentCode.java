package dong.lan.code.fragment;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dong.lan.code.Interface.CodeDataListener;
import dong.lan.code.Interface.onCodeLoadListener;
import dong.lan.code.MainActivity;
import dong.lan.code.R;
import dong.lan.code.adapter.MainRecycleAdapter;
import dong.lan.code.bean.Code;
import dong.lan.code.db.DBManeger;
import dong.lan.code.utils.DividerItemDecoration;
import dong.lan.code.utils.MyItemTouchHelper;

/**
 * 项目：code
 * 作者：梁桂栋
 * 日期： 2015/10/30  02:32.
 */
public class FragmentCode extends BaseFragment implements View.OnClickListener, onCodeLoadListener {

    private RecyclerView recyclerView;
    private List<Code> codes = new ArrayList<>();
    private MainRecycleAdapter adapter;
    private MainRecycleAdapter rAdapter;
    private EditText searchText;
    private LinearLayout loadingLayout;
    private SwipeRefreshLayout refreshLayout;
    private boolean isSearch = false;
    private boolean resetAdapter = true;
    private ClipboardManager clipboardManager = null;

    CodeDataListener codeDataListener;
    private MyItemTouchHelper callback;

    public void setCodeDataListener(CodeDataListener listenner) {
        codeDataListener = listenner;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragement_pw_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.setCodeLoadListener(this);
        clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        initView();
    }

    private void initView() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new MainRecycleAdapter(getActivity(), null);
        recyclerView.setAdapter(adapter);
        final TextView addCode = (TextView) getView().findViewById(R.id.add_code);
        searchText = (EditText) getView().findViewById(R.id.search_et);
        loadingLayout = (LinearLayout) getView().findViewById(R.id.loadingLayout);
        addCode.setOnClickListener(this);
        refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.code_swipe);
        refreshLayout.setColorSchemeResources(R.color.md_green_400, R.color.md_blue_400, R.color.md_yellow_400,
                R.color.md_red_400
        );
        refreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(0x123, 1000);
                if (adapter != null)
                    adapter.refresh();
            }
        });
        rAdapter = new MainRecycleAdapter(getActivity(), null);
        rAdapter.setOnItemClickListener(new MainRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                DetailCode(rAdapter.getCodeAt(pos), pos);
            }

            @Override
            public void onItemLongClick(View view, int pos, int type) {
                rAdapter.deleteCode(pos);
            }
        });


        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
            }

            @Override
            public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {

            }

            @Override
            public void afterTextChanged(Editable p1) {
                if (!searchText.getText().toString().equals("")) {
                    List<Code> c ;
                    c = DBManeger.getInstance().getSeachCodes(searchText.getText().toString());
                    if (c != null) {
                        rAdapter.delAddAll(c);
                        recyclerView.setAdapter(rAdapter);
                        rAdapter.notifyDataSetChanged();
                        isSearch = true;
                        resetAdapter = false;
                        callback.setTouchListener(rAdapter);
                    } else {
                        Show("没有搜索结果");
                        resetAdapter = false;
                    }
                } else if (isSearch) {
                    isSearch = false;
                    resetAdapter = true;
                }

                if (resetAdapter) {
                    //设置item的点击监听
                    recyclerView.setAdapter(adapter);
                    callback.setTouchListener(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        codes = DBManeger.getInstance().getAllCodes();
        if (codes == null || codes.isEmpty()) {
            codes = new ArrayList<>();
            codeDataListener.onCodeDataGet(0, null);
        } else {
            adapter = new MainRecycleAdapter(getActivity(), codes);
            recyclerView.setAdapter(adapter);

            //设置item的点击监听
            adapter.setOnItemClickListener(new MainRecycleAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, final int pos) {
                    DetailCode(adapter.getCodeAt(pos), pos);
                    adapter.updateCount(pos, true);
                }

                @Override
                public void onItemLongClick(View view,final int pos, int type) {
                    if (type == 0)
                        new AlertDialog.Builder(getActivity()).setMessage("确定删除吗？")
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.deleteCode(pos);
                            }
                        }).show();
                    else {
                        ClipData clipData = ClipData.newPlainText("text", adapter.getCodeAt(pos).getWord());
                        clipboardManager.setPrimaryClip(clipData);
                        Show("已复制到剪切板");
                    }
                }
            });
        }
        //设置布局管理
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        //设置分割线divider
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        //设置item的添加删除的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //拖动排序 与删除， 绑定到recyclerView
        callback = new MyItemTouchHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);


    }

    private void DetailCode(final Code code, final int pos) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.detail_code_dialog,null);
        final EditText name = (EditText) view.findViewById(R.id.detail_code_name);
        final EditText detailCode = (EditText) view.findViewById(R.id.detail_code_pwd);
        final EditText other = (EditText) view.findViewById(R.id.detail_code_other);
        TextView done = (TextView) view.findViewById(R.id.detail_code_done);
        final String oldCode = code.getWord();
        final String oldName = code.getDes();
        name.setText(oldName);
        detailCode.setText(oldCode);
        other.setText(code.getOther());
        builder.setView(view);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals(oldName)
                        && detailCode.getText().toString().equals(oldCode)
                        && other.getText().toString().equals(code.getOther())) {
                    dialog.dismiss();
                    return;
                }

                Code c = new Code(name.getText().toString(), detailCode.getText().toString(), other.getText().toString(), code.getCount() + 1);
                if (resetAdapter) {
                    adapter.updateCode(pos, c, code.getId() + "");
                } else if (rAdapter.getCodes() != null) {
                    rAdapter.updateCode(pos, c, code.getId() + "");
                }
                dialog.dismiss();
            }
        });
        dialog = builder.show();
    }

    AlertDialog dialog = null;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.add_code:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_code_dialog, null);
                final EditText name = (EditText) view.findViewById(R.id.add_code_name);
                final EditText code = (EditText) view.findViewById(R.id.add_code_pwd);
                final EditText other = (EditText) view.findViewById(R.id.add_code_other);
                TextView done = (TextView) view.findViewById(R.id.add_code_done);
                builder.setView(view);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (name.getText().toString().equals("")) {
                            Show("密码标题不能为空 ! ! !");
                            return;
                        }
                        if (code.getText().toString().equals("")) {
                            Show("密码不能为空");
                            return;
                        }
                        int count = 1;
                        if (codes != null && !codes.isEmpty()) {
                            count = codes.get(0).getCount();
                        }
                        Code c = new Code(name.getText().toString(), code.getText().toString(), other.getText().toString(),count);
                        c.setAsyn(0);
                        adapter.addCode(0, c);
                        recyclerView.scrollToPosition(0);
                        dialog.dismiss();
                    }
                });
                dialog = builder.show();
                break;
        }
    }

    /*
    密码改变的回调
    Tag:
    0 本地导入的回调
    1 导入加载的所有密码
    2 密码搜索回调
    3 本地导入的加载动画开始
    4 本地导入的加载动画关闭
     */
        @Override
    public void onCodeChange(int Tag, List<Code> codes) {
        if (Tag == 0) {
            codeDataListener.onCodeDataGet(1, this.codes);
            loadingLayout.setVisibility(View.GONE);
        } else if (Tag == 1) {
            adapter.addAll(codes);
            loadingLayout.setVisibility(View.GONE);
        } else if (Tag == 2 && !searchText.getText().toString().equals("")) {
            searchText.setText("");
        } else if (Tag == 3) {
            loadingLayout.setVisibility(View.VISIBLE);
        } else if (Tag == 4) {
            loadingLayout.setVisibility(View.GONE);
        }


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                refreshLayout.setRefreshing(false);
            }
        }
    };

}
