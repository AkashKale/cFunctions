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
public class NavigationDrawerFragment extends Fragment {

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
    private ListView mDrawerListView;
    private ListView lvFavorites;

    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    SharedPreferences sp;
    private SortByListAdapter sortByListAdapter;
    private FavoriteListAdapter favoriteListAdapter;

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
        View drawerView=inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView = (ListView)drawerView.findViewById(R.id.elvDrawer);
        sortByListAdapter=new SortByListAdapter(getActionBar().getThemedContext());
        mDrawerListView.setAdapter(sortByListAdapter);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Position clicked", position + "");
                TextView tvClickedView=(TextView)view.findViewById(R.id.tvNavDrawer);
                TextView tvNotClicked;
                ImageView ivClicked=(ImageView)view.findViewById(R.id.ivNavDrawer);
                ImageView ivNotClicked;
                LinearLayout linearLayout;
                SharedPreferences.Editor editor=sp.edit();
                Log.e("Position Clicked",position+"");
                switch(position)
                {
                    case 0:
                        linearLayout=(LinearLayout)mDrawerListView.getChildAt(1);
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
                        linearLayout=(LinearLayout)mDrawerListView.getChildAt(0);
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
                selectItem(position);
            }
        });
        lvFavorites = (ListView) drawerView.findViewById(R.id.lvFavorites);
        favoriteList=sp.getStringSet("favorite_list",null);
        if(favoriteList!=null) {
            favoriteListArray = new ArrayList<String>(favoriteList);
            favoriteListAdapter = new FavoriteListAdapter(getActionBar().getThemedContext(), favoriteListArray);
            lvFavorites.setAdapter(favoriteListAdapter);
        }
        lvFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String funname=(String)lvFavorites.getItemAtPosition(i);
                Log.e("Fav",funname);
                Intent intent=new Intent(getActionBar().getThemedContext(),DetailsActivity.class);
                Bundle b=new Bundle();
                b.putString("header", "none");
                b.putString("function_name", funname);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        return drawerView;
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
                    favoriteListAdapter = new FavoriteListAdapter(getActionBar().getThemedContext(), favoriteListArray);
                    lvFavorites.setAdapter(favoriteListAdapter);
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
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
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
    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    public class SortByListAdapter extends BaseAdapter
    {
        private Context context;
        String[] sortTypes;
        int[] images={R.drawable.ic_header,R.drawable.ic_function};
        int[] imagesSelected={R.drawable.ic_header_selected,R.drawable.ic_function_selected};
        public SortByListAdapter(Context context){
            sortTypes=context.getResources().getStringArray(R.array.sort_types);
            this.context=context;
        }

        @Override
        public int getCount() {
            return sortTypes.length;
        }

        @Override
        public Object getItem(int i) {
            return sortTypes[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            View listItem=null;
            if(view==null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                listItem = inflater.inflate(R.layout.drawer_layout, viewGroup, false);
                TextView tvNavDrawer = (TextView) listItem.findViewById(R.id.tvNavDrawer);
                ImageView ivNavDrawer = (ImageView) listItem.findViewById(R.id.ivNavDrawer);
                tvNavDrawer.setText(sortTypes[i]);
                ivNavDrawer.setImageResource(images[i]);
                int position = Integer.parseInt(sp.getString("position", "0"));
                Log.e("position", position + "");
                if (position == i) {
                    tvNavDrawer.setTypeface(null, Typeface.BOLD);
                    tvNavDrawer.setTextColor(getResources().getColor(R.color.primary));
                    ivNavDrawer.setImageResource(imagesSelected[position]);
                }
            }
            else
            {
                listItem=view;
            }
            return listItem;
        }
    }
    public class FavoriteListAdapter extends BaseAdapter{

        private Context context;
        private ArrayList<String> favorites;
        public FavoriteListAdapter(Context context,ArrayList<String>favorites)
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
            View listitem=null;
            if(view==null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                listitem = inflater.inflate(R.layout.favorite_list_item, viewGroup, false);
                TextView tvListItem = (TextView) listitem.findViewById(R.id.tvListItem);
                tvListItem.setText(favorites.get(i));
            }
            else
            {
                listitem=view;
            }
            return listitem;
        }
    }
}
