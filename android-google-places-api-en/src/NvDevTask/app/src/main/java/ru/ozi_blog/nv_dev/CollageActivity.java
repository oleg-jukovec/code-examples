package ru.ozi_blog.nv_dev;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

/**
 * The activity shows collage image,has the share floating action button
 * and back button on action bar.
 */
public class CollageActivity extends AppCompatActivity {
    // tag for logging
    private static final String  TAG = CollageActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // creation activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // getting collage image
        Intent intent = getIntent();
        final Uri mImageUri = Uri.parse(intent.getStringExtra(Intent.EXTRA_STREAM));
        Log.d(TAG, "Gotten collage uri: " + mImageUri.toString());
        Bitmap collage = null;
        try {
            collage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
        } catch (IOException e) {
            Log.d(TAG, "Getting collage image error.");
        }
        // set collage image
        ImageView result = (ImageView)findViewById(R.id.imageView);
        result.setImageBitmap(collage);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.collage_fab);
        // set on click floating action button listener
        fab.setOnClickListener(new View.OnClickListener() {
            // share image
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Sharing collage.");
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                // image type
                sharingIntent.setDataAndType(mImageUri, "image/png");
                // send uri
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, mImageUri);
                // start sharing intent
                startActivity(Intent.createChooser(sharingIntent,
                        getString(R.string.collage_activity_share_title)));
            }
        });
        // activate action bar
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
