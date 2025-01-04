package com.example.fitnessapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class fitness_video extends Fragment {

    // Dropdown menu items
    String[] items = {"Belly Fat Burn & Abs", "Booty & Leg", "Arm & Back", "Cardio"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    LinearLayout videoButtonLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fitness_video, container, false);

        // Initialize components
        autoCompleteTextView = view.findViewById(R.id.auto_complete_txt);
        videoButtonLayout = view.findViewById(R.id.video_button_layout);

        // Create an adapter for the dropdown menu
        adapterItems = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, items);
        autoCompleteTextView.setAdapter(adapterItems);

        // Handle item selection in the dropdown
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();

                // Load video items based on the selected category
                loadVideoItems(selectedItem);
            }
        });

        return view;
    }

    private void loadVideoItems(String selectedItem) {
        // Clear any existing video items
        videoButtonLayout.removeAllViews();
        // Add buttons based on the selected category
        switch (selectedItem) {
            case "Belly Fat Burn & Abs":
                addButton("10 Min Morning Routine", "https://www.youtube.com/watch?v=xyR8McQnGuw",R.drawable.thumbnail1);
                addButton("Lose Belly Fat in 10 Days", "https://www.youtube.com/watch?v=iZPjHyWhoDw",R.drawable.thumbnail2);
                addButton("Burn Belly Fat & Thigh Fat", "https://www.youtube.com/watch?v=KRS57hx3o6E",R.drawable.thumbnail3);
                addButton("15 Day Lower Abs & Intense", "https://www.youtube.com/watch?v=QsG25Rr09JY",R.drawable.thumbnail4);
                addButton("Belly Fat Burner Workout", "https://youtu.be/owrBD7_8edA?si=Ilc7a4lUQYLZC5VO",R.drawable.thumbnail5);
                addButton("Get Abs in 2 Weeks", "https://youtu.be/2pLT-olgUJs?si=cpbSqiv-C68a1aMU",R.drawable.thumbnail6);
                break;
            case "Booty & Leg":
                addButton("Booty Burn Workout", "https://youtu.be/Jg61m0DwURs?si=z0fp7_L7zYvpSop9",R.drawable.thumbnail7);
                addButton("Booty Burn Routine", "https://youtu.be/5zbYHZRzTY0?si=xDUV_JEmMMoEFrqB",R.drawable.thumbnail8);
                addButton("Leg Workout", "https://youtu.be/Fu_oExrPX68?si=1PVwBFft5w1wTqsE",R.drawable.thumbnail9);
                addButton("Lower Body Routine", "https://youtu.be/FJA3R7n_594?si=sLSjaSTXX1_csabd",R.drawable.thumbnail10);
                addButton("Glutes and Thighs", "https://youtu.be/p-uUnrCdhR8?si=9nwzLH--jWiX_Z_J",R.drawable.thumbnail11);
                addButton("Full Leg Sculpt", "https://youtu.be/tC2PuvibB7w?si=svs4us3gkWkmBIGq",R.drawable.thumbnail12);
                break;
            case "Arm & Back":
                addButton("Arm & Back Strength", "http://www.youtube.com/watch?v=DHOPWvO3ZcI",R.drawable.thumbnail13);
                addButton("Upper Body Workout", "http://www.youtube.com/watch?v=s0Qbxm3gUso",R.drawable.thumbnail14);
                addButton("Back Toning Routine", "http://www.youtube.com/watch?v=P7k7ABUKmxo",R.drawable.thumbnail15);
                addButton("Strengthen Arms", "http://www.youtube.com/watch?v=ATypGbs98F4",R.drawable.thumbnail16);
                addButton("Upper Body Sculpt", "http://www.youtube.com/watch?v=_bpnk9mxDk0",R.drawable.thumbnail17);
                break;
            case "Cardio":
                addButton("Full Body Cardio Workout ", "https://youtu.be/luoZRFWpRSs?si=T_9jPniI0LkFj3Qg",R.drawable.thumbnail18);
                addButton("30 Min Cardio Workout At Home", "https://youtube.com/watch?v=Yn0dV4s81H0",R.drawable.thumbnail19);
                addButton("10 Min HIIT Cardio Workout to Burn Fat", "https://youtube.com/watch?v=hLVTgnk7mLI",R.drawable.thumbnail20);
                addButton("20 minute Cardio Workout ", "https://youtu.be/Cvchap5oZSE?si=pK5QyNoV8YEghkpu",R.drawable.thumbnail21);
                addButton("Cardio Power", "https://youtu.be/Yn0dV4s81H0?si=K0jM9X1QncpjQJfz",R.drawable.thumbnail22);
                break;
            default:
                Toast.makeText(getContext(), "No videos available for this category.", Toast.LENGTH_SHORT).show();
        }
    }


    private void addButton(String title, String videoUrl, int thumbnailResId) {
        // Inflate the custom video item layout
        View videoItem = LayoutInflater.from(getContext()).inflate(R.layout.video_item_layout, videoButtonLayout, false);

        // Find the ImageView and TextView in the custom layout
        ImageView thumbnail = videoItem.findViewById(R.id.video_thumbnail);
        TextView videoTitle = videoItem.findViewById(R.id.video_title);

        // Set the thumbnail image and video title
        thumbnail.setImageResource(thumbnailResId);
        videoTitle.setText(title);

        // Set an onClickListener to open the YouTube URL
        videoItem.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
            startActivity(intent);
        });

        // Add the custom layout to the parent layout
        videoButtonLayout.addView(videoItem);
    }
}
