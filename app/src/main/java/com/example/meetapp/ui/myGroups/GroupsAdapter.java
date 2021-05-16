package com.example.meetapp.ui.myGroups;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.Group;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.meetapp.ui.groupInfo.GroupInfoFragment.getDominantColor;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder> {

    Fragment context;
    int type;
    ArrayList<LiveData<Group>> groups;

    public GroupsAdapter(Fragment context, ArrayList<LiveData<Group>> groups , int type) {
        this.context = context;
        this.groups = groups;
        this.type =type;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_adapter, parent, false);
        return new GroupsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {
        final Group current = groups.get(position).getValue();
        if (current != null) {
            holder.groupName.setText(current.getName());
            //Glide.with(context.requireActivity()).load(current.getPhotoUrl()).into(holder.groupImage);

            Glide.with(context.requireActivity()).load(current.getPhotoUrl()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }
                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();
                    int colorFromImg = getDominantColor(bitmap);
                    int[] colors = {context.requireContext().getColor(R.color.backgroundSec),colorFromImg,colorFromImg};

                    GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
                    gd.setCornerRadius(60);
                    holder.cardView.setBackground(gd);

                    return false;
                }
            }).into(holder.groupImage);

            Glide.with(context.requireActivity()).load(getSubjectIcon(current.getSubject())).into(holder.subject);
            if (type == Const.TYPE_MY_GROUPS) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Const.BUNDLE_GROUP_ID, current.getId());
                        final NavController navController = Navigation.findNavController(context.requireActivity(), R.id.nav_host_fragment);
                        navController.navigate(R.id.action_socialMenuFragment_to_groupInfoFragment, bundle);
                    }
                });
            }

            if (type == Const.TYPE_JOIN_GROUP) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Const.BUNDLE_GROUP_ID, current);
                        final NavController navController = Navigation.findNavController(context.requireActivity(), R.id.nav_host_fragment);
                        navController.navigate(R.id.action_joinGroupFragment_to_joinGroupDialog, bundle);
                    }
                });
            }
            holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add(position,0,0,context.getString(R.string.menu_option_leave_group)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            OnClickInRecyclerView onClickInRecyclerView = (OnClickInRecyclerView)context;
                            onClickInRecyclerView.onClickInRecyclerView(current.getId(), Const.ACTION_LEAVE,0);
                            return false;
                        }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    private int getSubjectIcon(String subject){
        switch (subject){
            case Const.SUBJECT_RESTAURANT:
                return R.drawable.restaurant;
            case Const.SUBJECT_BASKETBALL:
                return R.drawable.basketball;
            case Const.SUBJECT_SOCCER:
                return R.drawable.soccer;
            case Const.SUBJECT_FOOTBALL:
                return R.drawable.football;
            case Const.SUBJECT_VIDEO_GAMES:
                return R.drawable.videogame;
            case Const.SUBJECT_MEETING:
                return R.drawable.meetingicon;
            default:
                return R.drawable.groupsicon;
        }
    }

    class GroupsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView groupImage;
        TextView groupName;
        ImageView subject;
        CardView cardView;

        public GroupsViewHolder(@NonNull View itemView) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.groups_adapter_civ);
            groupName = itemView.findViewById(R.id.groups_adapter_group_name);
            subject = itemView.findViewById(R.id.groups_adapter_iv);
            cardView = itemView.findViewById(R.id.card_view_groups);
        }
    }
}
