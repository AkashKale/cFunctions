package com.conrover.cfunctions;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ExpandableListView.OnChildClickListener, SearchView.OnQueryTextListener, View.OnClickListener, SearchView.OnCloseListener, View.OnFocusChangeListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    String position;
    ExpandableListView elvList;//Our ExpandableListView
    SearchView svSearch;//Our SearchView
    ExpandableListAdapter listAdapter; //Adapter to add items to elvList
    List<String> groupNames;    //titles of each section
    HashMap<String,List<String>> listItems; //contents of elvList
    SharedPreferences sp;
    SearchManager searchManager;
    FloatingActionButton fab;
    boolean searchViewVisible=false;
    LinearLayout searchBoxLayout;
    String header,function_name,favflag;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        favflag=sp.getString("favflag", "0");
        if(favflag.equals("1"))
        {
            Bundle bundle = getIntent().getExtras();
            header = bundle.getString("header");
            function_name = bundle.getString("function_name");
            editor = sp.edit();
            editor.putString("favflag","0");
            editor.commit();
            if (header.equals("Nothing")) {
                Intent intent = new Intent(this, DetailsActivity.class);
                Bundle b = new Bundle();
                b.putString("header", "Nothing");
                b.putString("function_name", function_name);
                intent.putExtras(b);
                finish();
                startActivity(intent);
            }
        }
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        elvList=(ExpandableListView)findViewById(R.id.elvList);
        svSearch=(SearchView)findViewById(R.id.svSearch);
        svSearch.setOnQueryTextFocusChangeListener(this);
        setupExpList();

        new loadheaderfile().execute();
        listAdapter = new ExpandableListAdapter(this, groupNames, listItems);
        elvList.setAdapter(listAdapter);
        elvList.setOnChildClickListener(this);

        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(this);
        searchManager= (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        svSearch.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        svSearch.setIconifiedByDefault(false);
        svSearch.setOnQueryTextListener(this);
        svSearch.setOnCloseListener(this);
        svSearch.setOnSearchClickListener(this);
        searchBoxLayout=(LinearLayout)findViewById(R.id.searchBoxLayout);
    }
    private  void ExpandAll(){
        int count=listAdapter.getGroupCount();
        for(int i=0;i<count;i++)
        {
            elvList.expandGroup(i);
        }
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
    public void onClick(View view) {
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        svSearch.requestFocus();
        if(searchViewVisible)
        {
            animateClose();
            searchBoxLayout.setVisibility(View.INVISIBLE);
            searchViewVisible=false;
        }
        else
        {
            searchBoxLayout.setVisibility(View.VISIBLE);
            searchViewVisible=true;
            animateOpen();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        listAdapter.FilterData(query);
        searchViewVisible=false;
        animateClose();
        searchBoxLayout.setVisibility(View.INVISIBLE);
        ExpandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.e("Text", newText);
        if(!newText.isEmpty()) {
            listAdapter.FilterData(newText);
            ExpandAll();
        }else{
            Log.e("Text", "isempty");
            //searchBoxLayout.setVisibility(View.INVISIBLE);
            new loadheaderfile().execute();
        }
        return false;
    }

    @Override
    public boolean onClose() {
        Log.e("onclose", "true");
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        animateClose();
        searchBoxLayout.setVisibility(View.INVISIBLE);
        listAdapter.FilterData("");
        new loadheaderfile().execute();
        return false;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        //if(searchViewVisible)
        //{
        animateClose();
        searchBoxLayout.setVisibility(View.INVISIBLE);
        searchViewVisible=false;
        //}
        /*else
        {
            searchBoxLayout.setVisibility(View.VISIBLE);
            searchViewVisible=true;
        }*/
        //searchViewVisible=false;
        //searchBoxLayout.setVisibility(View.INVISIBLE);
        Log.e("fsfs", "sdfsfasdfsfafd");
    }

    void animateOpen()
    {
        final OvershootInterpolator interpolator = new OvershootInterpolator();
        ViewCompat.animate(fab).alpha(0.5f).withLayer().rotation(135f/2).setDuration(200).start();
        fab.setImageResource(R.drawable.ic_content_add);
        ViewCompat.animate(fab).alpha(1).withLayer().rotation(135f).setDuration(200).setInterpolator(interpolator).start();
        searchBoxLayout.animate().alpha(1).setDuration(400);
    }
    void animateClose()
    {
        final OvershootInterpolator interpolator = new OvershootInterpolator();
        ViewCompat.animate(fab).alpha(0.5f).withLayer().rotation(135f / 2).setDuration(200).start();
        fab.setImageResource(R.drawable.ic_action_search);
        ViewCompat.animate(fab).alpha(1).withLayer().rotation(0).setDuration(200).setInterpolator(interpolator).start();
        searchBoxLayout.animate().alpha(0).setDuration(400);
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
                for (int i = 0; i < m_jArry.length(); i++) {
                    JSONObject jo_inside = m_jArry.getJSONObject(i);
                    String name = jo_inside.getString("name");
                    temp.add(name);
                }
                return temp;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            groupNames = new ArrayList<String>(strings);
            listItems.clear();
            List<String> locallist = null;
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
                    listItems.put(groupNames.get(j), locallist);
                    j++;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            listAdapter = new ExpandableListAdapter(MainActivity.this, groupNames, listItems);
            elvList.setAdapter(listAdapter);
            Log.e("Groupnames",groupNames.size()+"");
            Log.e("listitems",listItems.size()+"");
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
        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        svSearch.setQuery("",false);
        Intent i=new Intent(this,DetailsActivity.class);
        Bundle b=new Bundle();
        String grpname=(String)listAdapter.getGroup(groupPosition);
        String funname=(String)listAdapter.getChild(groupPosition,childPosition);
        b.putString("header", grpname);
        b.putString("function_name", funname);
        i.putExtras(b);
        startActivity(i);
        //onClose();
        return true;
    }

    //CustomSearchView class
    public class CustomSearchView extends SearchView{

        public CustomSearchView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        /*public CustomSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public CustomSearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }*/

        public CustomSearchView(Context context) {
            super(context);
        }

        @Override
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            Log.e("Keyboard", "true");
           // if (keyCode == KeyEvent.KEYCODE_BACK &&
            //        event.getAction() == KeyEvent.ACTION_UP) {

                return true;
            //}
            //return super.dispatchKeyEvent(event);
        }
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

        /**x
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
