package com.wampinfotech.marvelCharacters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CharacterAdapter extends ArrayAdapter<Characters> implements Filterable {

    private ValueFilter valueFilter;
    private List<Characters> mStringFilterList;
    private List<Characters> arrayList;

    CharacterAdapter(Context context, List<Characters> characters) {
        super(context, 0, characters);
        arrayList = characters;
        mStringFilterList = characters;
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

        final TextView nameView = listItemView.findViewById(R.id.ch_name);
        nameView.setText(currentCharacter.getName());

//        TextView descView = listItemView.findViewById(R.id.ch_desc);
//        String desc = currentCharacter.getDesc();
//
//        if (desc == null || TextUtils.isEmpty(desc) || desc.equalsIgnoreCase("null")) {
//            descView.setVisibility(View.GONE);
//        } else {
//            descView.setText(desc);
//            descView.setVisibility(View.VISIBLE);
//        }

        final TextView textView = listItemView.findViewById(R.id.ch_secondary);
        String title = currentCharacter.getTitle();

        if (title == null || TextUtils.isEmpty(title) || title.equalsIgnoreCase("null")) {
            textView.setText(currentCharacter.getName());
        } else {
            textView.setText(title);
        }

        final ImageView imageView = listItemView.findViewById(R.id.ch_icon);

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


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Characters getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

    @NonNull
    @Override
    public android.widget.Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new CharacterAdapter.ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends android.widget.Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<Characters> filterList = new ArrayList<>();
                for (Characters item : mStringFilterList) {
                    if (item.getTitle().toUpperCase().contains(constraint.toString().toUpperCase())
                            || item.getName().toUpperCase().contains(constraint.toString().toUpperCase())) {
//                        Log.e("MARVEL", "ITEM: " + item.getTitle());
                        filterList.add(item);
                    }
                }
//                Log.e("_____", mStringFilterList.size() + "__________________________________________________" + filterList.size());
                results.count = filterList.size();
                results.values = filterList;
            } else {
//                Log.e("_____", mStringFilterList.size() + "Original Result");
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            arrayList = (List<Characters>) results.values;
            notifyDataSetChanged();
        }
    }
}
