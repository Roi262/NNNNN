package webdata.DictionaryObjects.Tables;

import webdata.DictionaryObjects.Tables.PostingLists.PIDPostingList;
import webdata.DictionaryObjects.Tables.Rows.Row;
import webdata.Review;

import java.util.Map;
import java.util.TreeMap;

import static webdata.Constants.HyperParameters.k;


public class FCTablePIDDict extends FCTable {

    TreeMap<String, PIDPostingList> sortedPIDDict; // sorted by key


    /*****************CONSTRUCTOR*****************/
    public FCTablePIDDict() {
        super();
        this.sortedPIDDict = new TreeMap<>();
    }


    @Override
    public void update(Review review) {
        String PID = review.getProductID();
        PIDPostingList PIDPostingList = sortedPIDDict.getOrDefault(PID, new PIDPostingList());
        PIDPostingList.update(review.getReviewID());
        sortedPIDDict.put(PID, PIDPostingList);
    }


    @Override
    public void compressAndCreate() {
        int currIndex = 0;
        int prefixSize;
        int currTermPtr = 0;

        String previousKthTerm = null;
        String term;
        String croppedTerm;
        String compressedPostingList;

        StringBuilder concatStrBuilder = new StringBuilder();

        PIDPostingList PIDPostingList;

        for (Map.Entry<String, PIDPostingList> entry : sortedPIDDict.entrySet()) {
            term = entry.getKey();
            PIDPostingList = entry.getValue();

//            update serializable table
            compressedPostingList = PIDPostingList.getCompressedPostingList();
            prefixSize = FCTable.getPrefixSize(previousKthTerm, term, currIndex);
            Row row = getRow(currIndex, compressedPostingList, term.length(), prefixSize, currTermPtr);
            serializableTable.add(row);

//            update the long string
            croppedTerm = FCTable.cropTerm(prefixSize, term);
            concatStrBuilder.append(croppedTerm);

//            update for next iteration
            if (currIndex % k == 0){
                previousKthTerm = term;
            }
            currTermPtr += croppedTerm.length();
            currIndex++;
        }
        compressedStringDict = concatStrBuilder.toString();
    }


}
