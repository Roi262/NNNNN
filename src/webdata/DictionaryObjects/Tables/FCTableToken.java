package webdata.DictionaryObjects.Tables;

import webdata.DictionaryObjects.Tables.PostingLists.TokenPostingList;
import webdata.Review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FCTableToken extends FCTable {

    /*****************CONSTRUCTOR*****************/
    public FCTableToken() {
        super();
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


            TokenPostingList tokenPostingList = (TokenPostingList) sortedDict.getOrDefault(token, new TokenPostingList());
            tokenPostingList.update(review.getReviewID(), entry.getValue());
            sortedDict.put(token, tokenPostingList);
        }
    }
}
