package com.pnuema.simplebible.retrievers;

import android.support.annotation.NonNull;

import com.pnuema.simplebible.data.Verses;
import com.pnuema.simplebible.retrofit.API;
import com.pnuema.simplebible.retrofit.IAPI;

import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VersesRetriever extends Observable {
    public void loadData(String version, String book, String chapter) {
        IAPI api = API.getInstance().create(IAPI.class);
        Call<Verses> call = api.getVerses(version, book, chapter);
        call.enqueue(new Callback<Verses>() {
            @Override
            public void onResponse(@NonNull Call<Verses> call, @NonNull Response<Verses> response) {
                Verses verses = response.body();
                if (verses == null || verses.response == null) {
                    return;
                }

                setChanged();
                notifyObservers(verses.response.verses);
            }

            @Override
            public void onFailure(@NonNull Call<Verses> call, @NonNull Throwable t) {
            }
        });
    }
}
