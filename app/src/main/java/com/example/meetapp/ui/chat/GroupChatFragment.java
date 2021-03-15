package com.example.meetapp.ui.chat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.model.Consts;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class GroupChatFragment extends Fragment {

    private static final String TAG = "CHAT";

    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private static final int REQUEST_CAMERA = 103;

    private GroupChatViewModel mViewModel;
    ImageView imagePreview;
    private Uri imageUri = null;

    public static GroupChatFragment newInstance() {
        return new GroupChatFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GroupChatViewModel.class);
        mViewModel.init(getArguments().getString(Consts.BUNDLE_GROUP_ID));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.group_chat_fragment, container, false);

        final RecyclerView recyclerView = view.findViewById(R.id.chat_recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        final ChatAdapter adapter = new ChatAdapter(this,mViewModel.getMessages().getValue());
        recyclerView.setAdapter(adapter);

        final EditText contextET = view.findViewById(R.id.chat_context_et);
        ImageView cameraIV = view.findViewById(R.id.chat_camera_iv);
        imagePreview = view.findViewById(R.id.selected_image_chat_iv);

        view.findViewById(R.id.chat_send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null){
                    //StorageUpload.uploadChatImage();
                    Message message = new Message();
                    message.setContext(contextET.getText().toString());
                    message.setSenderDisplayName(CurrentUser.getInstance().getDisplayName());
                    message.setSenderId(CurrentUser.getInstance().getId());
                    message.setUrl(imageUri.toString());
                    Date currentTime = Calendar.getInstance().getTime();
                    message.setTime(currentTime.getTime());
                    mViewModel.sendImageMessage(message);
                    contextET.setText("");
                    recyclerView.smoothScrollToPosition(adapter.getItemCount());
                    imageUri = null;
                    imagePreview.setVisibility(View.GONE);
                }
                else if (!contextET.getText().toString().matches("")){
                    Message message = new Message();
                    message.setContext(contextET.getText().toString());
                    message.setSenderDisplayName(CurrentUser.getInstance().getDisplayName());
                    message.setSenderId(CurrentUser.getInstance().getId());
                    Date currentTime = Calendar.getInstance().getTime();
                    message.setTime(currentTime.getTime());
                    mViewModel.sendMessage(message);
                    contextET.setText("");
                    recyclerView.smoothScrollToPosition(adapter.getItemCount());
                }
            }
        });

        cameraIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });

        mViewModel.getMessages().observe(getViewLifecycleOwner(), new Observer<ArrayList<MutableLiveData<Message>>>() {
            @Override
            public void onChanged(ArrayList<MutableLiveData<Message>> mutableLiveData) {
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
                for (MutableLiveData<Message> m : mutableLiveData) {
                    m.observe(getViewLifecycleOwner(), new Observer<Message>() {
                        @Override
                        public void onChanged(Message message) {
                            adapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(adapter.getItemCount());
                        }
                    });
                }
            }
        });
        return view;
    }


    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(requireActivity());
        myAlertDialog.setTitle("Upload Pictures Option");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pictureActionIntent, GALLERY_PICTURE);

                    }
                });

        myAlertDialog.setNegativeButton("Camera",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                        }else {
                            startActivityForResult(intent, CAMERA_REQUEST);
                        }
                    }
                });
        myAlertDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode ==GALLERY_PICTURE) {
            imageUri = data.getData();
            imagePreview.setVisibility(View.VISIBLE);
            imagePreview.setImageURI(imageUri);
            Log.d(TAG, "onActivityResult: " + imageUri + "_________");
        }
        else if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            if (data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imagePreview.setVisibility(View.VISIBLE);
                imagePreview.setImageBitmap(photo);
            }
        }
    }
}