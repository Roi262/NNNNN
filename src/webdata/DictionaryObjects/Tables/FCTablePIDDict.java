package webdata.DictionaryObjects.Tables;

import webdata.DictionaryObjects.Tables.PostingLists.PIDPostingList;
import webdata.Review;


public class FCTablePIDDict extends FCTable {


    /*****************CONSTRUCTOR*****************/
    public FCTablePIDDict() {
        super();
    }


    @Override
    public void update(Review review) {
        String PID = review.getProductID();
        PIDPostingList PIDPostingList = (PIDPostingList) sortedDict.getOrDefault(PID, new PIDPostingList());
        PIDPostingList.update(review.getReviewID());
        sortedDict.put(PID, PIDPostingList);
    }

}
