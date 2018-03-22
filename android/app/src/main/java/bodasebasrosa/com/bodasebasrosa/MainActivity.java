package bodasebasrosa.com.bodasebasrosa;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private final long timestamp = System.currentTimeMillis() / 1000;
    private final int random = new Random().nextInt(999999);
    private final String fileName = timestamp + "_" + random + ".jpg";

    private final String FIREBASE_USER = "admin@admin.com";
    private final String FIREBASE_PASS = "admin12345678";
    private final String FIREBASE_FOLDER = "images";

    private Image image;

    private ProgressBar progressBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView);

        checkFirebaseUser();
    }

    private void checkFirebaseUser(){
        textView.setText("Validando al usuario...");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            uploadShareStreamImageFirebase();
        }else{
            mAuth.signInWithEmailAndPassword(FIREBASE_USER, FIREBASE_PASS)
                    .addOnCompleteListener(this, signInListener);
        }
    }

     OnCompleteListener<AuthResult> signInListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (!task.isSuccessful()){
                Toast.makeText(MainActivity.this, "Error de acceso", Toast.LENGTH_SHORT).show();
                finish();
            }

            uploadShareStreamImageFirebase();
        }
    };

    private Bitmap getValidBitmap(Uri uri ){

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bmp1 = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap bmp2 = ImageUtils.rotateBitmap(bmp1,orientation);
            Bitmap bmp3 = ImageUtils.resize(bmp2,1080, 1080);

            bmp1.recycle();
            bmp2.recycle();

            return bmp3;
        } catch (Exception e) {
            return null;
        }
    }

    private void uploadShareStreamImageFirebase(){

        textView.setText("Subiendo la imagen...");
        try {

            Uri uri = ShareCompat.IntentReader.from(this).getStream();
            Bitmap bitmap = getValidBitmap(uri);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
            byte[] bitmapdata = bos.toByteArray();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bitmapdata);

            image = new Image();
            image.setHeight(bitmap.getHeight());
            image.setWidth(bitmap.getWidth());

            String child = File.separator + FIREBASE_FOLDER + File.separator + fileName;
            StorageReference previewRef = storageRef.child(child);
            UploadTask uploadTask = previewRef.putStream(inputStream);
            uploadTask
                    .addOnFailureListener(uploadImageFailureListener)
                    .addOnProgressListener(uploadImageProgressListener)
                    .addOnSuccessListener(uploadImageSuccessListener);
        } catch (Exception e) {
            Toast.makeText(this, "Imposible recuperar la imagen", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private OnFailureListener uploadImageFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(MainActivity.this, "Fallo al subir la imagen", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private OnProgressListener<UploadTask.TaskSnapshot> uploadImageProgressListener = new OnProgressListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

        }
    };

    private OnSuccessListener<UploadTask.TaskSnapshot> uploadImageSuccessListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            if (taskSnapshot == null || taskSnapshot.getDownloadUrl() == null){
                Toast.makeText(MainActivity.this, "Imposible subir la imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            image.setSrc(taskSnapshot.getDownloadUrl().toString());

            final DatabaseReference ref = mDatabase.child(FIREBASE_FOLDER).push();
            ref.setValue(image);

            Toast.makeText(MainActivity.this, "Imagen compartida", Toast.LENGTH_SHORT).show();
            finish();
        }
    };
}
