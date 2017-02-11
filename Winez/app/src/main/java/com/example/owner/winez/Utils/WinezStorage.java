package com.example.owner.winez.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ziv on 05/02/2017.
 */

public class WinezStorage {
    private static WinezStorage _instance;
    private FirebaseStorage mStorage;

    private WinezStorage(){
        this.mStorage = FirebaseStorage.getInstance();
    }

    public static WinezStorage getInstance(){
        if (_instance == null){
            _instance = new WinezStorage();
        }
        return _instance;
    }

    /**
     * Saves image to storage and cache
     * @param bitmap
     * @param path
     * @param onSaveCompleteListener
     */
    public void saveImage(final Bitmap bitmap, final String path, final OnSaveCompleteListener onSaveCompleteListener){
        final StorageReference ref = this.mStorage.getReference().child(path);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();

        // Saving to remote sotrage
        ref.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    onSaveCompleteListener.done();
                    saveImageToFile(bitmap,path);

                }else{
                    onSaveCompleteListener.failed();
                }
            }
        });
    }

    public interface OnSaveCompleteListener{
        void failed();
        void done();
    }

    /**
     * Gets an image according to path given
     * @param path
     * @param onGetBitmapListener
     */
    public void getImage(String path, final OnGetBitmapListener onGetBitmapListener){
        Tuple<Bitmap,Long> toRet = this.loadImageFromFile(path);
        if (toRet != null) {
            // Returning file in the meantime
            onGetBitmapListener.onResult(toRet.getT());

            // Returning newer image if there is one
            this.checkStorageForUpdate(path,toRet,onGetBitmapListener);
        } else {
            loadFileFromStorage(path, onGetBitmapListener);
        }
    }

    /**
     * Invokes with new image if there is one.
     * If not it dosent return an image
     * @param path
     * @param toCheck
     * @param onGetBitmapListener
     */
    private void checkStorageForUpdate(final String path, final Tuple<Bitmap, Long> toCheck, final OnGetBitmapListener onGetBitmapListener) {
        final StorageReference ref = this.mStorage.getReference().child(path);

        ref.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
            @Override
            public void onComplete(@NonNull Task<StorageMetadata> task) {
                if (task.isSuccessful()){
                    StorageMetadata meta = task.getResult();
                    if (meta.getUpdatedTimeMillis() > toCheck.getX()) {
                        getFromStorageUsingMetaData(meta, ref, new OnGetBitmapListener() {
                            @Override
                            public void onResult(Bitmap image) {
                                onGetBitmapListener.onResult(image);
                            }

                            @Override
                            public void onCancelled() {
                            }
                        }, path);
                    }
                }
            }
        });
    }

    private void loadFileFromStorage(final String path, final OnGetBitmapListener onGetBitmapListener) {
        final StorageReference ref = this.mStorage.getReference().child(path);

        // Getting image from storage
        // First checking image size
        ref.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
            @Override
            public void onComplete(@NonNull Task<StorageMetadata> task) {
                if (task.isSuccessful()){
                    StorageMetadata meta = task.getResult();
                    getFromStorageUsingMetaData(meta, ref, onGetBitmapListener, path);
                }else{
                    onGetBitmapListener.onCancelled();
                }
            }
        });
    }

    private void getFromStorageUsingMetaData(StorageMetadata meta, StorageReference ref, final OnGetBitmapListener onGetBitmapListener, final String path) {
        final long bytes = meta.getSizeBytes();
        // Getting image
        ref.getBytes(bytes).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()){
                    Bitmap bitmap = BitmapFactory
                            .decodeByteArray(task.getResult(),
                                    0,
                                    (int)bytes);
                    onGetBitmapListener.onResult(bitmap);
                    saveImageToFile(bitmap,path);
                }
                else{
                    onGetBitmapListener.onCancelled();
                }
            }
        });
    }

    /**
     * gets image from file. return null if not found
     * @param imageFileName
     * @return
     */
    private Tuple<Bitmap,Long> loadImageFromFile(String imageFileName) {
        Tuple<Bitmap,Long> toret = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir, imageFileName);
            if(imageFile.exists()) {
                long lastMod = imageFile.lastModified();
                InputStream inputStream = new FileInputStream(imageFile);
                toret = new Tuple<>(BitmapFactory.decodeStream(inputStream), lastMod);
                Log.d("tag", "got image from cache: " + imageFileName);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return toret;
    }


    private void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir,imageFileName);
            imageFile.createNewFile();

            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public interface OnGetBitmapListener{
        void onResult(Bitmap image);
        void onCancelled();
    }
}
