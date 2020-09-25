package com.example.meetapp.firebaseActions;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.meetapp.uploadsListeners.PhotoUploadCompleteListener;
import com.example.meetapp.uploadsListeners.PhotoUploadErrorListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class StorageUpload {
    private static StorageReference reference = FirebaseStorage.getInstance().getReference();
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();


    public static void uploadGroupImage(final Fragment context, final String groupId , Uri data){
                reference.child("Groups Images").child(groupId).putFile(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            reference.child("Groups Images").child(groupId).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()){
                                        database.getReference().child("Groups").child(groupId).child("photoUrl").setValue(task.getResult().toString());
                                        PhotoUploadCompleteListener photoUploadCompleteListener = (PhotoUploadCompleteListener)context;
                                        photoUploadCompleteListener.onPhotoUploadComplete();
                                    }else {
                                        reference.child("Groups Images").child(groupId).delete();
                                        PhotoUploadErrorListener photoUploadErrorListener = (PhotoUploadErrorListener)context;
                                        photoUploadErrorListener.onPhotoUploadError();
                                    }
                                }
                            });
                        }
                    }
                });
    }

    public static void uploadProfileImage(final Fragment context, final String id , Uri data){
        reference.child("Profile Images").child(id).putFile(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    reference.child("Profile Images").child(id).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                database.getReference().child("Users").child(id).child("profileImageUrl").setValue(task.getResult().toString());
                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(Uri.parse(task.getResult().toString()))
                                        .build();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    user.updateProfile(profileUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            PhotoUploadCompleteListener photoUploadCompleteListener = (PhotoUploadCompleteListener)context;
                                            photoUploadCompleteListener.onPhotoUploadComplete();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            reference.child("Profile Images").child(id).delete();
                                            PhotoUploadErrorListener photoUploadErrorListener = (PhotoUploadErrorListener)context;
                                            photoUploadErrorListener.onPhotoUploadError();
                                        }
                                    });
                                }

                            }else {
                                reference.child("Profile Images").child(id).delete();
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
