package photos.photosandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Photos extends AppCompatActivity implements Serializable {
    ListView albumList;
    PhotoLibrary photolib = null;
    ArrayAdapter<String> adapter;
    final Context context=this;
    private String title="";

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
        final ArrayList<Album> albums=photolib.Albums;
        final ArrayList<String> albumNames=new ArrayList<>();
        for(int i=0; i<albums.size(); i++){
            albumNames.add(albums.get(i).getTitle());
        }

        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, albumNames);
        albumList.setAdapter(adapter);

        albumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    displayAlbum(position);
            }
        });


        ImageButton addButton=(ImageButton) findViewById(R.id.addButton);
        ImageButton deleteButton=(ImageButton) findViewById(R.id.deleteButton);
        ImageButton renameButton=(ImageButton) findViewById(R.id.renameButton);

        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Enter Album Name");
                final EditText input=new EditText(context);
                builder.setView(input);

                builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        title=input.getText().toString();
                        boolean duplicate=false;
                        for(int i=0;i<albums.size();i++){
                            if(title.equals(albums.get(i).getTitle())){
                                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                alertDialog.setTitle("Alert");
                                alertDialog.setMessage("Duplicate Album Name, please try again");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                duplicate=true;
                                alertDialog.show();
                            }
                        }
                        if(duplicate==false) {
                            Album insert = new Album(title);
                            albums.add(insert);
                            photolib.setAlbums(albums);
                            Log.d("myTag", "Hello");
                            try {
                                PhotoLibrary.writeApp(photolib);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            albumNames.add(title);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });

                builder.show();
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
