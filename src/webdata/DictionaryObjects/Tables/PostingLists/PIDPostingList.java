package webdata.DictionaryObjects.Tables.PostingLists;

import java.util.ArrayList;

public class PIDPostingList implements PostingList {

    /**
     * The list of only the review IDs
     */
    private ArrayList<Integer> postingList;

    public PIDPostingList() {
        this.postingList = new ArrayList<>();
    }

    /**
     * Note that the way in which the program updates makes this list sorted
     *
     * @param reviewID
     */
    public void update(Integer reviewID) {
        postingList.add(reviewID);
    }


    @Override
    public String getCompressedPostingList() {
        postingList = updatePostingListToGaps(postingList);
        return DeltaPostingListCompressor.compressList(postingList);
    }
}
