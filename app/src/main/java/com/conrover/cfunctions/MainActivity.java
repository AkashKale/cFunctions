package com.conrover.cfunctions;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;
import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    String position;
    ExpandableListView elvList; //Our ExpandableListView
    ExpandableListAdapter listAdapter; //Adapter to add items to elvList
    List<String> groupNames;    //titles of each section
    HashMap<String,List<String>> listItems; //contents of elvList
    private Resources MainActivity;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        elvList=(ExpandableListView)findViewById(R.id.elvList);
        setupExpList();

        new loadheaderfile().execute();
        listAdapter = new ExpandableListAdapter(this, groupNames, listItems);
        elvList.setAdapter(listAdapter);
        elvList.setOnChildClickListener(this);
        //elvList.setOnGroupClickListener(this);

    }
    //setup Contents of ExpandableListView
    private void setupExpList() {
        groupNames = new ArrayList<String>();
        listItems = new HashMap<String, List<String>>();

        // Adding group names
        groupNames.add("stdio.h.");
        groupNames.add("conio.h");
        groupNames.add("header3.h");

        // Adding list items
        List<String> functions = new ArrayList<String>();
        for(int i=0;i<5;i++) {
            functions.add("function"+(i+1));
        }

        listItems.put(groupNames.get(0), functions); // Header, Child data
        listItems.put(groupNames.get(1), functions);
        listItems.put(groupNames.get(2), functions);
  }

    public String loadJSONFromAsset() {
        StringBuilder stringBuilder = new StringBuilder();
            try {
                sp= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                position=sp.getString("position", "0");
                //Log.e("Position",position+"");
                InputStream is;
                if(position.equals("0"))
                {
                    is = getAssets().open("headerfileinfo.json");
                }
                else
                {
                    is = getAssets().open("sortByFunctionNames.json");
                }
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
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }

    public class loadheaderfile extends AsyncTask<Void,Integer,ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            //Log.e("Background","running");
            ArrayList<String> temp=new ArrayList<String>();
            try {
                JSONObject obj = new JSONObject(loadJSONFromAsset());
                JSONArray m_jArry;
                if(position.equals("0")) {
                    m_jArry = obj.getJSONArray("headfile");
                }
                else{
                    m_jArry = obj.getJSONArray("alphabets");
                }
                //ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
                //HashMap<String, String> m_li;

                for (int i = 0; i < m_jArry.length(); i++) {
                    JSONObject jo_inside = m_jArry.getJSONObject(i);
                   // Log.d("Details-->", jo_inside.getString("formule"));
                    String name = jo_inside.getString("name");
                    //String url_value = jo_inside.getString("url");

                    //Add your values in your `ArrayList` as below:
                    //m_li = new HashMap<String, String>();
                    //m_li.put("formule", formula_value);
                    //m_li.put("url", url_value);
                    temp.add(name);
                    //formList.add(m_li);
                }
                return temp;
            } catch (JSONException e) {
                //e.printStackTrace();
                //Toast.makeText(getBaseContext(),e.toString(),Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            //Log.e("PostExecute", "running");
            groupNames = new ArrayList<String>(strings);
            List<String> locallist = null;
            List<String> functions = new ArrayList<String>();
            for (int i = 0; i < 5; i++) {
                functions.add("getc");
            }
            try {
                int j = 0;
                JSONObject jobj = new JSONObject(loadJSONFromAsset());
                JSONArray arr;
                if(position.equals("0")) {
                    arr = jobj.getJSONArray("headfile");
                }
                else{
                    arr = jobj.getJSONArray("alphabets");
                }
                while (j < arr.length()) {
                    JSONObject obj = arr.getJSONObject(j);
                    JSONArray inside_array = obj.getJSONArray("fun_name");
                    String[] temp = new String[inside_array.length()];
                    for (int i = 0; i < inside_array.length(); i++) {
                        JSONObject inside_obj = inside_array.getJSONObject(i);
                        temp[i] = inside_obj.getString("fun");
                    }
                    locallist = new ArrayList<String>(Arrays.asList(temp));

                    //for (int i = 0; i < groupNames.size(); i++) {
                        //if(i==2){
                        listItems.put(groupNames.get(j), locallist);
                    //}
                    j++;
                }
                //else{
                //listItems.put(groupNames.get(i), functions);
                // }// Header, Child data
            }
            catch(Exception e){
                e.printStackTrace();
        }
            listAdapter = new ExpandableListAdapter(MainActivity.this, groupNames, listItems);
            elvList.setAdapter(listAdapter);
        }
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Log.e("ItemSelected", "True");
        new loadheaderfile().execute();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.app_name);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.global, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        //if(groupPosition==2 && childPosition==1)
        //{
            Intent i=new Intent(this,DetailsActivity.class);
            Bundle b=new Bundle();
            String grpname=(String)listAdapter.getGroup(groupPosition);
            String funname=(String)listAdapter.getChild(groupPosition,childPosition);
            b.putString("header", grpname);
            b.putString("function_name", funname);
            i.putExtras(b);
            startActivity(i);
       // }
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
