package photos.photosandroid;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;

import static photos.photosandroid.AlbumActivity.getDataColumn;
import static photos.photosandroid.AlbumActivity.isGooglePhotosUri;
import static photos.photosandroid.AlbumActivity.isMediaDocument;

/**
 * @author Milan Patel
 * Displays Photo once selected from album
 */
public class displayPhoto extends AppCompatActivity{
    TableLayout TTable;
    ImageView ImgView;
    PhotoLibrary photoLib= new PhotoLibrary();
    final Context context=this;
    String testDecode="test";
    Photo photo;
    int pos;
    String AlbumName;
    int photoCount;
    Album temp=new Album("temp");

    /**
     * Creates Photo Display View
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.photo);
            ImgView=(ImageView) findViewById(R.id.imageView);
            Intent intent=getIntent();
            Bundle b=intent.getExtras();
        try {
            String path=context.getFilesDir()+"/"+"library.bin";
            photoLib = readApp(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

            pos=b.getInt("GRID_POS");
            AlbumName=b.getString("ALBUM_NAME");
            photo =findPhotoInAlbum(AlbumName, pos);
            String s=photo.getTitle();
            String FileName="";
            if(Uri.parse(photo.getImage())==null){
                FileName="No Title";
            }
            else{
                FileName = s.substring(s.lastIndexOf("/") + 1);
            }

            getSupportActionBar().setTitle(FileName);
            photoCount=b.getInt("PHOTO_NUM");
            setTagView(photo);
            setImageView(photo);
        for(int i=0; i<photoLib.getAlbums().size(); i++){
            if(photoLib.getAlbums().get(i).getTitle().equals(AlbumName)){
                temp=photoLib.getAlbums().get(i);
            }
        }

            Button addTag=(Button) findViewById(R.id.addtag);
            Button nextPhoto=(Button) findViewById(R.id.Next);
            Button previousPhoto=(Button) findViewById(R.id.Previous);
            Button movePhoto=(Button) findViewById(R.id.move);

        movePhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final AlertDialog builder=new AlertDialog.Builder(context).create();
                ScrollView Scroller=new ScrollView(context);
                builder.setTitle("Select Album to Move Photo to");
                final LinearLayout LL= new LinearLayout(context);
                LL.setOrientation(LinearLayout.VERTICAL);
                Scroller.addView(LL);
                Button cancel=new Button(context);
                cancel.setText("Cancel");
                cancel.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
                for(int i=0; i<photoLib.getAlbums().size();i++) {
                    TextView text = new TextView(context);
                    text.setText(photoLib.getAlbums().get(i).getTitle());
                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView text=(TextView) v;
                            String moveAlbum=text.getText().toString();

                            for(int i=0; i<photoLib.getAlbums().size();i++){
                                if(moveAlbum.equals(photoLib.getAlbums().get(i).getTitle())){
                                    Photo P=photo;
                                    photoLib.getAlbums().get(i).addPhoto(P);
                                    int origPos=pos;

                                    if(getNumPhotos()==1){
                                        deletePhotoInAlbum(origPos);
                                        try {
                                            writeApp(photoLib, context);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Log.d("debug", "entered");
                                        Bundle send=new Bundle();
                                        Intent intent=new Intent(context, AlbumActivity.class);
                                        send.putString("ALBUM_NAME", AlbumName);
                                        send.putSerializable("PHOTO_LIB", photoLib );
                                        intent.putExtras(send);
                                        startActivity(intent);
                                        builder.dismiss();
                                        return;
                                    }

                                    deletePhotoInAlbum(origPos);
                                    try {
                                        writeApp(photoLib, context);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    builder.dismiss();
                                    Intent newIntent=getIntent();
                                    Bundle oldBundle=newIntent.getExtras();
                                    if(pos==0)
                                    oldBundle.putInt("GRID_POS", pos);
                                    else
                                    oldBundle.putInt("GRID_POS", pos-1);
                                    newIntent.putExtras(oldBundle);
                                    startActivity(newIntent);


                                }
                            }

                        }
                    });
                    if (photoLib.getAlbums().get(i).getTitle().equals(AlbumName)) {

                    } else {
                        LL.addView(text);
                    }
                }
                LL.addView(cancel);
                builder.setView(Scroller);
                builder.show();
            }
        });



        nextPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos==temp.getNumPhotos()-1){
                    pos=0;
                    Photo next=temp.getPhotos().get(pos);
                    String s=next.getTitle();
                    String FileName="";
                    if(Uri.parse(next.getImage())==null){
                        FileName="No Title";
                    }
                    else{
                        FileName = s.substring(s.lastIndexOf("/") + 1);
                    }

                    getSupportActionBar().setTitle(FileName);
                    setImageView(next);
                    setTagView(next);
                    photo=next;
                }
                else{
                    pos++;
                    Photo next=temp.getPhotos().get(pos);
                    String s=next.getTitle();
                    String FileName="";
                    if(Uri.parse(next.getImage())==null){
                        FileName="No Title";
                    }
                    else{
                        FileName = s.substring(s.lastIndexOf("/") + 1);
                    }

                    getSupportActionBar().setTitle(FileName);
                    setImageView(next);
                    setTagView(next);
                    photo=next;
                }
                AlbumName=temp.getTitle();


            }
        });

        previousPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos==0){
                    pos=temp.getNumPhotos()-1;
                    Photo next=temp.getPhotos().get(pos);
                    setImageView(next);
                    setTagView(next);
                    photo=next;
                }
                else{
                    pos--;
                    Photo next=temp.getPhotos().get(pos);
                    setImageView(next);
                    setTagView(next);
                    photo=next;
                }
                AlbumName=temp.getTitle();


            }
        });

        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Enter Tag Name and Tag Value");
                LinearLayout LL= new LinearLayout(context);
                LL.setOrientation(LinearLayout.VERTICAL);
                final EditText input=new EditText(context);
                input.setHint("Tag Name");
                final EditText input1=new EditText(context);
                input1.setHint("Tag Value");
                LL.addView(input);
                LL.addView(input1);
                builder.setView(LL);
                builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String tagName=input.getText().toString();
                                String tagValue=input1.getText().toString();
                                Log.d("debugging", tagName+" "+tagValue);
                                boolean duplicate=false;
                                if(!(tagName.equals("person") || tagName.equals("location"))){
                                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                    alertDialog.setTitle("Alert");
                                    alertDialog.setMessage("Invalid Tag, Try Again");
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    duplicate=true;
                                    alertDialog.show();
                                }
                                for(int i=0; i<photo.getTags().size();i++){
                                    if((tagName.equals(photo.getTags().get(i).getTagName())) && (tagValue.equals(photo.getTags().get(i).getTagValue()))){
                                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                        alertDialog.setTitle("Alert");
                                        alertDialog.setMessage("Duplicate Tag, please try again");
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
                                if(duplicate==false){
                                    photo.getTags().add(new Tag(tagName, tagValue));
                                    setPhotoInAlbum(photo);
                                    try {
                                        writeApp(photoLib, context);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    setTagView(photo);

                                }
                                else{}
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

    }

    /**
     * Finds Photo Given Album Name and grid position
     * @param AlbumName
     * @param pos
     * @return
     */
    public Photo findPhotoInAlbum(String AlbumName, int pos){
        for(int i=0; i<photoLib.getAlbums().size(); i++){
            if(photoLib.getAlbums().get(i).getTitle().equals(AlbumName)){
                return photoLib.getAlbums().get(i).getPhotos().get(pos);
            }
        }
        return null;
    }

