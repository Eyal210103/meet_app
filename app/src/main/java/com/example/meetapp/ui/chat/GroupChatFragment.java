package com.example.meetapp.ui.chat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.databinding.GroupChatFragmentBinding;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class GroupChatFragment extends Fragment {

    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_PICTURE = 1;
    private static final int REQUEST_CAMERA = 103;

    private GroupChatViewModel mViewModel;
    ImageView imagePreview;
    private Uri imageUri = null;
    private Bitmap imageBitmap = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GroupChatViewModel.class);
        mViewModel.init(getArguments().getString(Const.BUNDLE_GROUP_ID));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        GroupChatFragmentBinding binding = GroupChatFragmentBinding.inflate(inflater,container,false);
        View view =binding.getRoot();

        final RecyclerView recyclerView = binding.chatRecyclerView;

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        final ChatAdapter adapter = new ChatAdapter(this,mViewModel.getMessages().getValue());
        recyclerView.setAdapter(adapter);

        final EditText contextET = binding.chatContextEt;
        ImageView cameraIV = binding.chatCameraIv;
        imagePreview = binding.selectedImageChatIv;

        binding.chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                message.setContext(contextET.getText().toString());
                message.setSenderDisplayName(CurrentUser.getInstance().getDisplayName());
                message.setSenderId(CurrentUser.getInstance().getId());
                Date currentTime = Calendar.getInstance().getTime();
                message.setTime(currentTime.getTime());
                if(imageUri != null){
                    //StorageUpload.uploadChatImage();
                    message.setUrl(imageUri.toString());
                    mViewModel.sendImageMessage(message);
                    imageUri = null;
                    imagePreview.setVisibility(View.GONE);
                }
                else if (imageBitmap != null){
                    mViewModel.sendCameraMessage(message,imageBitmap);
                    imageBitmap = null;
                    imagePreview.setVisibility(View.GONE);
                }
                else if (!contextET.getText().toString().matches("")){
                    mViewModel.sendMessage(message);
                }
                contextET.setText("");
                recyclerView.smoothScrollToPosition(adapter.getItemCount());

            }
        });

        cameraIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });

        mViewModel.getMessages().observe(getViewLifecycleOwner(), new Observer<ArrayList<LiveData<Message>>>() {
            @Override
            public void onChanged(ArrayList<LiveData<Message>> mutableLiveData) {
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(mutableLiveData.size()-1);
                for (LiveData<Message> m : mutableLiveData) {
                    if (!m.hasActiveObservers()) {
                        m.observe(getViewLifecycleOwner(), new Observer<Message>() {
                            @Override
                            public void onChanged(Message message) {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
        return view;
    }

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(requireActivity());
        myAlertDialog.setTitle(getString(R.string.upload_options));
        myAlertDialog.setMessage(getString(R.string.select_upload_method));

        myAlertDialog.setPositiveButton(getString(R.string.option_gallery),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pictureActionIntent, GALLERY_PICTURE);

                    }
                });

        myAlertDialog.setNegativeButton(getString(R.string.option_camera),new DialogInterface.OnClickListener() {
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
        }
        else if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            if (data != null) {
                imageBitmap = (Bitmap) data.getExtras().get("data");
                imagePreview.setVisibility(View.VISIBLE);
                imagePreview.setImageBitmap(imageBitmap);
            }
        }
    }
}