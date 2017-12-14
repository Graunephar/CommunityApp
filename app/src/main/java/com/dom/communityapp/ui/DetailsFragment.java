package com.dom.communityapp.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dom.communityapp.R;
import com.dom.communityapp.ui.InfoWindowAdapter;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by oleklitgaard-jensen on 13/12/2017.
 */

public class DetailsFragment extends Fragment{

    private ImageView detailsImage;

    FragmentManager Manager = getFragmentManager();
    private InfoWindowAdapter mInfoWindowAdapter;

    public void addInfoWindowAdapter(InfoWindowAdapter adapter) {

        this.mInfoWindowAdapter = adapter;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.details_fragment_layout,container,false);

        //ButterKnife.bind(getView());


        detailsImage = (ImageView) view.findViewById(R.id.imageButton);

        detailsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInfoWindowAdapter.removeDetailsFragment();
            }
            });

        return view;


    }

}
