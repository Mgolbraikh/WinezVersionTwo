package com.example.owner.winez;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.owner.winez.Model.Comment;
import com.example.owner.winez.Model.Model;
import com.example.owner.winez.Model.Wine;
import com.example.owner.winez.Utils.Consts;
import com.example.owner.winez.Utils.WinezDB;
import com.example.owner.winez.Utils.WinezStorage;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class WineFragment extends Fragment {
    Wine wine;
    List<Comment> comments;
    CommentsAdapter mAdapter;
    ImageView wineImage;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    public WineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle WineIdBundle = getArguments();
        setHasOptionsMenu(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_wine, container, false);
        final ListView commentsList = (ListView) view.findViewById(R.id.wine_comment_list);
        mAdapter = new CommentsAdapter();
        comments = new ArrayList<>();
        commentsList.setAdapter(mAdapter);

        // Getting wine from local
        wine = Model.getInstance().getWineFromLocal(WineIdBundle.getString(Consts.WINE_BUNDLE_ID));

        if(wine != null){
            this.fillWineData(view);
        }

        // Getting wine from remote
        Model.getInstance().getWine(WineIdBundle.getString(Consts.WINE_BUNDLE_ID), new WinezDB.GetOnCompleteResult<Wine>() {
            @Override
            public void onResult(Wine data) {
                wine = data;
                Model.getInstance().getCommentsForWine(wine.getUid(), new WinezDB.OnChildEventListener<Comment>() {
                    @Override
                    public void onChildAdded(Comment child, String previousChildName) {
                        comments.add(child);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(Comment child, String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(Comment child) {
                        comments.remove(child);
                        mAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildMoved(Comment child, String previousChildName) {

                    }

                    @Override
                    public void onCancelled(String err) {

                    }
                });
                fillWineData(view);
                wineImage = (ImageView) view.findViewById(R.id.wine_image);

                // Gets wine image if there is one
                if (wine.getPicture() != null) {
                    Model.getInstance().getImage(wine.getPicture(), new WinezStorage.OnGetBitmapListener() {
                        @Override
                        public void onResult(Bitmap image) {
                            wineImage.setImageBitmap(image);
                        }

                        @Override
                        public void onCancelled() {

                        }
                    });
                }
                CheckBox star = ((CheckBox) view.findViewById(R.id.wine_is_favorite));
                star.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            Model.getInstance().addWine(wine);
                        } else {
                            Model.getInstance().getCurrentUser().getUserWines().remove(wine.getUid());
                        }

                        // Saving changes
                        Model.getInstance().saveCurrentUser();
                    }
                });

            }

            @Override
            public void onCancel(String err) {
                Log.d("wine", err);
            }
        });

        ((EditText) view.findViewById(R.id.wine_new_comment)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) ||
                        (actionId == EditorInfo.IME_ACTION_NEXT)) {

                    // Creating comment
                    Comment toAdd = new Comment(wine.getUid(),
                            Model.getInstance().getCurrentUser().getUid(),
                            textView.getText().toString(),
                            Model.getInstance().getCurrentUser().getName());

                    // Saving to remote
                    Model.getInstance().saveComment(toAdd);

                    textView.setText("");
                    return true; // consume.
                }
                return false;
            }
        });

        return view;
    }

    private void fillWineData(View view) {
        TextView tvTitle = (TextView) view.findViewById(R.id.wine_wine_title);
        tvTitle.setText(wine.getName());
        EditText edPrice = (EditText) view.findViewById(R.id.wine_price);
        edPrice.setText(Double.toString(wine.getPrice()));
        EditText edType = (EditText) view.findViewById(R.id.wine_type);
        edType.setText(wine.getType());
        EditText edYear = (EditText) view.findViewById(R.id.wine_vintage_year);
        edYear.setText(wine.getVintage());
        CheckBox star = ((CheckBox) view.findViewById(R.id.wine_is_favorite));

        // Checking checkbox if needed
        star.setChecked(Model.getInstance()
                .getCurrentUser()
                .getUserWines()
                .containsKey(wine.getUid()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.uppermenu, menu);
        menu.findItem(R.id.menu_add_picture);
        menu.findItem(R.id.menu_signout).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_signout: {
                Model.getInstance().signOut();

                break;
            }
            case R.id.menu_add_picture: {
                addPicture();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void addPicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            final Bitmap image = (Bitmap) extras.get("data");

            final String imgUrl = wine.getUid() + ".jpg";
            Model.getInstance().saveImage(image, imgUrl, new WinezStorage.OnSaveCompleteListener() {

                @Override
                public void failed() {
                    Toast.makeText(getActivity(), "Save failed", Toast.LENGTH_LONG);
                }

                @Override
                public void done() {
                    Toast.makeText(getActivity(), "Save complete", Toast.LENGTH_LONG);
                    wine.setPicture(imgUrl);
                    Model.getInstance().saveWine(wine);
                    wineImage.setImageBitmap(image);
                }
            });
        }
    }

    class CommentsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return comments.size();
        }

        @Override
        public Object getItem(int i) {
            return comments.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.row_comment_list, null);
            }

            // Check if this is not the first and the user wishes to enter data
            Comment currComent = (Comment) this.getItem(i);
            TextView tvName = (TextView) view.findViewById(R.id.row_comment_name);
            TextView tvText = (TextView) view.findViewById(R.id.row_comment_text);

            tvName.setText(currComent.getUserName());
            tvText.setText(currComent.getText());

            return view;
        }
    }
}
