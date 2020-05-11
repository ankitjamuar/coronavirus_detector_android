package in.tagbin.covid_tracker.ui.slideshow;

import com.ramotion.expandingcollection.ECCardData;

import in.tagbin.covid_tracker.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardDataImpl implements ECCardData<String> {

    private String cardTitle;
    private String timeSlot;
    private String status;
    private String category;
    private String reason;

    private Integer mainBackgroundResource;
    private Integer headBackgroundResource;
    private List<String> listItems;

    public CardDataImpl(String reason,String timeSlot, String status, String category, String cardTitle, Integer mainBackgroundResource, Integer headBackgroundResource, List<String> listItems) {
        this.mainBackgroundResource = mainBackgroundResource;
        this.headBackgroundResource = headBackgroundResource;
        this.listItems = listItems;
        this.cardTitle = cardTitle;
        this.timeSlot = timeSlot;
        this.status = status;
        this.category = category;
        this.reason = reason;
    }

    @Override
    public Integer getMainBackgroundResource() {
        return mainBackgroundResource;
    }

    @Override
    public Integer getHeadBackgroundResource() {
        return headBackgroundResource;
    }

    @Override
    public List<String> getListItems() {
        return listItems;
    }

    public String getCardTitle() {
        return cardTitle;
    }
    public String getCardTimeSlot() {
        return timeSlot;
    }
    public String getCardStatus() {
        return status;
    }
    public String getCardCategory() {
        return category;
    }
    public String getCardReason() {
        return reason;
    }



    public static List<ECCardData> generateExampleData() {


        List<ECCardData> list = new ArrayList<>();

        return list;
    }

    private static List<String> createItemsList(String cardName) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(
                Arrays.asList(cardName)
        );
        return list;
    }

}