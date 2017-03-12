package bombscheduling.com.bombscheduling;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

public class Bomb implements Parcelable {

    private int id;
    private String title;
    private String body;
    private Calendar time;

    public Bomb(int id, String title, String body, Calendar time) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    protected Bomb(Parcel in) {
        id = in.readInt();
        title = in.readString();
        body = in.readString();
        time = (Calendar) in.readValue(Calendar.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeValue(time);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Bomb> CREATOR = new Parcelable.Creator<Bomb>() {
        @Override
        public Bomb createFromParcel(Parcel in) {
            return new Bomb(in);
        }

        @Override
        public Bomb[] newArray(int size) {
            return new Bomb[size];
        }
    };
}