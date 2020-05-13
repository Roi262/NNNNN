package webdata.DictionaryObjects.Tables;

import webdata.DictionaryObjects.Tables.PostingLists.PostingList;
import webdata.DictionaryObjects.Tables.Rows.Row;
import webdata.Review;

import java.util.Map;
import java.util.TreeMap;


public class FCTablePIDDict extends FCTable {

    TreeMap<String, PostingList> sortedPIDDict; // sorted by key


    /*****************CONSTRUCTOR*****************/
    public FCTablePIDDict() {
        super();
        this.sortedPIDDict = new TreeMap<>();
    }


    @Override
    public void update(Review review) {
        String PID = review.getProductID();
        PostingList PostingList = sortedPIDDict.getOrDefault(PID, new PostingList());
        PostingList.update(review.getReviewID());
        sortedPIDDict.put(PID, PostingList);
    }


    @Override
    public void compressAndCreate() {
        int currIndex = 0;
        int prefixSize;
        int currTermPtr = 0;

        String previousTerm = null;
        String term;
        String croppedTerm;
        String compressedPostingList;

        StringBuilder concatStrBuilder = new StringBuilder();

        PostingList PostingList;

        for (Map.Entry<String, PostingList> entry : sortedPIDDict.entrySet()) {
            term = entry.getKey();
            PostingList = entry.getValue();

//            update serializable table
            compressedPostingList = PostingList.getCompressedPostingList();
            prefixSize = FCTable.getPrefixSize(previousTerm, term, currIndex);
            Row row = getRow(currIndex, compressedPostingList, term.length(), prefixSize);
            serializableTable.add(row);

//            update the long string
            croppedTerm = FCTable.cropTerm(prefixSize, term);
            concatStrBuilder.append(croppedTerm);

//            update for next iteration
            previousTerm = term;
            currTermPtr += croppedTerm.length();
            currIndex++;
        }
        compressedStringDict = concatStrBuilder.toString();
    }


}
