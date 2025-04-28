// app/src/main/java/com/example/homework/viewmodel/ImageViewModel.java
package com.example.homework.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.example.homework.analyzer.ImageAnalyzer;
import com.example.homework.api.PixabayApiService;
import com.example.homework.api.RetrofitClient;
import com.example.homework.models.ImageItem;
import com.example.homework.paging.ImagePagingSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ImageViewModel extends AndroidViewModel {
    private final PixabayApiService apiService;
    private final Map<String, LiveData<PagingData<ImageItem>>> searchCache = new HashMap<>();
    private static final int PAGE_SIZE = 20;
    private final ImageAnalyzer imageAnalyzer;

    public ImageViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getClient().create(PixabayApiService.class);
        imageAnalyzer = new ImageAnalyzer(application.getApplicationContext());
    }

    public LiveData<PagingData<ImageItem>> searchImages(String query) {
        // AI Cache implementation - check if query result is already cached
        if (searchCache.containsKey(query)) {
            return searchCache.get(query);
        }

        Pager<Integer, ImageItem> pager = new Pager<>(
                new PagingConfig(PAGE_SIZE, PAGE_SIZE, false),
                () -> new ImagePagingSource(apiService, query)
        );

        LiveData<PagingData<ImageItem>> liveData = PagingLiveData.cachedIn(
                PagingLiveData.getLiveData(pager),
                ViewModelKt.getViewModelScope(this)
        );

        // Cache the result
        searchCache.put(query, liveData);
        return liveData;
    }

    public CompletableFuture<List<String>> analyzeImage(String imageUrl) {
        return imageAnalyzer.analyzeImage(imageUrl);
    }
}