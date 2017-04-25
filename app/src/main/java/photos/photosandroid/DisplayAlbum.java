package photos.photosandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DisplayAlbum extends AppCompatActivity {
    PhotoLibrary photolib=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_album);


    }
}
