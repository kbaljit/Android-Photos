package photos.photosandroid;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class DisplayAlbum extends AppCompatActivity {
    PhotoLibrary photolib=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album);

        Intent intent=getIntent();
        Bundle b=intent.getExtras();
        String Album_Name=b.getString("ALBUM_NAME");
        photolib=(PhotoLibrary)b.getSerializable("PHOTO_LIB");




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
        ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(F));
        PhotoLibrary photoLib = (PhotoLibrary)ois.readObject();
        ois.close();
        return photoLib;
    }
}
