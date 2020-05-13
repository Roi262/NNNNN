import webdata.IndexReader;
import webdata.SlowIndexWriter;

import java.io.File;
import java.io.IOException;

public class Main {

    static final String INDICES_DIR_NAME = "indices";
    static final String REVIEWS_FILE_NAME_10 = "10.txt";
    static final String REVIEWS_FILE_NAME_100 = "Small Datasets/100.txt";
    static final String REVIEWS_FILE_NAME_1000 = "Small Datasets/1000.txt";

    public static void main(String[] args) throws Exception {
        final String dir = System.getProperty("user.dir");
        final String indicesDir = dir + File.separatorChar + INDICES_DIR_NAME;

        buildIndex(indicesDir);
        queryMetaData(indicesDir);

        queryReviewMetaData(indicesDir);



        deleteIndex(indicesDir);


    }

    private static void buildIndex(String indicesDir) throws Exception {

        SlowIndexWriter slowIndexWriter = new SlowIndexWriter();
        slowIndexWriter.slowWrite(REVIEWS_FILE_NAME_100, indicesDir);
    }

    private static void queryMetaData(String indicesDir) throws IOException, ClassNotFoundException {
        IndexReader indexReader = new IndexReader();
        System.out.println(indexReader.getNumberOfReviews());
        System.out.println(indexReader.getTokenSizeOfReviews());
    }


    private static void queryReviewMetaData(String indicesDir) throws IOException, ClassNotFoundException {
        IndexReader indexReader = new IndexReader();
        int[] ridsTestCases = {1, 11, 12, 0, -2, 15, 35};
//        int[] ridsTestCases = {1, 11, 12, 999, 1001, 0, -2, 10, 32, 522};
        for (int rid : ridsTestCases) {
            System.out.println("review id: " + rid + " " +
                    indexReader.getProductId(rid) + " " +
                    indexReader.getReviewScore(rid) + " " +
                    indexReader.getReviewHelpfulnessNumerator(rid) + " " +
                    indexReader.getReviewHelpfulnessDenominator(rid) + " " +
                    indexReader.getReviewLength(rid));
        }
    }


    private static void deleteIndex(String indicesDir) {
        SlowIndexWriter slowIndexWriter = new SlowIndexWriter();
        slowIndexWriter.removeIndex(indicesDir);
    }
}
