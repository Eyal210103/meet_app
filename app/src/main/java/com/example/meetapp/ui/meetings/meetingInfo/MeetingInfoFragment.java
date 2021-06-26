package com.example.meetapp.ui.meetings.meetingInfo;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.meetapp.R;
import com.example.meetapp.databinding.MeetingInfoFragmentBinding;
import com.example.meetapp.firebaseActions.FirebaseTags;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.meetings.WhoComingAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.meetapp.ui.groupInfo.GroupInfoFragment.getDominantColor;

public class MeetingInfoFragment extends Fragment {

    private MeetingInfoViewModel mViewModel;
    private String type;
    private MeetingInfoFragmentBinding binding;
    private MapView mapView;
    private GoogleMap mMap;
    private boolean isUserAlreadyIn ,displayGroupInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MeetingInfoViewModel.class);
        String id = getArguments().getString(Const.BUNDLE_ID);
        type = getArguments().getString(Const.BUNDLE_TYPE);
        displayGroupInfo = getArguments().getBoolean(Const.MEETING_TYPE_GROUP,true);
        String groupId = getArguments().getString(Const.BUNDLE_GROUP_ID, "");
        mViewModel.init(id, groupId, type);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MeetingInfoFragmentBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = binding.meetingParticipantsRecyclerView;
        WhoComingAdapter adapter = new WhoComingAdapter(requireActivity(), mViewModel.getUsers().getValue());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        mapView = binding.mapViewMeetingInfo;

        initGoogleMap(savedInstanceState);

        mViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                adapter.notifyDataSetChanged();
                for (User u : users) {
                    if (u.getId().equals(CurrentUser.getInstance().getId())) {
                        isUserAlreadyIn = true;
                        toggleIsUserIn();
                        return;
                    }
                }
                isUserAlreadyIn = false;
            }
        });

        if (type.equals(Const.MEETING_TYPE_PUBLIC)) {
            mViewModel.getPublicM().observe(getViewLifecycleOwner(), new Observer<Meeting>() {
                @Override
                public void onChanged(Meeting meeting) {
                    updateUI(meeting);
                }
            });
        } else if (type.equals(Const.MEETING_TYPE_GROUP)) {
            mViewModel.getGroupM().observe(getViewLifecycleOwner(), new Observer<GroupMeeting>() {
                @Override
                public void onChanged(GroupMeeting groupMeeting) {
                    updateUI(groupMeeting);
                }
            });
        }

        if (type.equals(Const.MEETING_TYPE_GROUP) && displayGroupInfo) {
            mViewModel.getGroupData().observe(getViewLifecycleOwner(), new Observer<Group>() {
                @Override
                public void onChanged(Group group) {
                    updateGroupDataUI(group);
                }
            });
        }
        binding.imComingButtonMeetingInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserAlreadyIn) {
                    if (type.equals(Const.MEETING_TYPE_PUBLIC)) {
                        Meeting meeting = mViewModel.getPublicM().getValue();
                        CurrentUser.quitMeeting(meeting.getId(), FirebaseTags.PUBLIC_MEETINGS_CHILDES);
                        meeting.deleteUserArrival(CurrentUser.getInstance().getId());
                    } else if (type.equals(Const.MEETING_TYPE_GROUP)) {
                        GroupMeeting meeting = mViewModel.getGroupM().getValue();
                        CurrentUser.quitMeeting(meeting.getId(), FirebaseTags.GROUP_MEETINGS_CHILDES);
                        meeting.deleteUserArrival(CurrentUser.getInstance().getId());
                    }
                    isUserAlreadyIn= false;
                    binding.imComingButtonMeetingInfo.setText(getString(R.string.confirm_arrival));
                } else {
                    if (type.equals(Const.MEETING_TYPE_PUBLIC)) {
                        Meeting meeting = mViewModel.getPublicM().getValue();
                        CurrentUser.joinMeeting(meeting.getId(), FirebaseTags.PUBLIC_MEETINGS_CHILDES, meeting.getId());
                        meeting.confirmUserArrival(CurrentUser.getInstance().getId());
                    } else if (type.equals(Const.MEETING_TYPE_GROUP)) {
                        GroupMeeting meeting = mViewModel.getGroupM().getValue();
                        CurrentUser.joinMeeting(meeting.getId(), FirebaseTags.GROUP_MEETINGS_CHILDES, mViewModel.getGroupId());
                        meeting.confirmUserArrival(CurrentUser.getInstance().getId());
                    }
                    isUserAlreadyIn= true;
                    binding.imComingButtonMeetingInfo.setText(getString(R.string.user_is_not_coming));
                    adapter.notifyDataSetChanged();
                }
                //adapter.notifyDataSetChanged();
            }
        });

        return binding.getRoot();
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(10f));
                } else {
                    Toast.makeText(requireActivity(), "Map Error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateUI(Meeting meeting) {
        TextView day = binding.meetingInfoTvDayOfMonthCalendarItem;
        TextView dayOfWeek = binding.meetingInfoTvDayOfWeekCalendarItem;
        TextView month = binding.meetingInfoTvDayCalendarItem;
        TextView hour = binding.meetingInfoHourTextView;
        TextView location = binding.meetingInfoLocationTextView;
        TextView description = binding.meetingInfoDescTextView;
        ImageView subjectImageView = binding.meetingInfoSubjectIv;

        Date date = new Date(meeting.getMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        day.setText("" + calendar.get(Calendar.DAY_OF_MONTH));
        dayOfWeek.setText(getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
        month.setText(getMonth(calendar.get(Calendar.MONTH)));
        hour.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        description.setText(meeting.getDescription());
        location.setText(getAddress(meeting.getLocation()));
        subjectImageView.setImageResource(getSubjectIcon(meeting.getSubject()));
        mMap.addMarker(new MarkerOptions().position(new LatLng(meeting.getLatitude(), meeting.getLongitude())));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(meeting.getLatitude(), meeting.getLongitude())));
        mMap.moveCamera(CameraUpdateFactory.zoomIn());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(meeting.getLatitude(), meeting.getLongitude())));
        //mMap.getUiSettings().setAllGesturesEnabled(false);
    }

    private void updateGroupDataUI(Group group) {
        binding.meetingInfoGroupInfo.setVisibility(View.VISIBLE);
        Glide.with(this).load(group.getPhotoUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();
                int colorFromImg = getDominantColor(bitmap);
                int[] colors = {requireContext().getColor(R.color.backgroundSec),colorFromImg,colorFromImg};

                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
                gd.setCornerRadius(40);
                binding.meetingInfoGroupInfo.setBackground(gd);

                return false;
            }
        }).into(binding.meetingInfoGroupCiv);
        binding.meetingInfoGroupName.setText(group.getName());
    }

    private void toggleIsUserIn() {
        Button button = binding.imComingButtonMeetingInfo;
        if (isUserAlreadyIn)
            button.setText(getString(R.string.user_is_coming));
        else
            button.setText(getString(R.string.confirm_arrival));
    }

    public String getDayOfWeek(int day) {
        switch (day) {
            case android.icu.util.Calendar.SUNDAY:
                return getString(R.string.days_sunday);

            case android.icu.util.Calendar.MONDAY:
                return getString(R.string.days_monday);

            case android.icu.util.Calendar.TUESDAY:
                return getString(R.string.days_tuesday);

            case android.icu.util.Calendar.WEDNESDAY:
                return getString(R.string.days_wednesday);

            case android.icu.util.Calendar.THURSDAY:
                return getString(R.string.days_thursday);

            case android.icu.util.Calendar.FRIDAY:
                return getString(R.string.days_friday);

            case android.icu.util.Calendar.SATURDAY:
                return getString(R.string.days_saturday);
            default:
                return "";
        }
    }

    public String getMonth(int day) {
        switch (day) {
            case android.icu.util.Calendar.JANUARY:
                return getString(R.string.months_january);

            case android.icu.util.Calendar.FEBRUARY:
                return getString(R.string.months_february);

            case android.icu.util.Calendar.MARCH:
                return getString(R.string.months_march);

            case android.icu.util.Calendar.APRIL:
                return getString(R.string.months_april);

            case android.icu.util.Calendar.MAY:
                return getString(R.string.months_may);

            case android.icu.util.Calendar.JUNE:
                return getString(R.string.months_june);

            case android.icu.util.Calendar.JULY:
                return getString(R.string.months_july);

            case android.icu.util.Calendar.AUGUST:
                return getString(R.string.months_august);

            case android.icu.util.Calendar.SEPTEMBER:
                return getString(R.string.months_september);

            case android.icu.util.Calendar.OCTOBER:
                return getString(R.string.months_october);

            case android.icu.util.Calendar.NOVEMBER:
                return getString(R.string.months_november);

            case android.icu.util.Calendar.DECEMBER:
                return getString(R.string.months_december);

            default:
                return "";
        }
    }

    public String getAddress(LatLng location) {
        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            try {
                Address obj = addresses.get(0);
                return obj.getAddressLine(0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //add = add + "\n" + obj.getCountryName();
            //add = add + "\n" + obj.getCountryCode();
            //add = add + "\n" + obj.getAdminArea();
            //add = add + "\n" + obj.getPostalCode();
            //add = add + "\n" + obj.getSubAdminArea();
            //add = add + "\n" + obj.getLocality();
            //add = add + "\n" + obj.getSubThoroughfare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error Has Occurred";
    }

    private int getSubjectIcon(String subject) {
        switch (subject) {
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

    @Override
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null) {
            mapView.onStop();
        }
    }
}