package dong.lan.code.adapter;


import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dong.lan.code.R;
import dong.lan.code.bean.Code;
import dong.lan.code.db.CodeDao;
import dong.lan.code.db.DBManeger;
import dong.lan.code.utils.AES;

/**
 * Created by Dooze on 2015/9/20.
 */
public class MainRecycleAdapter extends RecyclerView.Adapter<MainRecycleAdapter.CodeHolder> {

    private LayoutInflater inflater;
    private Context context;
    private List<Code> codes;

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);

        void onItemLongClick(View view, int pos, int type);
    }


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public MainRecycleAdapter(Context context, List<Code> codes) {
        this.context = context;
        this.codes = codes;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public CodeHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.item_list_code, null);
        return new CodeHolder(view);
    }

    private static long firstTime;
    private static int POS = -1;
    private static boolean VISIT = false;

    @Override
    public void onBindViewHolder(final CodeHolder codeHolder, final int pos) {
        codeHolder.word.setText(codes.get(pos).getWord());
        codeHolder.des.setText(codes.get(pos).getDes());
        codeHolder.other.setText(codes.get(pos).getOther() == null ? "无" : codes.get(pos).getOther());
        if (onItemClickListener != null) {
            codeHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (firstTime + 500 > System.currentTimeMillis()) {
                        if (VISIT) {
                            codeHolder.word.setVisibility(View.GONE);
                            VISIT = false;
                        } else {
                            codeHolder.word.setVisibility(View.VISIBLE);
                            VISIT = true;
                        }
                    } else {
                        onItemClickListener.onItemClick(codeHolder.itemView, codeHolder.getPosition());
                    }
                    firstTime = System.currentTimeMillis();
                }
            });

            codeHolder.des.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onItemLongClick(codeHolder.itemView, codeHolder.getPosition(), 0);
                    return false;
                }
            });
            codeHolder.word.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onItemLongClick(codeHolder.itemView, codeHolder.getPosition(), 1);
                    return false;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if(codes==null)
            return 0;
        return codes.size();
    }


    public void addCode(int pos, Code code) {
        if(codes==null)
            codes= new ArrayList<>();
        codes.add(pos, code);
        notifyItemInserted(pos);
        DBManeger.getInstance().saveCode(code);


    }

    public void delAddAll(List<Code> codes) {
        if (this.codes == null) {
            this.codes = codes;
        } else {
            this.codes.clear();
            this.codes.addAll(codes);
        }
        notifyDataSetChanged();
    }

    public void addAll(List<Code> codes)
    {
        if(this.codes==null)
            this.codes= new ArrayList<>();
        this.codes.addAll(codes);
    }


    public void updateCode(int pos, Code code, String id) {
        ContentValues values = new ContentValues();
        values.put(CodeDao.COLUMN_COUNT, code.getCount());
        values.put(CodeDao.COLUMN_CODE, code.getDes());
        values.put(CodeDao.COLUNMN_WORD, AES.encode(code.getWord()));
        values.put(CodeDao.COLUMN_OTHER, AES.encode(code.getOther()));
        DBManeger.getInstance().updateCode(values, id);
        codes.get(pos).setCount(code.getCount());
        codes.get(pos).setDes(code.getDes());
        codes.get(pos).setWord(code.getWord());
        codes.get(pos).setOther(code.getOther());
        codes.get(pos).setAsyn(0);
        notifyDataSetChanged();
    }

    public void refresh()
    {
        if(codes==null)
            return;
        Collections.sort(codes);
        notifyDataSetChanged();
    }
    public void deleteCode(int pos) {
        DBManeger.getInstance().deleteCode(codes.get(pos).getDes());
        codes.remove(pos);
        notifyItemRemoved(pos);
    }

    public void updateCount(int pos, boolean plus) {
        if (pos < codes.size() - 1) {
            ContentValues v = new ContentValues();
            v.put(CodeDao.COLUMN_COUNT, codes.get(pos + 1).getCount() + (plus ? 1 : -1));
            DBManeger.getInstance().updateCode(v, codes.get(pos).getId() + "");
        }
    }

    public List<Code> getCodes() {
        return codes;
    }

    public Code getCodeAt(int pos) {
        return codes.get(pos);
    }

    class CodeHolder extends RecyclerView.ViewHolder {

        private TextView des;
        private TextView word;
        private TextView other;
        private LinearLayout parent;

        public CodeHolder(View itemView) {
            super(itemView);
            parent = (LinearLayout) itemView.findViewById(R.id.parent_code);
            des = (TextView) itemView.findViewById(R.id.code_des);
            word = (TextView) itemView.findViewById(R.id.code_code_word);
            other = (TextView) itemView.findViewById(R.id.code_more);
        }
    }
}
