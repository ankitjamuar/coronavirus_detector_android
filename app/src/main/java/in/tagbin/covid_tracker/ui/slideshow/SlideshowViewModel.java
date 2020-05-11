package in.tagbin.covid_tracker.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SlideshowViewModel extends ViewModel {

    private MutableLiveData<String> passes;

    public SlideshowViewModel() {
        passes = new MutableLiveData<>();
        passes.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return passes;
    }


}