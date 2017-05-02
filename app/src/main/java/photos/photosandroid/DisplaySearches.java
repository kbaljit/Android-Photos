package photos.photosandroid;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class DisplaySearches extends AppCompatActivity{
    TableLayout TTable;
    ImageView ImgView;
    PhotoLibrary photoLib= new PhotoLibrary();
    final Context context=this;
    Photo photo;
    int pos;
    String AlbumName;
    int photoCount;
    Album temp=new Album("temp");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searches);
        ImgView = (ImageView) findViewById(R.id.imageView);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        try {
            String path = context.getFilesDir() + "/" + "library.bin";
            photoLib = readApp(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        pos = b.getInt("GRID_POS");
        AlbumName = b.getString("ALBUM_NAME");
        photo = photoLib.getLastSearch().getPhotos().get(pos);
        photoCount = b.getInt("PHOTO_NUM");
        setTagView(photo);
        setImageView(photo);
        temp = photoLib.getLastSearch();


        Button nextPhoto = (Button) findViewById(R.id.Next);
        Button previousPhoto = (Button) findViewById(R.id.Previous);

        nextPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos == temp.getNumPhotos() - 1) {
                    pos = 0;
                    Photo next = temp.getPhotos().get(pos);
                    setImageView(next);
                    setTagView(next);
                    photo = next;
                } else {
                    pos++;
                    Photo next = temp.getPhotos().get(pos);
                    setImageView(next);
                    setTagView(next);
                    photo = next;
                }
                AlbumName = temp.getTitle();


            }
        });

        previousPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos == 0) {
                    pos = temp.getNumPhotos() - 1;
                    Photo next = temp.getPhotos().get(pos);
                    setImageView(next);
                    setTagView(next);
                    photo = next;
                } else {
                    pos--;
                    Photo next = temp.getPhotos().get(pos);
                    setImageView(next);
                    setTagView(next);
                    photo = next;
                }
                AlbumName = temp.getTitle();


            }
        });
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
            nameView.setText(Name);
            TextView spacing=new TextView(this);
            spacing.setText("   ");
            TextView valueView=new TextView(this);
            valueView.setText(Value);
            row.addView(nameView);
            row.addView(spacing);
            row.addView(valueView);
            TTable.addView(row);
        }

    }

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
