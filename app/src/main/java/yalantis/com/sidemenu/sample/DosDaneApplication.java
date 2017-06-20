package yalantis.com.sidemenu.sample;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Jarraya on 16/11/2015.
 */
public class DosDaneApplication extends Application {
    Boolean isRceiverActive=false;
    @Override
    public void onCreate() {
        super.onCreate();


        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "PElME4JwN4afYrAYpa4ZPt7Kz2GIqEwBd7d9MboN", "VgGp18P96ER9wgc7D4bj1V4jDBLQkCpzqkXB2SiV");


    }
}
