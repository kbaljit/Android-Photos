package photos.photosandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Photos extends AppCompatActivity implements Serializable {
    ListView albumList;
    PhotoLibrary photolib = null;
    ArrayAdapter<String> adapter;
    final Context context=this;
    private String title="";
    private String rename="";
    private Album temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        try {
            String path=context.getFilesDir()+"/"+"library.bin";
            photolib = readApp(new File(path));
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

        setListener();


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
                            try {
                                writeApp(photolib, context);
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

                albumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        albums.remove(position);
                        albumNames.remove(position);
                        adapter.notifyDataSetChanged();
                        photolib.setAlbums(albums);

                        try {
                            writeApp(photolib, context);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setListener();

                    }

                });
            }

        });

        renameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                albumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        temp=albums.get(position);

                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
                        builder.setTitle("Enter Album Name");
                        final EditText input=new EditText(context);
                        builder.setView(input);

                        builder.setPositiveButton("RENAME", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                rename=input.getText().toString();
                                temp.setTitle(rename);
                                albums.set(position, temp );
                                photolib.setAlbums(albums);
                                try {
                                    writeApp(photolib, context);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                albumNames.set(position, rename);
                                adapter.notifyDataSetChanged();

                            }
                        });

                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                dialog.cancel();
                            }
                        });

                        builder.show();
                        setListener();

                    }

                });

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

    public void setListener(){
        albumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayAlbum(position);
            }
        });
    }

    public static void writeApp(PhotoLibrary photoLib, Context context) throws IOException {
        File outFile = new File(context.getFilesDir(), "library.bin");
        ObjectOutput oos = new ObjectOutputStream(new FileOutputStream(outFile));
        oos.writeObject(photoLib);
        oos.close();
    }

    public static PhotoLibrary readApp(File F) throws IOException, ClassNotFoundException {
        if(F.length() == 0){
            return new PhotoLibrary();
        }
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(F));
        PhotoLibrary photoLib = (PhotoLibrary)ois.readObject();
        ois.close();
        return photoLib;
    }


}
