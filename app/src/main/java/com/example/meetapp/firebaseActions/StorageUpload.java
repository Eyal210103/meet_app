package com.example.meetapp.firebaseActions;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.meetapp.callbacks.PhotoUploadCompleteListener;
import com.example.meetapp.callbacks.PhotoUploadErrorListener;
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
    private static final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();


    public static void uploadGroupImage(final Fragment context, final String groupId , Uri data){
                reference.child(FirebaseTags.STORAGE_GROUP_IMAGE).child(groupId).putFile(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            reference.child(FirebaseTags.STORAGE_GROUP_IMAGE).child(groupId).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()){
                                        database.getReference().child(FirebaseTags.GROUPS_CHILDES).child(groupId)
                                                .child(FirebaseTags.GROUPS_PHOTO_URL_CHILD).setValue(task.getResult().toString());
                                        PhotoUploadCompleteListener photoUploadCompleteListener = (PhotoUploadCompleteListener)context;
                                        photoUploadCompleteListener.onPhotoUploadComplete();
                                    }else {
                                        reference.child(FirebaseTags.STORAGE_GROUP_IMAGE).child(groupId).delete();
                                        PhotoUploadErrorListener photoUploadErrorListener = (PhotoUploadErrorListener)context;
                                        photoUploadErrorListener.onPhotoUploadError();
                                    }
                                }
                            });
                        }
                    }
                });
    }

    public static void uploadChatImage(final Fragment context, final String groupId , String messageId , Uri data){
        reference.child(FirebaseTags.STORAGE_CHAT_IMAGES).child(groupId).child(messageId)
                .putFile(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    reference.child(FirebaseTags.STORAGE_CHAT_IMAGES).child(groupId).child(messageId)
                            .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                database.getReference().child(FirebaseTags.STORAGE_GROUP_CHAT).child(groupId).child(FirebaseTags.STORAGE_CHAT).child(messageId).child(FirebaseTags.CHAT_PHOTO_URL).setValue(task.getResult().toString());
//                                PhotoUploadCompleteListener photoUploadCompleteListener = (PhotoUploadCompleteListener)context; TODO
//                                photoUploadCompleteListener.onPhotoUploadComplete();
                            }else {
                                reference.child(FirebaseTags.STORAGE_CHAT_IMAGES).child(groupId).child(messageId).delete();
//                                PhotoUploadErrorListener photoUploadErrorListener = (PhotoUploadErrorListener)context;
//                                photoUploadErrorListener.onPhotoUploadError();
                            }
                        }
                    });
                }
            }
        });
    }

    public static void uploadProfileImage(final Fragment context, final String id , Uri data){
        reference.child(FirebaseTags.STORAGE_PROFILE_IMAGES).child(id).putFile(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    reference.child(FirebaseTags.STORAGE_PROFILE_IMAGES).child(id).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                database.getReference().child(FirebaseTags.USER_CHILDES).child(id).child(FirebaseTags.USER_PHOTO_URL_CHILD).setValue(task.getResult().toString());
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
                                            reference.child(FirebaseTags.STORAGE_PROFILE_IMAGES).child(id).delete();
                                            PhotoUploadErrorListener photoUploadErrorListener = (PhotoUploadErrorListener)context;
                                            photoUploadErrorListener.onPhotoUploadError();
                                        }
                                    });
                                }

                            }else {
                                reference.child(FirebaseTags.STORAGE_PROFILE_IMAGES).child(id).delete();
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
