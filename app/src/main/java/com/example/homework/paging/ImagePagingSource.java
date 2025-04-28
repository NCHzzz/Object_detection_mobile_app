// paging/ImagePagingSource.java
package com.example.homework.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.example.homework.api.PixabayApiService;
import com.example.homework.models.ImageItem;
import com.example.homework.models.PixabayResponse;

import java.io.IOException;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class ImagePagingSource extends RxPagingSource<Integer, ImageItem> {
    private static final int STARTING_PAGE_INDEX = 1;
    private static final String API_KEY = "49986625-4f1859a03ab2560647eb5ea99";
    private static final int PER_PAGE = 20;

    private final PixabayApiService apiService;
    private final String query;

    public ImagePagingSource(PixabayApiService apiService, String query) {
        this.apiService = apiService;
        this.query = query;
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, ImageItem>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        int page = loadParams.getKey() != null ? loadParams.getKey() : STARTING_PAGE_INDEX;

        return Single.fromCallable(() -> {
            try {
                Call<PixabayResponse> call = apiService.searchImages(API_KEY, query, page, PER_PAGE);
                Response<PixabayResponse> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    PixabayResponse data = response.body();

                    // Calculate next key
                    Integer nextKey = null;
                    if (!data.getHits().isEmpty() && data.getHits().size() >= PER_PAGE) {
                        nextKey = page + 1;
                    }

                    return new LoadResult.Page<Integer, ImageItem>(
                            data.getHits(),
                            page > STARTING_PAGE_INDEX ? page - 1 : null,
                            nextKey
                    );
                } else {
                    return new LoadResult.Error<Integer, ImageItem>(new IOException("API error " + response.code()));
                }
            } catch (Exception e) {
                return new LoadResult.Error<Integer, ImageItem>(e);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, ImageItem> state) {
        Integer anchorPosition = state.getAnchorPosition();
        if (anchorPosition == null) {
            return null;
        }

        LoadResult.Page<Integer, ImageItem> anchorPage = state.closestPageToPosition(anchorPosition);
        if (anchorPage == null) {
            return null;
        }

        Integer prevKey = anchorPage.getPrevKey();
        if (prevKey != null) {
            return prevKey + 1;
        }

        Integer nextKey = anchorPage.getNextKey();
        if (nextKey != null) {
            return nextKey - 1;
        }

        return null;
    }
}