package webdata.DictionaryObjects.Tables;

import webdata.DictionaryObjects.Tables.PostingLists.PostingList;
import webdata.DictionaryObjects.Tables.PostingLists.TokenPostingList;
import webdata.DictionaryObjects.Tables.Rows.Row;
import webdata.Review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
        int currIndex = 0;
        int prefixSize;
        int currTermPtr = 0;

        String previousTerm = null;
        String term;
        String croppedTerm;
        String compressedPostingList;

        StringBuilder concatStrBuilder = new StringBuilder();

        PostingList PostingList;

        for (Map.Entry<String, TokenPostingList> entry : sortedTokensDict.entrySet()) {
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
