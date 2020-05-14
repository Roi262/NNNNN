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

//    public void create(TreeSet<Integer> reviewIDs) {
//        postingList.addAll(reviewIDs);
//    }

    @Override
    public String getCompressedPostingList() {
        postingList = updatePostingListToGaps(postingList);
        return DeltaPostingListCompressor.compressList(postingList);
    }



//    public static ArrayList<Integer> deGap(ArrayList<Integer> postingList) {
////        ArrayList<Integer> degappedPL = new ArrayList<>();
////        degappedPL.add(gappedPostingList.get(0));
//        for (int i = 1; i < postingList.size(); i++) {
//            postingList.add(postingList.get(i-1) + postingList.get(i));
//        }
//        return postingList;
//    }


}
