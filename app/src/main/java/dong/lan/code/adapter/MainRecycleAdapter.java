package dong.lan.code.adapter;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dong.lan.code.Interface.ItemTouchListener;
import dong.lan.code.R;
import dong.lan.code.bean.Code;
import dong.lan.code.db.CodeDao;
import dong.lan.code.db.DBManager;
import dong.lan.code.utils.AES;

/**
 * 项目：code
 * 作者：梁桂栋
 * 日期： 2015/9/20  16:21.
 */
public class MainRecycleAdapter extends RecyclerView.Adapter<MainRecycleAdapter.CodeHolder> implements ItemTouchListener{

    private LayoutInflater inflater;
    private List<Code> codes;
    private Context context;

    @Override
    public void onItemMoved(int fromPos, int toPos) {
        ContentValues values = new ContentValues();
        int fromCount = codes.get(fromPos).getCount();
        int toCount = codes.get(toPos).getCount();

        codes.get(fromPos).setCount(toCount);
        codes.get(toPos).setCount(fromCount);

        values.put(CodeDao.COLUMN_COUNT, codes.get(fromPos).getCount());
        DBManager.getInstance().updateCode(values, String.valueOf(codes.get(fromPos).getId()));
        values.clear();

        values.put(CodeDao.COLUMN_COUNT, codes.get(toPos).getCount());
        DBManager.getInstance().updateCode(values, String.valueOf(codes.get(toPos).getId()));
        Code code = codes.get(fromPos);
        codes.set(fromPos,codes.get(toPos));
        codes.set(toPos,code);
        notifyItemMoved(fromPos,toPos);
    }

    @Override
    public void onItemSwiped(final int pos) {
        new AlertDialog.Builder(context)
                .setTitle("确定要删除该密码？")
                .setMessage(codes.get(pos).getDes())
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCode(pos);
                    }
                }).show();
            notifyItemChanged(pos);
    }

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
        if(this.codes!=null)
            CodeDao.minIndex = codes.get(0).getCount();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public CodeHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.item_list_code, null);
        return new CodeHolder(view);
    }


    @Override
    public void onBindViewHolder(final CodeHolder codeHolder, final int pos) {
        codeHolder.word.setText(codes.get(pos).getWord());
        codeHolder.des.setText(codes.get(pos).getDes());
        codeHolder.other.setText(codes.get(pos).getOther() == null ? "无" : codes.get(pos).getOther());
        if (onItemClickListener != null) {
            codeHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(codeHolder.itemView, codeHolder.getLayoutPosition());
                }
            });
            codeHolder.word.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onItemLongClick(codeHolder.itemView, codeHolder.getLayoutPosition(), 1);
                    return false;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (codes == null)
            return 0;
        return codes.size();
    }


    public void addCode(int pos, Code code) {
        if (codes == null)
            codes = new ArrayList<>();
        codes.add(pos, code);
        notifyItemInserted(pos);
        DBManager.getInstance().saveCode(code);


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

    public void addAll(List<Code> codes) {
        if (this.codes == null)
            this.codes = new ArrayList<>();
        this.codes.addAll(codes);
    }

    public void clear(){
        if(codes!=null)
            codes.clear();
    }
    public void updateCode(int pos, Code code, String id) {
        ContentValues values = new ContentValues();
        values.put(CodeDao.COLUMN_COUNT, code.getCount());
        values.put(CodeDao.COLUMN_CODE, code.getDes());
        values.put(CodeDao.COLUNMN_WORD, AES.encode(code.getWord()));
        values.put(CodeDao.COLUMN_OTHER, AES.encode(code.getOther()));
        DBManager.getInstance().updateCode(values, id);
        codes.get(pos).setCount(code.getCount());
        codes.get(pos).setDes(code.getDes());
        codes.get(pos).setWord(code.getWord());
        codes.get(pos).setOther(code.getOther());
        codes.get(pos).setAsyn(0);
        notifyDataSetChanged();
    }

    public void refresh() {
        if (codes == null)
            return;
        Collections.sort(codes);
        notifyDataSetChanged();
    }

    public void deleteCode(int pos) {
        DBManager.getInstance().deleteCode(codes.get(pos).getDes());
        codes.remove(pos);
        notifyItemRemoved(pos);
    }

    public void updateCount(int pos, boolean plus) {
        if (pos < codes.size() - 1) {
            ContentValues v = new ContentValues();
            v.put(CodeDao.COLUMN_COUNT, codes.get(pos + 1).getCount() + (plus ? 1 : -1));
            DBManager.getInstance().updateCode(v, codes.get(pos).getId() + "");
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

         CodeHolder(View itemView) {
            super(itemView);
            des = (TextView) itemView.findViewById(R.id.code_des);
            word = (TextView) itemView.findViewById(R.id.code_code_word);
            other = (TextView) itemView.findViewById(R.id.code_more);
        }
    }
}
