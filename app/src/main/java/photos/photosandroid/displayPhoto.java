package photos.photosandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;

public class displayPhoto extends AppCompatActivity {
    TableLayout TTable;
    PhotoLibrary photoLib= new PhotoLibrary();
    final Context context=this;
    String testDecode="test";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.photo);

            /*Simply a test Photo, will not actually work. Previous Activity should create photos from Bitmap
            byte[] testPhoto=null;
            Photo test=new Photo(testPhoto);

            //After Selecting a Photo, populate a two column Table Layout with tag names and values
            ArrayList<Tag> tags=test.getTags();
            TTable=(TableLayout) findViewById(R.id.TagTable);
            for(int i=0; i<tags.size();i++){
                TableRow row=new TableRow(this);
                String Name=tags.get(i).getTagName();
                String Value=tags.get(i).getTagValue();
                TextView nameView=new TextView(this);
                nameView.setText(""+Name);
                TextView valueView=new TextView(this);
                valueView.setText(""+Value);
                row.addView(nameView);
                row.addView(valueView);
                TTable.addView(row);
            }*/



    }


}
