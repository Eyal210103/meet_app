package com.example.meetapp.firebaseActions;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.meetapp.uploadsListeners.PhotoUploadCompleteListener;
import com.example.meetapp.uploadsListeners.PhotoUploadErrorListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class StorageUpload {
    private static StorageReference reference = FirebaseStorage.getInstance().getReference();
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();


    public static void uploadGroupImage(final Context context, final String groupId , byte[] data){
                reference.child("Group").child(groupId).putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            reference.child("Group").child(groupId).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()){
                                        database.getReference().child("Groups").child(groupId).child("photoUrl").setValue(task.getResult().toString());
                                        PhotoUploadCompleteListener photoUploadCompleteListener = (PhotoUploadCompleteListener)context;
                                        photoUploadCompleteListener.onPhotoUploadComplete();
                                    }else {
                                        PhotoUploadErrorListener photoUploadErrorListener = (PhotoUploadErrorListener)context;
                                        photoUploadErrorListener.onPhotoUploadError();
                                    }
                                }
                            });
                        }
                    }
                });
    }
}
