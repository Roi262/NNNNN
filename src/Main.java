import webdata.IndexReader;
import webdata.Serializer;
import webdata.SlowIndexWriter;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import static webdata.Constants.PrefixConstants.*;

public class Main {

    static final String INDICES_DIR_NAME = "indices";
    static final String REVIEWS_FILE_NAME_1 = "Small Datasets/1.txt";
    static final String REVIEWS_FILE_NAME_10 = "Small Datasets/10.txt";
    static final String REVIEWS_FILE_NAME_100 = "Small Datasets/100.txt";
    static final String REVIEWS_FILE_NAME_1000 = "Small Datasets/1000.txt";

    public static void main(String[] args) throws Exception {
        final String dir = System.getProperty("user.dir");
        final String indicesDir = dir + File.separatorChar + INDICES_DIR_NAME;

        buildIndex(indicesDir);

//        queryMetaData(indicesDir);
//        queryReviewMetaData(indicesDir);

//        queryConcatDictTester(indicesDir);

        queryWordIndex(indicesDir);

//        queryProductIndex(indicesDir);

        deleteIndex(indicesDir);
    }


    private static void buildIndex(String indicesDir) throws Exception {
        SlowIndexWriter slowIndexWriter = new SlowIndexWriter();
        slowIndexWriter.slowWrite(REVIEWS_FILE_NAME_1, indicesDir);
//        slowIndexWriter.slowWrite(REVIEWS_FILE_NAME_1000, indicesDir);
    }

//    private static void queryConcatDictTester(String indicesDir) throws IOException, ClassNotFoundException {
//        IndexReader indexReader = new IndexReader();
//
//        Serializer.WriteObjectToFile();
//
//    }


    private static void queryMetaData(String indicesDir) throws IOException, ClassNotFoundException {
        IndexReader indexReader = new IndexReader();
        System.out.println(indexReader.getNumberOfReviews());
        System.out.println(indexReader.getTokenSizeOfReviews());
    }


    private static void queryReviewMetaData(String indicesDir) throws IOException, ClassNotFoundException {
        IndexReader indexReader = new IndexReader();
        int[] ridsTestCases = {1, 11, 12, 0, 15, 35};
//        int[] ridsTestCases = {1, 11, 12, 999, 1001, 0, -2, 10, 32, 522};
        for (int rid : ridsTestCases) {
            System.out.println(
                    REVIEW_ID_PREFIX  + rid + "\n" +
                    PROD_ID_PREFIX + indexReader.getProductId(rid) + "\n" +
                    SCORE_PREFIX + indexReader.getReviewScore(rid) + "\n" +
                    HELP_PREFIX + indexReader.getReviewHelpfulnessNumerator(rid) + "/" +
                    indexReader.getReviewHelpfulnessDenominator(rid) + "\n" +
                    "Review length: " + indexReader.getReviewLength(rid) +"\n\n");
        }
    }


    private static void queryWordIndex(String indicesDir) throws IOException, ClassNotFoundException {
        IndexReader indexReader = new IndexReader();
        String[] wordTestCases = {"0", "jezer", "roi"};
//        String[] wordTestCases = {"0", "bulba", "zzz", "1", "9oz", "a", "crunchy", "how", "laxative",
//                "prefer", "storebought", "zucchini", "the"};
        testGetReviewsWithToken(indexReader, wordTestCases);

        testGetTokenFrequency(indexReader, wordTestCases);
        testGetTokenCollectionFrequency(indexReader, wordTestCases);

    }




    private static void testGetReviewsWithToken(IndexReader indexReader,
                                                String[] wordTestCases) {
        System.out.println("Checking getReviewsWithToken...");
        for (String word : wordTestCases) {
            Enumeration<Integer> res = indexReader.getReviewsWithToken(word);
            if (res == null) continue;
            System.out.print(word + ": " + System.lineSeparator());
            while (res.hasMoreElements()) {
                System.out.print(res.nextElement().toString() + " ");
            }
            System.out.println();
        }
    }

    private static void testGetTokenFrequency(IndexReader indexReader,
                                              String[] wordTestCases) {
        System.out.println("Checking getTokenFrequency...");
        for (String word : wordTestCases) {
            int numOfReviews = indexReader.getTokenFrequency(word);
            System.out.println(word + ": " + "numOfReviews: " + numOfReviews);
        }
    }

    private static void testGetTokenCollectionFrequency(IndexReader indexReader,
                                                        String[] wordTestCases) {
        System.out.println("Checking getTokenCollectionFrequency...");
        for (String word : wordTestCases) {
            int numOfMentions = indexReader.getTokenCollectionFrequency(word);
            System.out.println(word + ": " + "numOfMentions: " + numOfMentions);
        }
    }




//    private static void queryProductIndex(String indicesDir) throws IOException, ClassNotFoundException {
//        IndexReader indexReader = new IndexReader();
//        String[] productTestCases = {"A009ASDF5", "B099ASDF5", "B0001PB9FE", "B0002567IW", "B000ER6YO0",
//                "B000G6RYNE", "B006F2NYI2", "B009HINRX8", "B001E4KFG0"};
//        for (String pid : productTestCases) {
//            Enumeration<Integer> res = indexReader.getProductReviews(pid);
//            System.out.print(pid + ": " + System.lineSeparator());
//            while (res.hasMoreElements()) {
//                System.out.print(res.nextElement().toString() + " ");
//            }
//            System.out.println();
//        }
//    }

    private static void deleteIndex(String indicesDir) {
        SlowIndexWriter slowIndexWriter = new SlowIndexWriter();
        slowIndexWriter.removeIndex(indicesDir);
    }
}
