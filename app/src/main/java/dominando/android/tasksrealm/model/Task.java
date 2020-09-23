package dominando.android.tasksrealm.model;

import androidx.annotation.NonNull;

import java.util.Calendar;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Task extends RealmObject {
    @PrimaryKey
    public String name;

    public String description;
    public long end;
    public String place;
    public boolean started;

    @NonNull
    @Override
    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(end);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return "Name: " + name + "\nDescription: " + description + "\nEnd: " +
                (day < 10 ? "0" + day : "" + day) + "/" +
                (month < 10 ? "0" + month : "" + month) + "/" +
                year + " " +
                (hour < 10 ? "0" + hour : "" + hour) + ":" +
                (minute < 10 ? "0" + minute : "" + minute);
    }
}
