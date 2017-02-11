package com.example.owner.winez;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.owner.winez.Model.Model;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabControlFragment extends Fragment {

    public TabControlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the menu
        setHasOptionsMenu(true);
        buildTabs();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_control, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.uppermenu, menu);
        menu.findItem(R.id.menu_signout).setVisible(true);
        menu.findItem(R.id.menu_add_picture).setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_signout:
            {
                Model.getInstance().signOut();

                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume(){
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        super.onResume();
    }

    @Override
    public void onDestroyView(){
        // Checking before destroying
        if (!getActivity().isDestroyed()) {

            getActivity().getActionBar().removeAllTabs();
            getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
        super.onDestroyView();
    }
    private void buildTabs() {
        ActionBar bar = getActivity().getActionBar();

        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Checking if tabs already created
        if (bar.getTabCount() == 0) {
            final MyWinesListFragment myWines = new MyWinesListFragment();
            final AllWinesFragment allWines = new AllWinesFragment();
            ActionBar.Tab myWinesTab = bar.newTab().setText("My Wines").setTabListener(new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                    fragmentTransaction.add(R.id.tabs_maineview, myWines);
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                    fragmentTransaction.remove(myWines);
                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                }
            });
            bar.addTab(myWinesTab);

            bar.addTab(bar.newTab().setText("All Wines").setTabListener(new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                    fragmentTransaction.add(R.id.tabs_maineview, allWines);
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                    fragmentTransaction.remove(allWines);
                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

                }
            }),false);

        }
    }
}
