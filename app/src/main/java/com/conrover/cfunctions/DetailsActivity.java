package com.conrover.cfunctions;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by sony on 29/08/2015.
 */
public class DetailsActivity extends ActionBarActivity {

    TextView tvHeaderFile,tvSyntax,tvReturns,tvParameters,tvDesc;
    String function_name,header,synt,desc,par,ret,hea;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailsactivity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //TextView tv= (TextView) findViewById(R.id.tvvvv);
        Bundle bundle=getIntent().getExtras();
        header= bundle.getString("header");
        function_name=bundle.getString("function_name");
        //tv.setText("Header="+header+" func_name="+function_name);
        tvHeaderFile= (TextView) findViewById(R.id.tvHeaderFile);
        tvSyntax= (TextView) findViewById(R.id.tvSyntax);
        tvReturns= (TextView) findViewById(R.id.tvReturns);
        tvParameters= (TextView) findViewById(R.id.tvParameters);
        tvDesc= (TextView) findViewById(R.id.tvDesc);
        new loadfunction().execute();

    }
    public String loadJSONFromAsset() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = getAssets().open("funinfo.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public class loadfunction extends AsyncTask<Void,Integer,String>{

        @Override
        protected String doInBackground(Void... params) {
            try {
                JSONObject obj = new JSONObject(loadJSONFromAsset());
                JSONArray m_jArry = obj.getJSONArray("funlist");

                for (int i = 0; i < m_jArry.length(); i++) {
                    JSONObject jo_inside = m_jArry.getJSONObject(i);
                    String name = jo_inside.getString("funname");
                    if(name.equals(function_name)){

                       hea=jo_inside.getString("header");
                        synt=jo_inside.getString("syntax");
                        ret=jo_inside.getString("returns");
                        par=jo_inside.getString("parameters");
                        desc=jo_inside.getString("desc");
                       // Log.e("syntax---",jo_inside.getString("returns"));
                        //tvHeaderFile.setText(header);
                        //tvSyntax.setText(synt);
                        //tvReturns.setText(ret);
                        //tvParameters.setText(par);
                        //tvDesc.setText(desc);
                        //break;
                    }
                    //temp.add(name);
                    //formList.add(m_li);
                }
                return ret;
            } catch (JSONException e) {
                e.printStackTrace();
                tvHeaderFile.setText("heeeee");
                //Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
            return ret;
            //return null;
        }

       @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tvHeaderFile.setText(hea);
            //tvHeaderFile.setText(header);
            tvSyntax.setText(synt);
            tvReturns.setText(ret);
            tvParameters.setText(par);
            tvDesc.setText(desc);
        }
    }
}
