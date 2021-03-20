package com.example.channelslist;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ItemViewModel extends ViewModel {
    private final MutableLiveData<TvRequest> selectedItem = new MutableLiveData<>();
    public void selectItem(TvRequest tvRequest) {
        selectedItem.setValue(tvRequest);
    }
    public LiveData<TvRequest> getSelectedItem() {
        return selectedItem;
    }
}