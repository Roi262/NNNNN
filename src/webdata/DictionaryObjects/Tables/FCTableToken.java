package webdata.DictionaryObjects.Tables;

import webdata.DictionaryObjects.Tables.PostingLists.PIDPostingList;
import webdata.DictionaryObjects.Tables.PostingLists.TokenPostingList;
import webdata.DictionaryObjects.Tables.Rows.Row;
import webdata.Review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static webdata.Constants.HyperParameters.k;

public class FCTableToken extends FCTable  {

    TreeMap<String, TokenPostingList> sortedTokensDict; // sorted by key


    /*****************CONSTRUCTOR*****************/
    public FCTableToken(){
        super();
        this.sortedTokensDict = new TreeMap<>();
    }


    /**
     * Counts the occurrences of each token in the review.
     * Then, updates the posting list of the token.
     * @param review
     */
    @Override
    public void update(Review review) {
        ArrayList<String> tokensInReview = review.getTextTokens();
        Map<String, Integer> tokensCount = new HashMap<>();

//        count number of occurrences in review for each token
        for (String token : tokensInReview) {
            Integer count = tokensCount.getOrDefault(token, 0);
            tokensCount.put(token, count + 1);
        }

        for (Map.Entry<String, Integer> entry : tokensCount.entrySet()) {
            String token = entry.getKey();
            if (token.equals("")) continue;

            TokenPostingList tokenPostingList = sortedTokensDict.getOrDefault(token, new TokenPostingList());
            tokenPostingList.update(review.getReviewID(), entry.getValue());
            sortedTokensDict.put(token, tokenPostingList);
        }
    }

    @Override
    public void compressAndCreate() {
        int rowIndex = 0; // index of row in table
        int prefixSize;
        int currTermPtr = 0;

        String previousKthTerm = null;
        String term;
        String croppedTerm;
        String compressedPostingList;

        StringBuilder concatStrBuilder = new StringBuilder();

        PIDPostingList PIDPostingList;

        for (Map.Entry<String, TokenPostingList> entry : sortedTokensDict.entrySet()) {
            term = entry.getKey();
            PIDPostingList = entry.getValue();

//            update serializable table
            compressedPostingList = PIDPostingList.getCompressedPostingList();
            prefixSize = FCTable.getPrefixSize(previousKthTerm, term, rowIndex);
            Row row = getRow(rowIndex, compressedPostingList, term.length(), prefixSize, currTermPtr);
            serializableTable.add(row);

//            update the long string
            croppedTerm = FCTable.cropTerm(prefixSize, term);
            concatStrBuilder.append(croppedTerm);

//            update for next iteration
            if (rowIndex % k == 0){
                previousKthTerm = term;
            }
            currTermPtr += croppedTerm.length();
            rowIndex++;
        }
        compressedStringDict = concatStrBuilder.toString();
    }

}
