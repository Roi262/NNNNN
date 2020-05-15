package webdata;

import webdata.DictionaryObjects.FeaturesDict;
import webdata.DictionaryObjects.Tables.FCTablePIDDict;
import webdata.DictionaryObjects.Tables.FCTableToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static webdata.Constants.IndexFilePaths.*;
import static webdata.Constants.PrefixConstants.*;

public class SlowIndexWriter {


    /****************** File reading objects ******************/
    public static String dir;
    private File dataFile;
    private BufferedReader reader;

    /****************** Dictionary objects ******************/
    private FeaturesDict featuresDict;
    private FCTableToken fcTableTokenDict;
    private FCTablePIDDict fcTablePIDDict;

    /****************** Counters ******************/
    TotalCounts totalCounts;


    public SlowIndexWriter() {
        this.featuresDict = new FeaturesDict();
        this.fcTableTokenDict = new FCTableToken();
        this.fcTablePIDDict = new FCTablePIDDict();
        this.totalCounts = new TotalCounts();
    }


    /**
     * Given product review data, creates an on disk index
     * inputFile is the path to the file containing the review data
     * dir is the directory in which all index files will be created
     * if the directory does not exist, it should be created
     */
    public void slowWrite(String inputFile, String dir) throws Exception {
        this.dir = dir;
//        Constants.IndexFilePaths.initializeIndexFilePaths(dir);
        createDirectory();
        readIntoMainMem(inputFile);

        compressAndCreateTables();
        serializeToDisk();
    }

    /**
     * creates a directory with the given path
     */
    private void createDirectory() {
        File f = new File(dir);
        if (!(f.exists() && f.isDirectory())) {
            new File(dir).mkdirs();
        }
    }

    /**
     * read the contents of the input file into the main memory
     *
     * @param inputFile
     */
    private void readIntoMainMem(String inputFile) throws IOException {
//        initialize file reader
        this.dataFile = new File(inputFile);
        this.reader = new BufferedReader(new FileReader(dataFile));

        readAllReviews();
    }


    /**
     * read all the reviews.
     * for each review, update the dictionary objects and counters.
     *
     * @throws IOException
     */
    private void readAllReviews() throws IOException {
//        read review into new review object
        int reviewID = 1;
        Review review;
        while (true) {
            review = readNextReview(reviewID);
            if (review == null) break;

            featuresDict.update(review);
            fcTableTokenDict.update(review);
            fcTablePIDDict.update(review);
            totalCounts.updateTotalNumOfTokens(review.getNumOfTokensInReview());

            reviewID++;
        }
        totalCounts.setTotalNumOfReviews(reviewID);
    }

    private Review readNextReview(int reviewID) throws IOException {
        ArrayList<String> reviewLines = new ArrayList<>();

        String line = reader.readLine();
        if (line == null) return null;

        while (!line.equals("")) {
            if (!unnecessaryValue(line)){
                reviewLines.add(line);
            }

            line = reader.readLine();

            if (line == null) break;
        }
        if (reviewLines.isEmpty()){
            return null;
        }
        return new Review(reviewID, reviewLines);
    }

    /**
     * @param line
     * @return True if the line is redundant, false otherwise
     */
    private Boolean unnecessaryValue(String line) {
        return line.startsWith(PROFILE_NAME_PREFIX) | line.startsWith(TIME_PREFIX) |
                line.startsWith(SUMMARY_PREFIX) | line.startsWith(REVIEW_ID_PREFIX);
    }


    private void compressAndCreateTables() {
        fcTablePIDDict.compressAndCreate();
        fcTableTokenDict.compressAndCreate();
    }


    private void serializeToDisk() throws Exception {
//        serialize tc
        Serializer.WriteObjectToFile(totalCounts, dir + TOTAL_COUNTS_PATHS);

//        serialize featuresDict
        Serializer.WriteObjectToFile(featuresDict, dir + FEATURES_DICT_PATH);

//        Serialize fcTable product dict and string
        Serializer.WriteObjectToFile(
                fcTablePIDDict.getSerializableTable(), dir + SERIALIZABLE_PROD_ID_TABLE_PATH);

        Serializer.WriteObjectToFile(
                fcTablePIDDict.getCompressedStringDict(), dir + COMPRESSED_PROD_ID_DICT_STRING_PATH);

//        Serialize fcTable Token dict and string
        Serializer.WriteObjectToFile(
                fcTableTokenDict.getSerializableTable(), dir + SERIALIZABLE_TOKEN_TABLE_PATH);

        Serializer.WriteObjectToFile(
                fcTableTokenDict.getCompressedStringDict(), dir + COMPRESSED_TOKEN_DICT_STRING_PATH);


        int k = 0;

    }


    /**
     * Delete all index files by removing the given directory
     */
    public void removeIndex(String dir) {
        File fileDir = new File(dir);
        File[] files = (fileDir).listFiles();
        if(files != null) {
            for (final File file : files) {
                file.delete();
            }
        }
        fileDir.delete();
    }
}