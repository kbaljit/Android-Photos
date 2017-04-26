package photos.photosandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DisplayAlbum extends AppCompatActivity {
    PhotoLibrary photolib=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album);

        Intent intent=getIntent();
        Bundle b=intent.getExtras();
        String Album_Name=b.getString("ALBUM_NAME");
        photolib=(PhotoLibrary)b.getSerializable("PHOTO_LIB");




    }
}
