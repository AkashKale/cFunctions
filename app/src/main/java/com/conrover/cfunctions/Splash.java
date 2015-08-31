package com.conrover.cfunctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by sony on 27/08/2015.
 */
public class Splash extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Thread timer=new Thread(){
            public void run()
            {
                try{
                    sleep(2000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally{
                    startActivity(new Intent("android.intent.action.MainActivity"));
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }
}
