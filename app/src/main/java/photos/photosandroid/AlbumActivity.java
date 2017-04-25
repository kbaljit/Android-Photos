package photos.photosandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

/**
 * Created by Baljit on 4/24/2017.
 */

public class AlbumActivity extends AppCompatActivity{
    GridView photoList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album);

        photoList = (GridView) findViewById(R.id.gridview);
        //photoList.setAdapter(new ImageAdapter(AlbumActivity.this, photoList));


        photoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //open display code here
            }
        });
    }
}
