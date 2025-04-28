// app/src/main/java/com/example/homework/adapter/ImagePagingAdapter.java
package com.example.homework.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homework.R;
import com.example.homework.models.ImageItem;
import com.example.homework.viewmodel.ImageViewModel;

import java.util.List;

public class ImagePagingAdapter extends PagingDataAdapter<ImageItem, ImagePagingAdapter.ImageViewHolder> {

    private final ImageViewModel viewModel;

    public ImagePagingAdapter(ViewModelStoreOwner viewModelStoreOwner) {
        super(COMPARATOR);
        this.viewModel = new ViewModelProvider(viewModelStoreOwner).get(ImageViewModel.class);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    // In your ImagePagingAdapter.java, modify the onBindViewHolder method:
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageItem item = getItem(position);
        if (item != null) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getPreviewURL())
                    .centerCrop()
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_error)
                    .into(holder.imageView);

            // Set initial tags
            holder.tagsTextView.setText(item.getTags());

            // Reference the textView for clarity
            final TextView textView = holder.tagsTextView;

            // Analyze image and update tags
            viewModel.analyzeImage(item.getPreviewURL()).thenAccept(labels -> {
                if (labels != null && !labels.isEmpty()) {
                    String originalTags = item.getTags();
                    String aiLabels = String.join(", ", labels);
                    String combinedTags = originalTags + "\nAI detected: " + aiLabels;

                    // Use Activity's runOnUiThread to ensure UI update on main thread
                    if (holder.itemView.getContext() instanceof AppCompatActivity) {
                        ((AppCompatActivity) holder.itemView.getContext()).runOnUiThread(() -> {
                            // Check if view is still bound to the same item
                            if (holder.getBindingAdapterPosition() != RecyclerView.NO_POSITION
                                    && getItem(holder.getBindingAdapterPosition()) == item) {
                                textView.setText(combinedTags);
                                // Make sure the TextView is visible
                                textView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            });
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tagsTextView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            tagsTextView = itemView.findViewById(R.id.tags_text_view);
        }
    }

    private static final DiffUtil.ItemCallback<ImageItem> COMPARATOR =
            new DiffUtil.ItemCallback<ImageItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull ImageItem oldItem, @NonNull ImageItem newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull ImageItem oldItem, @NonNull ImageItem newItem) {
                    return oldItem.getId() == newItem.getId();
                }
            };
}