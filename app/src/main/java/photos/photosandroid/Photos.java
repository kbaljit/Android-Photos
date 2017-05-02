package photos.photosandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

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
    PhotoLibrary photolib = new PhotoLibrary();
    PhotoLibrary searches = new PhotoLibrary();
    ArrayAdapter<String> adapter;
    final Context context=this;
    private String title="";
    private String rename="";
    private Album temp;
    ArrayList<Photo> matches;

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
        final ArrayList<Album> albums=photolib.getAlbums();
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
        ImageButton searchButton=(ImageButton) findViewById(R.id.searchButton);

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

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                LinearLayout lila1= new LinearLayout(context);
                lila1.setOrientation(LinearLayout.VERTICAL);
                builder.setTitle("Enter Tag Name and Value");
                final EditText type=new EditText(context);
                type.setHint("Name");
                final EditText value=new EditText(context);
                value.setHint("Value");
                lila1.addView(type);
                lila1.addView(value);
                builder.setView(lila1);

                builder.setPositiveButton("SEARCH", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        String typeS = type.getText().toString();
                        String valueS = value.getText().toString();

                        if(!((typeS.equalsIgnoreCase("location")) || (typeS.equalsIgnoreCase("person")))){
                            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Incorrect Tag Name. Please enter a location or person tag.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }else if((typeS.equals("")) || (valueS.equals(""))){
                            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Tag name and/or value is empty. Try Again.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }else{
                            matches = new ArrayList<>();
                            for(int i = 0; i < photolib.getAlbums().size(); i++){
                                for(int j = 0; j < photolib.getAlbums().get(i).getPhotos().size(); j++){
                                    for(int k = 0; k < photolib.getAlbums().get(i).getPhotos().get(j).getTags().size(); k++){
                                        String t = photolib.getAlbums().get(i).getPhotos().get(j).getTags().get(k).getTagName();
                                        String v = photolib.getAlbums().get(i).getPhotos().get(j).getTags().get(k).getTagValue();
                                        if(t.equalsIgnoreCase(typeS) && v.equalsIgnoreCase(valueS)){
                                            matches.add(photolib.getAlbums().get(i).getPhotos().get(j));
                                        }else if(t.equalsIgnoreCase(typeS) && v.toLowerCase().contains(valueS.toLowerCase())){
                                            matches.add(photolib.getAlbums().get(i).getPhotos().get(j));
                                        }
                                    }
                                }
                            }

                            if(matches.size() == 0){
                                Toast.makeText(Photos.this, "No matches found.",
                                        Toast.LENGTH_LONG).show();
                            }else{
                                Album mFound = new Album("Matches_lastSearch");
                                for(int a = 0; a < matches.size(); a++){
                                    mFound.addPhoto(matches.get(a));
                                }
                                final GridView gridView = new GridView(context);
                                final ImageAdapter ia = new ImageAdapter(context);
                                gridView.setAdapter(ia);
                                gridView.setNumColumns(3);
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Bundle bundle=new Bundle();
                                        bundle.putInt("GRID_POS", position);
                                        bundle.putSerializable("LIBRARY", photolib);
                                        bundle.putString("ALBUM_NAME", "Matches_lastSearch");
                                        Intent intent=new Intent(context, DisplaySearches.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        gridView.invalidateViews();
                                        ia.notifyDataSetChanged();
                                    }
                                });

                                AlertDialog.Builder b = new AlertDialog.Builder(context);
                                b.setView(gridView);
                                b.setTitle("Matches Found");
                                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                b.show();
                            }
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
            Intent intent=new Intent(this, AlbumActivity.class);
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

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context cont, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = cont.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public String getPath(Uri uri) {
        if( uri == null ) {
            return null;
        }

        if (isMediaDocument(uri)){
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            final String selection = "_id=?";
            final String[] selectionArgs = new String[] {
                    split[1]
            };

            return getDataColumn(context, contentUri, selection, selectionArgs);
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }

        return null;
    }

    private class ImageAdapter extends BaseAdapter {
        private Context context;

        public ImageAdapter(Context c) {
            this.context = c;
        }

        public int getCount() {
            return matches.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(context);
                Uri uri;
                String uriString=matches.get(position).getImage();
                uri=Uri.parse(uriString);
                String filePath=getPath(uri);
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                imageView.setImageBitmap(bitmap);
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            return imageView;
        }
    }
}
