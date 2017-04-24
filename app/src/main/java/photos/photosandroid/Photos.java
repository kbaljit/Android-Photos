package photos.photosandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

public class Photos extends AppCompatActivity {
    ListView albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        PhotoLibrary photolib = null;
        try {
            photolib = PhotoLibrary.readApp();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        albumList= (ListView) findViewById(R.id.AlbumList);
        ArrayList<Album> albums=photolib.Albums;
        ArrayList<String> albumNames=new ArrayList<>();
        for(int i=0; i<albums.size(); i++){
            albumNames.add(albums.get(i).getTitle());
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, R.layout.activity_photos, albumNames);
        albumList.setAdapter(adapter);


        ImageButton addButton=(ImageButton) findViewById(R.id.addButton);
        ImageButton deleteButton=(ImageButton) findViewById(R.id.deleteButton);
        ImageButton renameButton=(ImageButton) findViewById(R.id.renameButton);

        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

            }
        });

        renameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

            }
        });
    }
}
