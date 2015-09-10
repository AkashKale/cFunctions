package com.conrover.cfunctions;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements ExpandableListView.OnChildClickListener {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerExpListView;
    private ListView lvFavorites;

    ExpandableListAdapter expListAdapter;
    List<String> groupNames;
    HashMap<String,List<String>> listItems;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    SharedPreferences sp;
    public int imageSet=0;

    private DrawerAdapter drawerAdapter;
    private CustomListAdapter customListAdapter;

    Set<String> favoriteList;
    ArrayList<String>favoriteListArray;
    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View drawerView=(View)inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerExpListView = (ExpandableListView)drawerView.findViewById(R.id.elvDrawer);
        mDrawerExpListView.setOnChildClickListener(this);
        mDrawerExpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Position clicked", position + "");
                selectItem(position);
            }
        });
        setupExpList();
        drawerAdapter=new DrawerAdapter(getActionBar().getThemedContext(),groupNames,listItems);
        mDrawerExpListView.setAdapter(drawerAdapter);
        mDrawerExpListView.setItemChecked(mCurrentSelectedPosition, true);
        //expListAdapter=new ExpandableListAdapter(getActionBar().getThemedContext(),groupNames,listItems);
        //mDrawerExpListView.setAdapter(expListAdapter);
        lvFavorites = (ListView) drawerView.findViewById(R.id.lvFavorites);
        favoriteList=sp.getStringSet("favorite_list",null);
        if(favoriteList!=null) {
            favoriteListArray = new ArrayList<String>(favoriteList);
            customListAdapter = new CustomListAdapter(getActionBar().getThemedContext(), favoriteListArray);
            lvFavorites.setAdapter(customListAdapter);
        }
        lvFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("Fav","Clicked");
            }
        });

        return drawerView;
    }

    private void setupExpList() {
        groupNames = new ArrayList<String>();
        listItems = new HashMap<String, List<String>>();

        // Adding group names
        groupNames.add("Sort By");

        // Adding list items
        List<String> sortTypes = new ArrayList<String>();
        sortTypes.add("Header Files");
        sortTypes.add("Function Names");
        listItems.put("Sort By", sortTypes); // Header, Child data
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_action_navigation_menu,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.e("Drawer", "opened");
                favoriteList=sp.getStringSet("favorite_list",null);
                if(favoriteList!=null) {
                    favoriteListArray = new ArrayList<String>(favoriteList);
                    customListAdapter = new CustomListAdapter(getActionBar().getThemedContext(), favoriteListArray);
                    lvFavorites.setAdapter(customListAdapter);
                }
                drawerView.bringToFront();
                mDrawerLayout.requestLayout();
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerExpListView != null) {
            mDrawerExpListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            Log.e("callback","true");
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            Log.e("onAttached","called");
            imageSet++;
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
        //ExpandableListView elv;
        TextView tvClickedView=(TextView)view.findViewById(R.id.tvNavDrawer);
        TextView tvNotClicked;
        ImageView ivClicked=(ImageView)view.findViewById(R.id.ivNavDrawer);
        ImageView ivNotClicked;
        LinearLayout linearLayout;
        SharedPreferences.Editor editor=sp.edit();
        switch(childPosition)
        {
            case 0:
                linearLayout=(LinearLayout)expandableListView.getChildAt(2);
                ivNotClicked=(ImageView)linearLayout.getChildAt(0);
                ivNotClicked.setImageResource(R.drawable.ic_function);
                ivClicked.setImageResource(R.drawable.ic_header_selected);
                tvNotClicked=(TextView)linearLayout.getChildAt(1);
                tvNotClicked.setTypeface(null, Typeface.NORMAL);
                tvClickedView.setTypeface(null, Typeface.BOLD);
                tvClickedView.setTextColor(getResources().getColor(R.color.primary));
                tvNotClicked.setTextColor(getResources().getColor(R.color.text_color));
                editor.putString("position", "0");
                editor.commit();
                break;
            case 1:
                linearLayout=(LinearLayout)expandableListView.getChildAt(1);
                ivNotClicked=(ImageView)linearLayout.getChildAt(0);
                ivNotClicked.setImageResource(R.drawable.ic_header);
                ivClicked.setImageResource(R.drawable.ic_function_selected);
                tvNotClicked=(TextView)linearLayout.getChildAt(1);
                tvNotClicked.setTypeface(null, Typeface.NORMAL);
                tvClickedView.setTypeface(null, Typeface.BOLD);
                tvClickedView.setTextColor(getResources().getColor(R.color.primary));
                tvNotClicked.setTextColor(getResources().getColor(R.color.text_color));
                editor.putString("position", "1");
                editor.commit();
                break;
        }
        selectItem(0);
        //startActivity(new Intent("android.intent.action.MainActivity"));
        //getActivity().finish();
        //getView().invalidate();
        //getActivity().recreate();
        /*FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment=getTargetFragment();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .detach(fragment)
                .attach(fragment)
                .commit();*/
        return true;
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    public class DrawerAdapter extends BaseExpandableListAdapter
    {
        private Context context;
        HashMap<String,List<String>> listitems;
        List<String> groupnames;
        String[] sortTypes;
        int[] images={R.drawable.ic_header,R.drawable.ic_function};
        int[] imagesSelected={R.drawable.ic_header_selected,R.drawable.ic_function_selected};
        public DrawerAdapter(Context context,List<String> groupnames,HashMap<String,List<String>> listItems){
            sortTypes=context.getResources().getStringArray(R.array.sort_types);
            this.context=context;
            this.listitems=listItems;
            this.groupnames=groupnames;
        }

        /*@Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View row=null;
            if(view==null)
            {
                LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row=inflater.inflate(R.layout.drawer_layout,viewGroup,false);
            }
            else
            {
                row=view;
            }
            TextView tvNavDrawer=(TextView)row.findViewById(R.id.tvNavDrawer);
            ImageView ivNavDrawer=(ImageView)row.findViewById(R.id.ivNavDrawer);
            tvNavDrawer.setText(sortTypes[i]);
            ivNavDrawer.setImageResource(images[i]);
            return row;
        }*/

        @Override
        public int getGroupCount() {
            return this.groupnames.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.listitems.get(this.groupnames.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.groupnames.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.listitems.get(this.groupnames.get(groupPosition))
                    .get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean b, View convertView, ViewGroup viewGroup) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_group, null);
            }

            TextView tvListGroup = (TextView) convertView
                    .findViewById(R.id.tvListGroup);
            tvListGroup.setTypeface(null, Typeface.BOLD);
            tvListGroup.setText(headerTitle);
            //Log.e("Header", "Working"+tvListGroup.getText());
            return convertView;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            View row=null;
            if(view==null)
            {
                LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row=inflater.inflate(R.layout.drawer_layout,viewGroup,false);
            }
            else
            {
                row=view;
            }
            if(imageSet<=2) {
                Log.e("Image", "Set");
                TextView tvNavDrawer = (TextView) row.findViewById(R.id.tvNavDrawer);
                ImageView ivNavDrawer = (ImageView) row.findViewById(R.id.ivNavDrawer);
                tvNavDrawer.setText(sortTypes[i1]);
                ivNavDrawer.setImageResource(images[i1]);
                int position=Integer.parseInt(sp.getString("position","0"));
                Log.e("position", position + "");
                if(position==i1)
                {
                    tvNavDrawer.setTypeface(null,Typeface.BOLD);
                    tvNavDrawer.setTextColor(getResources().getColor(R.color.primary));
                    ivNavDrawer.setImageResource(imagesSelected[position]);
                }
                imageSet++;
            }
            return row;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
    public class CustomListAdapter extends BaseAdapter{

        private Context context;
        private ArrayList<String> favorites;
        public CustomListAdapter(Context context,ArrayList<String>favorites)
        {
            this.context=context;
            this.favorites=favorites;
        }
        @Override
        public int getCount() {
            return favorites.size();
        }

        @Override
        public Object getItem(int i) {
            return favorites.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Log.e("getView","Pass");
            View listitem=null;
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listitem=inflater.inflate(R.layout.list_item,viewGroup,false);
            TextView tvListItem=(TextView)listitem.findViewById(R.id.tvListItem);
            tvListItem.setText(favorites.get(i));
            return listitem;
        }
    }
}
