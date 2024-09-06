package com.aaa.vibesmusic.ui.songimport.result;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImportSongsActivityResultContract extends ActivityResultContract<Void, List<Uri>> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Void unused) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        return Intent.createChooser(intent, "Select music to import");
    }

    @Override
    public List<Uri> parseResult(int i, @Nullable Intent intent) {
        if(i == Activity.RESULT_OK) {
            List<Uri> importedSongs = null;

            if(Objects.nonNull(intent)) {
                ClipData clipData = intent.getClipData();
                if (Objects.nonNull(clipData)) {
                    importedSongs = this.parseClipData(clipData);
                } else {
                    Uri data = intent.getData();
                    importedSongs = this.parseUriData(data);
                }
            }

            return importedSongs;
        } else {
            return List.of();
        }
    }

    /**
     *
     * @param data The {@link ClipData} with all the {@link Uri}s to import
     * @return The {@link List} of chosen {@link Uri}s
     */
    private List<Uri> parseClipData(ClipData data) {
        List<Uri> uris = new ArrayList<>();

        for (int i = 0; i < data.getItemCount(); i++) {
            Uri uri = data.getItemAt(i).getUri();
            uris.add(uri);
        }

        return uris;
    }

    /**
     *
     * @param data The {@link Uri} of the file to user chose
     * @return The {@link List} of chosen {@link Uri}s
     */
    private List<Uri> parseUriData(Uri data) {
        return List.of(data);
    }
}
