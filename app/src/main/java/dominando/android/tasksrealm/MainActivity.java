package dominando.android.tasksrealm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import dominando.android.tasksrealm.model.Task;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    public static RealmResults<Task> results;

    public RealmChangeListener callback = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            Log.e("TASKS", "updated: " + element);
            results = (RealmResults<Task>) element;
            results = results.sort("end", Sort.ASCENDING);
            listTasks.setAdapter(new ArrayAdapter<Task>(
                            MainActivity.this,
                            android.R.layout.simple_list_item_1,
                            results
                    )
            );
        }
    };

    private ListView listTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateRealmQuery();
        setContentView(R.layout.activity_task_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton actionButton = findViewById(R.id.actionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SaveTask.class);
                startActivity(intent);
            }
        });

        listTasks = (ListView) findViewById(R.id.listTasks);
        listTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showOptions(results.get(position));
                return false;
            }
        });
        updateRealmQuery();
    }

    private void updateRealmQuery() {
        RealmResults<Task> result = ((CoreApplication)getApplication()).realm.where(Task.class).findAllAsync();
        result.addChangeListener(callback);
    }

    public void showOptions(final Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("What would you like to do with this task?");
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, SaveTask.class);
                intent.putExtra("task", task.name);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this can only be done from the same thread where the object was created
                ((CoreApplication)getApplication()).realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        task.deleteFromRealm();
                    }
                });

            }
        });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}