package photos.photosandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

public class Photos extends AppCompatActivity {
    ListView albumList;
    PhotoLibrary photolib = null;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
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

        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, albumNames);
        albumList.setAdapter(adapter);

        albumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String entry=(String) parent.getAdapter().getItem(position);
                    displayAlbum(position);
            }
        });


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

    private void displayAlbum(int pos){
            Bundle bundle=new Bundle();
            String entry=(String) adapter.getItem(pos);
            bundle.putString("ALBUM_NAME", entry);
            bundle.putSerializable("PHOTO_LIB", photolib );
            Intent intent=new Intent(this, DisplayAlbum.class);
            intent.putExtras(bundle);
            startActivity(intent);


    }
}
