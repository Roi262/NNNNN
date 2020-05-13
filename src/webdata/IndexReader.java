package webdata;

import webdata.DictionaryObjects.FeaturesDict;

import java.io.IOException;

import static webdata.Constants.Features.*;
import static webdata.Constants.IndexFilePaths.*;
import static webdata.Serializer.ReadObjectFromFile;
import static webdata.SlowIndexWriter.dir;

public class IndexReader {

    /********dictionaries**********/
    FeaturesDict featuresDict;

    /******************Counter****************/
    TotalCounts tc;


    /*****************CONSTRUCTOR*****************/
    public IndexReader() throws IOException, ClassNotFoundException {
        this.featuresDict = (FeaturesDict) ReadObjectFromFile(FEATURES_DICT_PATH);
        this.tc = (TotalCounts) ReadObjectFromFile(dir + TOTAL_COUNTS_PATHS);
    }



    /**
     * Returns the product identifier for the given review
     * Returns null if there is no review with the given identifier
     */
    public String getProductId(int reviewId) {
        return featuresDict.getProductID(reviewId);
    }

    /**
     * Returns the score for a given review
     * Returns -1 if there is no review with the given identifier
     */
    public int getReviewScore(int reviewId) {
        return featuresDict.getValue(reviewId, SCORE);
    }

    /**
     * Returns the numerator for the helpfulness of a given review * Returns -1 if there is no review with the given identifier
     */
    public int getReviewHelpfulnessNumerator(int reviewId) {
        return featuresDict.getValue(reviewId, HELP_N);
    }

    /**
     * Returns the denominator for the helpfulness of a given review * Returns -1 if there is no review with the given identifier
     */
    public int getReviewHelpfulnessDenominator(int reviewId) {
        return featuresDict.getValue(reviewId, HELP_D);
    }

    /**
     * Returns the number of tokens in a given review
     * Returns -1 if there is no review with the given identifier
     */
    public int getReviewLength(int reviewId) {
        return featuresDict.getValue(reviewId, REVIEW_LEN);
    }




    /**
     * Return the number of product reviews available in the system
     */
    public int getNumberOfReviews() {
        return tc.getTotalNumOfReviews();
    }

    /**
     * Return the number of tokens in the system
     * (Tokens should be counted as many times as they appear)
     */
    public int getTokenSizeOfReviews() {
        return tc.getTotalNumOfTokens();
    }
}
