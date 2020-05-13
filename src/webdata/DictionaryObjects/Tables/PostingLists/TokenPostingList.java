package webdata.DictionaryObjects.Tables.PostingLists;

import java.util.ArrayList;

public class TokenPostingList extends PostingList {

    /** The respective list of frequencies to the posting list,
     * i.e., a list of the number of appearances of the token this list refers
     * to in the review id that is equal to the index of the item in the list.
     */
    private final ArrayList<Integer> frequencyList;

    public TokenPostingList() {
        super();
        this.frequencyList = new ArrayList<>();
    }

    public void update(Integer reviewID, int freq){
        super.update(reviewID);
        frequencyList.add(freq);
    }

    /**
     *
     * @return the delta compressed posting list with frequencies.
     * Overrides the method from Parent class, because parent doesn't deal with frequencies
     */
    @Override
    public String getCompressedPostingList(){
        updatePostingListToGaps();
        ArrayList<Integer> postingListWithFrequencies = postingList;
        postingListWithFrequencies.addAll(frequencyList);
        return DeltaPostingListCompressor.compressList(postingListWithFrequencies);
    }

}
