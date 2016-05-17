package dong.lan.code.bean;

import android.support.annotation.NonNull;

/**
 * 项目：  code
 * 作者：  梁桂栋
 * 日期：  2015/9/20  18:20.
 * Email: 760625325@qq.com
 */
public class Code implements Comparable<Code>{
    private int asyn;
    private int id;
    private String des;
    private String word;
    private int count;
    private String other;
    public Code()
    {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Code(String des,String word,int count)
    {
        this.des = des;
        this.count = count;
        this.word =word;
    }

    public Code(String des,String word,String other,int count)
    {
        this.des = des;
        this.count = count;
        this.word =word;
        this.other = other;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAsyn() {
        return asyn;
    }

    public void setAsyn(int asyn) {
        this.asyn = asyn;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }


    @Override
    public int compareTo(@NonNull Code another) {
        if(this.getCount()>another.getCount())
        {
            return 1;
        }
        if(this.getCount()==another.getCount())
        {
            return 0;
        }
        if(this.getCount()<another.getCount())
        {
            return -1;
        }
        return 0;
    }
}
