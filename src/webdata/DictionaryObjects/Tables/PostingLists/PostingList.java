package webdata.DictionaryObjects.Tables.PostingLists;

import java.util.ArrayList;

public interface PostingList {

    String getCompressedPostingList();

    default ArrayList<Integer> updatePostingListToGaps(ArrayList<Integer> postingList) {
        for (int i = postingList.size() - 1; i > 0; i--) {
            postingList.set(i, postingList.get(i) - postingList.get(i - 1));
        }
        return postingList;
    }

}
