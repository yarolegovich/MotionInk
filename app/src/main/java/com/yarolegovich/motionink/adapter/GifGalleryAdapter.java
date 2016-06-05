package com.yarolegovich.motionink.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yarolegovich.motionink.GifOpenActivity;
import com.yarolegovich.motionink.R;

import java.io.File;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by yarolegovich on 04.06.2016.
 */
public class GifGalleryAdapter extends RecyclerView.Adapter<GifGalleryAdapter.ViewHolder> {

    private File[] files;

    public GifGalleryAdapter(File[] files) {
        this.files = files;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_gif, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Uri gifUri = Uri.fromFile(files[position]);
        holder.gifUri = gifUri;
        holder.gifImageView.setImageURI(gifUri);
    }

    @Override
    public int getItemCount() {
        return files != null ? files.length : 0;
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Uri gifUri;

        GifImageView gifImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            gifImageView = (GifImageView) itemView.findViewById(R.id.gif_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), GifOpenActivity.class);
            intent.putExtra(GifOpenActivity.EXTRA_GIF, gifUri);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) v.getContext(),
                            gifImageView, "gif");
            v.getContext().startActivity(intent, options.toBundle());
        }
    }
}
