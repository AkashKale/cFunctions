package com.conrover.cfunctions;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by sony on 29/08/2015.
 */
public class DetailsActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    TextView tvHeaderFile,tvSyntax,tvReturns,tvParameters,tvDesc;
    String function_name,header,synt,desc,par,ret,hea;
    boolean isFavorite=false;
    Set<String> favoriteList;
    ArrayList<String> favoriteListArray;
    SharedPreferences sp;
    ListView lvSee;
    ArrayList<String> SimilarFunList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailsactivity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle bundle=getIntent().getExtras();
        header= bundle.getString("header");
        function_name=bundle.getString("function_name");
        tvHeaderFile= (TextView) findViewById(R.id.tvHeaderFile);
        tvSyntax= (TextView) findViewById(R.id.tvSyntax);
        tvReturns= (TextView) findViewById(R.id.tvReturns);
        tvParameters= (TextView) findViewById(R.id.tvParameters);
        tvDesc= (TextView) findViewById(R.id.tvDesc);
        lvSee= (ListView) findViewById(R.id.lvSee);
        lvSee.setOnItemClickListener(this);
        new loadfunction().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        isFavorite=false;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details_activity, menu);
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        favoriteList=sp.getStringSet("favorite_list", null);
        favoriteListArray=new ArrayList<String>();
        if(favoriteList!=null)
        {
            favoriteListArray=new ArrayList<String>(favoriteList);
            if(favoriteList.contains(function_name))
            {
                isFavorite = true;
                menu.getItem(0).setIcon(R.drawable.ic_action_toggle_star);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //Log.e("sdasfafdaffa","yeessssssssssss");
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("Option","Selected");
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
            case R.id.menuitem_favorite:
                if(isFavorite)
                {
                    isFavorite=false;
                    item.setIcon(R.drawable.ic_action_toggle_star_outline);
                    favoriteListArray.remove(function_name);
                    Log.e("fla", favoriteListArray.toString());
                    favoriteList=new HashSet<>(favoriteListArray);
                    sp.edit().putStringSet("favorite_list",favoriteList).commit();
                }
                else
                {
                    isFavorite=true;
                    item.setIcon(R.drawable.ic_action_toggle_star);
                    favoriteListArray.add(function_name);
                    Log.e("fla", favoriteListArray.toString());
                    favoriteList=new HashSet<>(favoriteListArray);
                    sp.edit().putStringSet("favorite_list",favoriteList).commit();
                }
                break;
        }
        return true;
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
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        invalidateOptionsMenu();
        String funname=(String)lvSee.getItemAtPosition(position);
        function_name=(String)lvSee.getItemAtPosition(position);
        Log.e("onitemclick",funname);

        new loadfunction().execute();
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
                        JSONArray seealso=jo_inside.getJSONArray("seealso");
                        SimilarFunList=new ArrayList<String>();
                        for(int j=0;j<seealso.length();j++)
                        {
                            JSONObject similar=seealso.getJSONObject(j);
                            SimilarFunList.add(similar.getString("similarfun"));
                        }
                    }
                }
                return ret;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ret;
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
           ArrayAdapter<String> adapter = new ArrayAdapter<String>(DetailsActivity.this, android.R.layout.simple_list_item_1,SimilarFunList) {

               @Override
               public View getView(int position, View convertView,
                                   ViewGroup parent) {
                   View view =super.getView(position, convertView, parent);

                   TextView textView=(TextView) view.findViewById(android.R.id.text1);

            /*YOUR CHOICE OF COLOR*/
                   textView.setTextColor(Color.WHITE);
                   return view;
               }
           };
           lvSee.setAdapter(adapter);
           //lvSee.setAdapter(new ArrayAdapter<String>(DetailsActivity.this, android.R.layout.simple_list_item_1, SimilarFunList));
           setListViewHeightBasedOnChildren(lvSee);
           lvSee.setOnItemClickListener(DetailsActivity.this);
        }
    }
}
