package photos.photosandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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

public class displayPhoto extends AppCompatActivity{
    TableLayout TTable;
    ImageView ImgView;
    PhotoLibrary photoLib= new PhotoLibrary();
    final Context context=this;
    String testDecode="test";
    Photo photo;
    int pos;
    String AlbumName;


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
            photo=findPhotoInAlbum(AlbumName, pos);

            setTagView(photo);
            setImageView(photo);

            Button addTag=(Button) findViewById(R.id.addtag);
            Button deleteTag=(Button) findViewById(R.id.deletetag);

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
                                boolean duplicate=false;
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

    public Photo findPhotoInAlbum(String AlbumName, int pos){
        for(int i=0; i<photoLib.getAlbums().size(); i++){
            if(photoLib.getAlbums().get(i).getTitle().equals(AlbumName)){
                return photoLib.getAlbums().get(i).getPhotos().get(pos);
            }
        }
        return null;
    }

    public void setPhotoInAlbum(Photo P){
        for(int i=0; i<photoLib.getAlbums().size(); i++){
            if(photoLib.getAlbums().get(i).getTitle().equals(AlbumName)){
                photoLib.getAlbums().get(i).getPhotos().set(pos, P);
            }
        }

    }

    public void setTagView(final Photo P){
        ArrayList<Tag> tags=P.getTags();
        TTable=(TableLayout) findViewById(R.id.TagTable);
        TTable.removeAllViews();
        for(int i=0; i<tags.size();i++){
            TableRow row=new TableRow(this);
            String Name=tags.get(i).getTagName();
            String Value=tags.get(i).getTagValue();
            TextView nameView=new TextView(this);
            nameView.setText(Name+"  ");
            TextView valueView=new TextView(this);
            valueView.setText("  "+Value);
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
                    TextView valueView=(TextView) tempRow.getChildAt(1);
                    String tagName=nameView.getText().toString();
                    String tagValue=valueView.getText().toString();

                    Log.d("Debug", tagName + " "+ tagValue);

                    for(int i=0; i<temp.getTags().size();i++){
                        if((tagName.trim().equals(temp.getTags().get(i).getTagName())) && (tagValue.trim().equals(temp.getTags().get(i).getTagValue()))){
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
            row.addView(valueView);
            row.addView(tagDelete);
            TTable.addView(row);
        }

    }
    public void setImageView(Photo P){
        Uri uri=Uri.parse(P.getImage());
        String filePath=getPath(uri);
        Bitmap bitmap =BitmapFactory.decodeFile(filePath);
        ImgView.setImageBitmap(bitmap);

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
