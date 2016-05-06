package dong.lan.code.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目：code
 * 作者：梁桂栋
 * 日期： 2015/10/31  02:43.
 */
public class Note implements Parcelable {

    private String type;
    private String time;
    private String note;

    public Note()
    {}

    public Note(String type,String time,String note)
    {
        this.type = type;
        this.time = time;
        this.note = note;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.time);
        dest.writeString(this.note);
    }

    protected Note(Parcel in) {
        this.type = in.readString();
        this.time = in.readString();
        this.note = in.readString();
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
