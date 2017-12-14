package com.dom.communityapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by oleklitgaard-jensen on 13/12/2017.
 */

public class DetailsFragment extends Fragment{

    private ImageView detailsImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.details_fragment_layout,container,false);

        //ButterKnife.bind(getView());


        detailsImage = (ImageView) view.findViewById(R.id.imageButton);

        detailsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity mapsActivity = (MapsActivity)getActivity();
                mapsActivity.removeDetailsFragment();
            }
            });

        return view;


    }
    FragmentManager Manager = getFragmentManager();


  /* public void removeDetailsFragment() {
        DetailsFragment detailsFragment = (DetailsFragment) Manager.findFragmentByTag("dtFragment");
        FragmentTransaction transaction = Manager.beginTransaction();
        transaction.remove(detailsFragment);
        transaction.commit();
    }*/

}
