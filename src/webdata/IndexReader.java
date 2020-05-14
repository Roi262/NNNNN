package webdata;

import webdata.DictionaryObjects.FeaturesDict;
import webdata.DictionaryObjects.Tables.FCTable;
import webdata.DictionaryObjects.Tables.FCTablePIDDict;
import webdata.DictionaryObjects.Tables.Rows.Row;
import webdata.Readers.FCTokenTableReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import static webdata.Constants.Features.*;
import static webdata.Constants.IndexFilePaths.*;
import static webdata.Serializer.ReadObjectFromFile;
import static webdata.SlowIndexWriter.dir;

public class IndexReader {

    /********dictionaries**********/
    private FeaturesDict featuresDict;


//    private ArrayList<Row> PIDTable;
//    private String compressedPIDStringDict;
//
//    private ArrayList<Row> TokenTable;
//    private String compressedTokenStringDict;

    /********FCTable Readers**********/
    private FCTokenTableReader fcTokenTableReader;

    /******************Counter****************/
    private TotalCounts tc;


    /*****************CONSTRUCTOR*****************/
    public IndexReader() throws IOException, ClassNotFoundException {
        this.featuresDict = (FeaturesDict) ReadObjectFromFile(dir + FEATURES_DICT_PATH);
        this.tc = (TotalCounts) ReadObjectFromFile(dir + TOTAL_COUNTS_PATHS);

        initializeFCTokenTableReader();
    }

    private void initializeFCTokenTableReader() throws IOException, ClassNotFoundException {
        ArrayList<Row> table = (ArrayList<Row>) ReadObjectFromFile(dir + SERIALIZABLE_TOKEN_TABLE_PATH);
        String allTermString = (String) ReadObjectFromFile(dir + COMPRESSED_TOKEN_DICT_STRING_PATH);
        this.fcTokenTableReader = new FCTokenTableReader(table, allTermString);
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




    /**
     * Return the number of reviews containing a given token (i.e., word)
     * Returns 0 if there are no reviews containing this token
     */
    public int getTokenFrequency(String token) {
        return fcTokenTableReader.getTokenFrequency(token);
    }

    /**
     * Return the number of times that a given token (i.e., word) appears in
     * the reviews indexed
     * Returns 0 if there are no reviews containing this token
     */
    public int getTokenCollectionFrequency(String token) {
        return fcTokenTableReader.getTokenCollectionFrequency(token);
    }

    /**
     * The Posting List
     * Return a series of integers of the form id-1, freq-1, id-2, freq-2, ... such
     * that id-n is the n-th review containing the given token and freq-n is the
     * number of times that the token appears in review id-n
     * Note that the integers should be sorted by id
     * Returns an empty Enumeration if there are no reviews containing this token
     */
    public Enumeration<Integer> getReviewsWithToken(String token) {
        return fcTokenTableReader.getReviewsWithToken(token);
    }

//    /**
//     * Return the ids of the reviews for a given product identifier
//     * Note that the integers returned should be sorted by id
//     * Returns an empty Enumeration if there are no reviews for this product
//     */
//    public Enumeration<Integer> getProductReviews(String productId) throws IOException, ClassNotFoundException {
//
//    }
}
