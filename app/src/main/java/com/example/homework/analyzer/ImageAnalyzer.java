package com.example.homework.analyzer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ImageAnalyzer {
    private final Context context;
    private final ImageLabeler labeler;

    public ImageAnalyzer(Context context) {
        this.context = context;
        this.labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
    }

    // In ImageAnalyzer.java, update the analyzeImage method
    public CompletableFuture<List<String>> analyzeImage(String imageUrl) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();

        Log.d("ImageAnalyzer", "Starting image analysis for: " + imageUrl);

        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().disallowHardwareConfig())
                .into(new CustomTarget<Bitmap>(300, 300) { // Add specific dimensions
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, Transition<? super Bitmap> transition) {
                        Log.d("ImageAnalyzer", "Image loaded successfully, bitmap size: " +
                                bitmap.getWidth() + "x" + bitmap.getHeight());
                        InputImage image = InputImage.fromBitmap(bitmap, 0);

                        // In ImageAnalyzer.java
                        labeler.process(image)
                                .addOnSuccessListener(labels -> {
                                    List<String> topLabels = new ArrayList<>();
                                    int count = Math.min(labels.size(), 3);

                                    for (int i = 0; i < count; i++) {
                                        topLabels.add(labels.get(i).getText());
                                    }

                                    Log.d("ImageAnalyzer", "Image analysis complete. Labels: " +
                                            (topLabels.isEmpty() ? "None found" : String.join(", ", topLabels)));
                                    future.complete(topLabels);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ImageAnalyzer", "Error analyzing image", e);
                                    future.complete(new ArrayList<>());
                                });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Nothing to do
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Log.e("ImageAnalyzer", "Failed to load image");
                        future.complete(new ArrayList<>());
                    }
                });

        return future;
    }
}