package com.example.owner.winez;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.owner.winez.Model.Model;
import com.example.owner.winez.Model.Wine;
import com.example.owner.winez.Utils.ApiClasses.WineApiClass;
import com.example.owner.winez.Utils.Consts;
import com.example.owner.winez.Utils.WineApi;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllWinesFragment extends Fragment {

    List<WineApiClass> allWines;
    AllWinesAdapter mAdapter;

    public AllWinesFragment() {

     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_all_wines, container, false);
        final ListView list = (ListView)view.findViewById(R.id.all_wines_list);
        mAdapter = new AllWinesAdapter();
        allWines = new ArrayList<>();
        WineApi.getInstance().GetWinesByCategory(new WineApi.GetResultOnResponse<WineApiClass>() {
            @Override
            public void onResult(ArrayList<WineApiClass> data) {
                allWines = data;
                view.findViewById(R.id.all_wines_waiting_bar).setVisibility(View.GONE);
                view.findViewById(R.id.all_wines_layout).setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancel() {

            }
        });

        list.setAdapter(mAdapter);
        list.setEmptyView(view.findViewById(R.id.all_wines_empty_txt));
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d("TAG", "row selected " + position);

                WineApiClass currentWine = allWines.get(Integer.parseInt(view.getTag().toString()));
                Model.getInstance().addWineToDB(currentWine);

                Fragment wineDetailFrag = new WineFragment();
                FragmentTransaction ftr  = getActivity().getFragmentManager().beginTransaction();
                Bundle WineToShow = new Bundle();
                WineToShow.putString(Consts.WINE_BUNDLE_ID,currentWine.getId());

                wineDetailFrag.setArguments(WineToShow);
                ftr.replace(R.id.WinezActivityMainView, wineDetailFrag);
                ftr.addToBackStack(null);
                ftr.show(wineDetailFrag);
                ftr.commit();
            }
        });

        return view;
    }

    class AllWinesAdapter extends BaseAdapter{



        @Override
        public int getCount() {
            return allWines.size();
        }

        @Override
        public Object getItem(int i) {
            return allWines.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null){
                view = LayoutInflater.from(getActivity()).inflate(R.layout.row_all_wine_list,null);
                CheckBox cb = (CheckBox)view.findViewById(R.id.all_wine_favorite);

                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WineApiClass currentWine =
                                (WineApiClass)getItem((Integer) ((View)view.getParent()).getTag());
                        if(((CheckBox)view).isChecked()) {
                            Model.getInstance().addWine(new Wine(currentWine));
                        }else{
                            Model.getInstance().removeWine(currentWine);
                        }
                    }
                });
            }

            WineApiClass item = (WineApiClass)this.getItem(i);
            TextView nameText = (TextView) view.findViewById(R.id.row_all_wine_name);
            nameText.setText( item.getName() + " - " + item.getType());
            CheckBox cb = (CheckBox)view.findViewById(R.id.all_wine_favorite);
            cb.setChecked(Model.getInstance().getCurrentUser().getUserWines().containsKey(item.getId()));
            view.setTag(i);

            return view;
        }
    }

}
