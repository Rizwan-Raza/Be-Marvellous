package com.wampinfotech.marvelCharacters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CharacterAdapter extends ArrayAdapter<Characters> {

    public CharacterAdapter(Context context, List<Characters> characters) {
        super(context, 0, characters);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.character_item, parent, false);
        }

        Characters currentCharacter = getItem(position);
        if (currentCharacter == null) {
            return listItemView;
        }

        TextView nameView = listItemView.findViewById(R.id.ch_name);
        nameView.setText(currentCharacter.getName());

        TextView descView = listItemView.findViewById(R.id.ch_desc);
        String desc = currentCharacter.getDesc();

        if (desc == null || TextUtils.isEmpty(desc) || desc.equalsIgnoreCase("null")) {
            descView.setVisibility(View.GONE);
        } else {
            descView.setText(desc);
            descView.setVisibility(View.VISIBLE);
        }

        TextView textView = listItemView.findViewById(R.id.ch_secondary);
        String title = currentCharacter.getTitle();

        if (title == null || TextUtils.isEmpty(title) || title.equalsIgnoreCase("null")) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(title);
            textView.setVisibility(View.VISIBLE);
        }

        ImageView imageView = listItemView.findViewById(R.id.ch_icon);

        // https://terrigen-cdn-dev.marvel.com/content/prod/1x/
        String filename = currentCharacter.getImage();

        if (filename.equalsIgnoreCase("https://terrigen-cdn-dev.marvel.com/content/prod/1x/null") || filename.equalsIgnoreCase("https://terrigen-cdn-dev.marvel.com/content/prod/1x/")) {
            imageView.setImageResource(R.drawable.image_not_found);
        } else {
            Picasso.get().load(currentCharacter.getImage()).into(imageView);
        }

//        TextView nameView = listItemView.findViewById(R.id.ch_name);
//        nameView.setText(currentCharacter != null ? currentCharacter.getName() : null);
//
//        TextView descView = listItemView.findViewById(R.id.ch_desc);
//        descView.setText(currentCharacter != null ? currentCharacter.getDesc() : null);
//
//        TextView idView = listItemView.findViewById(R.id.ch_id);
//        idView.setText((currentCharacter != null ? String.valueOf(currentCharacter.getId()) : "0"));
//
//        // Create a new Date object from the time in milliseconds of the earthquake
//        Date dateObject = currentCharacter != null ? currentCharacter.getUpdate() : null;
//
//        // Find the TextView with view ID date
//        TextView dateView = listItemView.findViewById(R.id.ch_date);
//        // Format the date string (i.e. "Mar 3, 1984")
//        String formattedDate = formatDate(dateObject);
//        // Display the date of the current earthquake in that TextView
//        dateView.setText(formattedDate);
//
//        // Find the TextView with view ID time
//        TextView timeView = listItemView.findViewById(R.id.ch_time);
//        // Format the time string (i.e. "4:30PM")
//        String formattedTime = formatTime(dateObject);
//        // Display the time of the current earthquake in that TextView
//        timeView.setText(formattedTime);

        return listItemView;
    }

//    /**
//     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
//     */
//    private String formatDate(Date dateObject) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy", Locale.ENGLISH);
//        return dateFormat.format(dateObject);
//    }
//
//    /**
//     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
//     */
//    private String formatTime(Date dateObject) {
//        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
//        return timeFormat.format(dateObject);
//    }
}
