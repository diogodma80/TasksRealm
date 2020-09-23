package dominando.android.tasksrealm;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;

import dominando.android.tasksrealm.model.Task;
import io.realm.Realm;

public class SaveTask extends AppCompatActivity {

    private int day, month, year;
    private int hour = -1, minute = -1;

    private TextView saveTaskDate;
    private TextView saveTaskHour;
    private TextView saveTaskName;
    private TextView saveTaskDescription;
    private TextView saveTaskPlace;

    private String key;

    //private String messageError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        key = getIntent().getStringExtra("task");
        if (key != null){
            Task result = MainActivity.results.where().equalTo("name", key).findFirst();
        }

        saveTaskDate = (TextView) findViewById(R.id.saveTaskDate);
        saveTaskHour = (TextView) findViewById(R.id.saveTaskHour);

        saveTaskName = (TextView) findViewById(R.id.saveTaskName);
        saveTaskDescription = (TextView) findViewById(R.id.saveTaskDescription);
        saveTaskPlace = (TextView) findViewById(R.id.saveTaskPlace);

        key = getIntent().getStringExtra("task");

        if (key != null) {
            setTitle("Edit Task");
            Task result = ((CoreApplication) getApplication()).realm.where(Task.class).equalTo("name", key).findFirst();
            saveTaskName.setText(result.name);
            saveTaskName.setEnabled(false);
            saveTaskDescription.setText(result.description);
            saveTaskPlace.setText(result.place);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(result.end);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.DAY_OF_MONTH);
            year = calendar.get(calendar.YEAR);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);

            fillInDate();
            fillInHour();
        }

        final FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.actionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CoreApplication) getApplication()).realm.executeTransactionAsync(new Realm.Transaction() {

                    @Override
                    public void execute(Realm realm) {
                        Task task = new Task();
                        task.name = saveTaskName.getText().toString();
                        task.description = saveTaskDescription.getText().toString();
                        task.place = saveTaskPlace.getText().toString();

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, hour, minute);

                        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                            Snackbar.make(actionButton, "The end date must be in the future", Snackbar.LENGTH_LONG).show();
                        }

                        task.end = calendar.getTimeInMillis();
                        task.started = false;
                        realm.copyToRealmOrUpdate(task);

                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        //Transaction was successful
                        Snackbar.make(actionButton, "Task saved successfully", Snackbar.LENGTH_INDEFINITE).setAction("EXIT", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }).show();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        // Transaction failed and was automatically canceled.
                    }
                });
            }
        });

    }

    public void date(View view) {
        if (day == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                day = dayOfMonth;
                month = month;
                year = year;
                fillInDate();
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void fillInDate() {
        saveTaskDate.setText("End date: " +
                (day < 10 ? "0" + day : "" + day) + "/" +
                (month + 1 < 10 ? "0" + (month + 1) : "" + (month + 1)) + "/" +
                year);
        saveTaskDate.setVisibility(View.VISIBLE);
    }

    public void hour(View view) {
        if (hour == -1 && minute == -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
        }

        TimePickerDialog timerPickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                minute = minute;
                fillInHour();
            }
        }, hour, minute, true);
        timerPickerDialog.show();
    }

    private void fillInHour() {
        saveTaskHour.setText("End hour: " +
                (hour < 10 ? "0" + hour : "" + hour) + ":" +
                (minute < 10 ? "0" + minute : "" + minute));
        saveTaskHour.setVisibility(View.VISIBLE);
    }






}