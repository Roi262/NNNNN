package webdata.DictionaryObjects.Tables.PostingLists;

import java.util.ArrayList;

public class PostingList {

    /** The list of only the review IDs*/
    protected final ArrayList<Integer> postingList;

    public PostingList() {
        this.postingList = new ArrayList<>();
    }

    /**
     * Note that the way in which the program updates makes this list sorted
     * @param reviewID
     */
    public void update(Integer reviewID){
        postingList.add(reviewID);
    }

//    public void create(TreeSet<Integer> reviewIDs) {
//        postingList.addAll(reviewIDs);
//    }

    public String getCompressedPostingList(){
        updatePostingListToGaps();
        return DeltaPostingListCompressor.compressList(postingList);
    }

    public void updatePostingListToGaps() {
        for(int i = postingList.size()-1; i > 0; i--){
            postingList.set(i, postingList.get(i) - postingList.get(i-1));
        }
    }


}
