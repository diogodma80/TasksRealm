package dominando.android.tasksrealm;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class CoreApplication extends Application {

    public Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
    }
}
