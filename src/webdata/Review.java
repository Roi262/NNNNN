package webdata;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static webdata.Constants.PrefixConstants.*;

public class Review {

    public static final int NUMERATOR = 0, DENOMINATOR = 1;


    private ArrayList<String> reviewLines;
    private int reviewID;
    private String productID;
    private int score;
    private int helpNumerator;
    private int helpDenominator;
    private ArrayList<String> textTokens;
    private int numOfTokensInReview;


    public Review(int reviewID, ArrayList<String> reviewStr) {
        this.reviewID = reviewID;
        this.reviewLines = reviewStr;
        this.parseReviewStr();
        this.numOfTokensInReview = textTokens.size();
    }

    /*****************GETTTERS*************************/

    public ArrayList<String> getReviewLines() {
        return reviewLines;
    }

    public int getReviewID() {
        return reviewID;
    }

    public String getProductID() {
        return productID;
    }

    public int getScore() {
        return score;
    }

    public int getHelpNumerator() {
        return helpNumerator;
    }

    public int getHelpDenominator() {
        return helpDenominator;
    }

    public ArrayList<String> getTextTokens() {
        return textTokens;
    }

    public int getNumOfTokensInReview() {
        return numOfTokensInReview;
    }

    /******************************************/

    /**
     * Parses the text of a complete review into the class variables
     */
    private void parseReviewStr() {
        for (String line : reviewLines) {
            if (line.startsWith(PROD_ID_PREFIX)) {
                productID = handleProductID(line);

            } else if (line.startsWith(HELP_PREFIX)) {
                Integer[] help = handleHelpfulness(line);
                helpNumerator = help[NUMERATOR];
                helpDenominator = help[DENOMINATOR];

            } else if (line.startsWith(SCORE_PREFIX)) {
                score = handleScore(line);

            } else if (line.startsWith(TEXT_PREFIX)) {
                textTokens = new ArrayList<>(handleText(line));
            }
        }
    }

    private List<String> handleText(String line) {
        line = line.replace(TEXT_PREFIX, "");
        line = line.replaceAll("\\W", " ");
        line = line.toLowerCase();
        return Arrays.asList(line.split(" "));
    }

    private Integer handleScore(String line) {
        line = line.replace(SCORE_PREFIX, "");
        line = line.replace(".0", "");
        return Integer.parseInt(line);
    }

    /**
     * @param line
     * @return helpfullness values
     */
    private Integer[] handleHelpfulness(String line) {
        Integer[] help = new Integer[2];
        line = line.replace(HELP_PREFIX, "");
        line = line.replace("/", "");
        help[NUMERATOR] = Integer.parseInt(String.valueOf(line.charAt(NUMERATOR)));
        help[DENOMINATOR] = Integer.parseInt(String.valueOf(line.charAt(DENOMINATOR)));
        return help;
    }

    private String handleReviewID(String line) {
        return line.replace(REVIEW_ID_PREFIX, "");
    }

    private String handleProductID(String line) {
        return line.replace(PROD_ID_PREFIX, "");
    }

}
