package dong.lan.code.bean;

/**
 * Created by Dooze on 2015/10/31.
 */
public class Note {

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
}
