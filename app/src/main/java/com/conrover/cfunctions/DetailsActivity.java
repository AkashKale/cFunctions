package com.conrover.cfunctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by sony on 29/08/2015.
 */
public class DetailsActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailsactivity);
        TextView tv= (TextView) findViewById(R.id.tvvvv);
       // Intent intent=getIntent();
        Bundle bundle=getIntent().getExtras();
        String header= bundle.getString("header");
        String function_name=bundle.getString("function_name");
        tv.setText("Header="+header+" func_name="+function_name);
    }
}
