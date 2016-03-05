package com.jolmagic.channimit.blognonefast.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.einmalfel.earl.Item;
import com.jolmagic.channimit.blognonefast.R;

import java.util.List;

/**
 * Created by Big on 5/3/2559.
 * Copyright © 2012 - 2559 channimit All rights reserved.
 */
public class NewsHeadlineAdapter extends RecyclerView.Adapter<NewsHeadlineAdapter.ViewHolder> {
    private List<? extends Item> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView headlineView;
        public TextView descView;
        public ViewHolder(View convertView) {
            super(convertView);

            headlineView  = (TextView) convertView.findViewById(R.id.headline_in_view_headline);
            descView = (TextView) convertView.findViewById(R.id.desc_in_view_headline);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NewsHeadlineAdapter(List<? extends Item> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NewsHeadlineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_headline, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.headlineView.setText(mDataset.get(position).getTitle());
        Html.ImageGetter imageGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                return new ColorDrawable(Color.TRANSPARENT);
            }
        };
        holder.descView.setText(Html.fromHtml(mDataset.get(position).getDescription(), imageGetter, null));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
