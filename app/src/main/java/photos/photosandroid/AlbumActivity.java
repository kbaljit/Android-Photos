package photos.photosandroid;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Baljit on 4/24/2017.
 */

public class AlbumActivity extends AppCompatActivity{
    GridView photoList;
    final Context context=this;
    ArrayList<Photo> photos;
    PhotoLibrary photolib=null;
    private String selectedImagePath;
    int p;
    ImageAdapter ia;
    Bundle b;
    Uri selectedImageUri;

    /**
     * Creates Thumbnail Grid of Photos
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album);

        Intent intent = getIntent();
        b = intent.getExtras();
        String Album_Name = b.getString("ALBUM_NAME");
        photolib = (PhotoLibrary) b.getSerializable("PHOTO_LIB");

        for (int i = 0; i < photolib.getAlbums().size(); i++) {
            if (photolib.getAlbums().get(i).getTitle().equals(Album_Name)) {
                photos = photolib.getAlbums().get(i).getPhotos();
                p = i;
            }
        }

        ImageButton addButton = (ImageButton) findViewById(R.id.addphoto);
        final ImageButton deleteButton = (ImageButton) findViewById(R.id.deletephoto);

        photoList = (GridView) findViewById(R.id.gridview);
        ia = new ImageAdapter(this);
        photoList.setAdapter(ia);

        //enable delete button when item is long clicked
        photoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                final int position = pos;
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        photolib.getAlbums().get(p).getPhotos().remove(position);
                        photos = photolib.getAlbums().get(p).getPhotos();
                        photoList.setAdapter(ia);
                        try {
                            writeApp(photolib, context);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        deleteButton.setVisibility(View.INVISIBLE);
                        ia.notifyDataSetChanged();
                    }
                });
                return true;
            }
        });

        photoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //open display code here
                Bundle bundle=new Bundle();
                bundle.putInt("GRID_POS", position);
                bundle.putSerializable("LIBRARY", photolib);
                bundle.putString("ALBUM_NAME", b.getString("ALBUM_NAME"));
                Intent intent=new Intent(context, displayPhoto.class);
                intent.putExtras(bundle);
                startActivity(intent);
                photoList.invalidateViews();
                ia.notifyDataSetChanged();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        0);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), 1);

                photoList.setAdapter(ia);
                photoList.invalidateViews();
                ia.notifyDataSetChanged();
            }
        });

        if(photos.size() > 0) {
            Toast.makeText(AlbumActivity.this, "Long click a photo to enable the delete button.",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Upon Selecting a Photo from Gallery, createa and adds photo instance to library
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1 && data != null && data.getData() != null) {
                selectedImageUri = data.getData();
                String stringUri=selectedImageUri.toString();
                Photo photo = new Photo(stringUri);
                photo.setTitle(getPath(selectedImageUri));
                photos.add(photo);
                photoList.setAdapter(ia);
                photoList.invalidateViews();
                ia.notifyDataSetChanged();
                try {
                    writeApp(photolib, context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        photoList.setAdapter(ia);
        photoList.invalidateViews();
        ia.notifyDataSetChanged();
    }

    /**
     * Retrives Path of uri file
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
     * Checks if uri is of a Media Document
     * @param uri
     * @return
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Checks if uri is a google photo
     * @param uri
     * @return
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Retrives Data Column of Photo by uri
     * @param cont
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
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

    /**
     * Serializes Photo Library
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
     * Image Adapter Class
     * @author Baljit Kaur
     */
    private class ImageAdapter extends BaseAdapter {
        private Context context;

        public ImageAdapter(Context c) {
            this.context = c;
        }

        public int getCount() {
            return photos.size();
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
                String uriString=photos.get(position).getImage();
                uri=Uri.parse(uriString);
                String filePath=getPath(uri);
                Bitmap bitmap =BitmapFactory.decodeFile(filePath);
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
