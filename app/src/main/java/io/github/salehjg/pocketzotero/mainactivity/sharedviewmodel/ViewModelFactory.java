package io.github.salehjg.pocketzotero.mainactivity.sharedviewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application application;

    private final long id;

    public ViewModelFactory(@NonNull Application application, long id) {
        this.application = application;
        this.id = id;
    }

    @NonNull
    @Override
    public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == SharedViewModel.class) {
            return (T) new SharedViewModel(application, id);
        }
        return null;
    }
}
