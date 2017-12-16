package com.dom.communityapp.ui;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dom.communityapp.R;
import com.dom.communityapp.models.CommunityIssue;
import com.dom.communityapp.models.IssueDropDownTranslator;


/**
 * Created by oleklitgaard-jensen on 13/12/2017.
 * The fragment with is inflated on top of the map markers
 */

public class DetailsFragment extends Fragment{

    private ImageView closeImage, detailsImage;
    private TextView destriptionLong,destriptionShort,category,tags,time;
    private Button resolve;


    private InfoWindowAdapter mInfoWindowAdapter;

    public void addInfoWindowAdapter(InfoWindowAdapter adapter) {

        this.mInfoWindowAdapter = adapter;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.details_fragment_layout,container,false);


        closeImage = (ImageView) view.findViewById(R.id.imageButton);
        detailsImage = (ImageView) view.findViewById(R.id.details_fragment_imView);
        destriptionLong = (TextView) view.findViewById(R.id.details_fragment_long_description);
        destriptionShort = (TextView) view.findViewById(R.id.details_fragment_short_description);
        category = (TextView) view.findViewById(R.id.details_fragment_category_txt);
        tags = (TextView) view.findViewById(R.id.details_fragment_tags_txt);
        time = (TextView) view.findViewById(R.id.details_fragment_time_txt);
        resolve = (Button) view.findViewById(R.id.details_fragment_resolve_btn);

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInfoWindowAdapter.removeDetailsFragment();
            }
            });

        final CommunityIssue issue = mInfoWindowAdapter.getIssue();

        resolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInfoWindowAdapter.removeDetailsFragment();
                mInfoWindowAdapter.resolveIssue(issue);

            }
        });

        issue.attachTranslators(view.getContext());

        Bitmap bitmap = issue.getIssueImage().getBitmap();
        if(bitmap != null) detailsImage.setImageBitmap(bitmap);
        destriptionLong.setText(issue.getLong_description());
        destriptionShort.setText(issue.getShort_description());
        category.setText(issue.getCategory().toString());
        tags.setText(issue.getTag().toString());
        time.setText(issue.getTime().toString());


        return view;


    }

}
