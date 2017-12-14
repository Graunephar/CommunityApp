package com.dom.communityapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.dom.communityapp.models.CommunityIssue;
import com.dom.communityapp.models.IssueImage;
import com.dom.communityapp.storage.FirebaseDatabaseStorage;
import com.dom.communityapp.storage.FirebaseObserver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FetchIssueActivity extends AppCompatActivity implements FirebaseObserver {

    @BindView(R.id.fetch_imageView_pic)
    ImageView imgView;
    @BindView(R.id.fetch_txt_shortdescription)
    TextView txtShortDescription;
    @BindView(R.id.fetch_txt_view_longdescription)
    TextView txtLongDescription;
    @BindView(R.id.fetch_txt_view_tag)
    TextView txtTag;
    @BindView(R.id.fetch_txt_view_time)
    TextView txtTime;
    @BindView(R.id.fetch_txtview_category)
    TextView txtCategory;
    @BindView(R.id.fetch_txtview_id)
    TextView txtID;


    private FirebaseDatabaseStorage mFirebaseDatabaseStorage;
    private CommunityIssue mCurrentIssue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_issue);

        mFirebaseDatabaseStorage = new FirebaseDatabaseStorage(this);

        mFirebaseDatabaseStorage.addObserver(this);

        ButterKnife.bind(this);
    }


    @OnClick(R.id.fetch_btn)
    public void fetchTheStuff() {

    }

    @Override
    public void onDataChanged(String value) {

    }

    @Override
    public void getImage(Uri downloadUrl) {

    }

    @Override
    public void onNewIssue(CommunityIssue issue) {
        mCurrentIssue = issue;
        txtCategory.setText(issue.getCategory());
        txtTime.setText(issue.getTimed_duration());
        txtTag.setText(issue.getTag());
        txtShortDescription.setText(issue.getShort_description());
        txtLongDescription.setText(issue.getLong_description());

        IssueImage image = issue.issueImage;
        if (image.getBitmap() != null) { // Image is already downloaded
            updateView(image.getBitmap());
        }
    }

    @Override
    public void imageDownloaded(CommunityIssue issue) {

        if (issue.firebaseID.equals(mCurrentIssue.firebaseID) && issue.issueImage.getBitmap() != null) {
            updateView(issue.issueImage.getBitmap());
        }

    }

    private void updateView(Bitmap bitmap) {
        imgView.setImageBitmap(bitmap);
    }
}