    /**
     * Deletes Photo given grid position
     * @param pos
     */
    public void deletePhotoInAlbum(int pos) {
        for (int i = 0; i < photoLib.getAlbums().size(); i++) {
            if (photoLib.getAlbums().get(i).getTitle().equals(AlbumName)) {
                photoLib.getAlbums().get(i).getPhotos().remove(pos);
            }
        }
    }

    /**
     * Gets Number of Photos in album
     * @return
     */
    public int getNumPhotos(){
        for (int i = 0; i < photoLib.getAlbums().size(); i++) {
            if (photoLib.getAlbums().get(i).getTitle().equals(AlbumName)) {
               return photoLib.getAlbums().get(i).getPhotos().size();
            }
        }
        return 0;
    }

    /**
     * Sets Photo in album given Photo
     * @param P
     */
    public void setPhotoInAlbum(Photo P){
        for(int i=0; i<photoLib.getAlbums().size(); i++){
            if(photoLib.getAlbums().get(i).getTitle().equals(AlbumName)){
                photoLib.getAlbums().get(i).getPhotos().set(pos, P);
            }
        }

    }

    /**
     * Sets Table of Tag Values given Photo
     * @param P
     */
    public void setTagView(final Photo P){
        ArrayList<Tag> tags=P.getTags();
        TTable=(TableLayout) findViewById(R.id.TagTable);
        TTable.removeAllViews();
        for(int i=0; i<tags.size();i++){
            TableRow row=new TableRow(this);
            String Name=tags.get(i).getTagName();
            String Value=tags.get(i).getTagValue();
            TextView nameView=new TextView(this);
            nameView.setText(Name);
            TextView spacing=new TextView(this);
            spacing.setText("   ");
            TextView valueView=new TextView(this);
            valueView.setText(Value);
            Button tagDelete=new Button(this);
            tagDelete.setText("Delete");
            tagDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id=pos;
                    Photo temp=P;
                    View row=(View) v.getParent();
                    ViewGroup Container=((ViewGroup) row.getParent());
                    Container.removeView(row);
                    Container.invalidate();

                    TableRow tempRow=(TableRow) row;
                    TextView nameView=(TextView) tempRow.getChildAt(0);
                    TextView valueView=(TextView) tempRow.getChildAt(2);
                    String tagName=nameView.getText().toString();
                    String tagValue=valueView.getText().toString();

                    for(int i=0; i<temp.getTags().size();i++){
                        if((tagName.equals(temp.getTags().get(i).getTagName())) && (tagValue.equals(temp.getTags().get(i).getTagValue()))){
                            Log.d("Debug", temp.getTags().get(i).getTagName() + " "+ temp.getTags().get(i).getTagValue());
                            temp.getTags().remove(i);
                            setPhotoInAlbum(temp);
                            try {
                                writeApp(photoLib, context);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }



                }
            });
            row.addView(nameView);
            row.addView(spacing);
            row.addView(valueView);
            row.addView(tagDelete);
            TTable.addView(row);
        }

    }

    /**
     * Refreshes GridView of Parent Activity upon clicking back button
     * @param menuItem
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home: {
                Log.d("Debugger", "Back Button Refresh");
                Bundle send=new Bundle();
                Intent intent=new Intent(context, AlbumActivity.class);
                send.putString("ALBUM_NAME", AlbumName);
                send.putSerializable("PHOTO_LIB", photoLib );
                intent.putExtras(send);
                startActivity(intent);
                finish();
            }
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    /**
     * Sets Image View of Display Screen
     * @param P
     */
    public void setImageView(Photo P){
        Uri uri=Uri.parse(P.getImage());
        String filePath=getPath(uri);
        Bitmap bitmap =BitmapFactory.decodeFile(filePath);
        ImgView.setImageBitmap(bitmap);

    }

    /**
     * Gets file path of uri
     * @param uri
     * @return
     */
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

    /**
     * Serializes PhotoLibrary
     * @param photoLib
     * @param context
     * @throws IOException
     */
    public static void writeApp(PhotoLibrary photoLib, Context context) throws IOException {
        File outFile = new File(context.getFilesDir(), "library.bin");
        ObjectOutput oos = new ObjectOutputStream(new FileOutputStream(outFile));
        oos.writeObject(photoLib);
        oos.close();
    }

    /**
     * Reads Serialized Library
     * @param F
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
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
