package webdata.Readers;

import webdata.DictionaryObjects.Tables.Rows.Row;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import static webdata.Constants.BinarySearchConstants.LARGER;
import static webdata.Constants.BinarySearchConstants.SMALLER;
import static webdata.Constants.HyperParameters.k;
import static webdata.DictionaryObjects.Tables.PostingLists.DeltaPostingListCompressor.deltaDecode;

public class FCTokenTableReader {
    private final ArrayList<Row> table;
    private final String compressedAllTermStr;

    ArrayList<Integer> currReviewIDs;
    ArrayList<Integer> currFrequencies;

    public FCTokenTableReader(ArrayList<Row> table, String compressedAllTermStr) {
        this.table = table;
        this.compressedAllTermStr = compressedAllTermStr;
    }

    /**
     * Return the number of reviews containing a given token (i.e., word)
     * Returns 0 if there are no reviews containing this token
     */
    public int getTokenFrequency(String token) {
        int rowInd = findRowIndex(token);
        if (rowInd == -1) { // token not found
            return 0;
        }
        Row row = table.get(rowInd);
        ArrayList<Integer> pList = deltaDecode(row.getCompressedBinaryStringPostingList());
        assert pList.size() % 2 == 0;
        return pList.size() / 2;
    }

    /**
     * Return the number of times that a given token (i.e., word) appears in
     * the reviews indexed
     * Returns 0 if there are no reviews containing this token
     */
    public int getTokenCollectionFrequency(String token) {
        if (updateCurrPostingListAndFrequencies(token) == -1) { // token not found
            return 0;
        }
        int totalFreq = 0;
        for (Integer freq : currFrequencies) {
            totalFreq += freq;
        }
        return totalFreq;
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
        if (updateCurrPostingListAndFrequencies(token) == -1) {

//            System.out.println("The word '" + token + "' is not in the database");
            return Collections.emptyEnumeration();
        }
        return Collections.enumeration(mergedPList(currReviewIDs, currFrequencies));
    }

    public Enumeration<Integer> getProductReviews(String productId) throws IOException, ClassNotFoundException, NullPointerException {
        int rowInd = findRowIndex(productId);
        if (rowInd == -1) {
//            System.out.println("The product ID '" + productId + "' is not in the database");
            return Collections.emptyEnumeration();
        }
        Row row = table.get(rowInd);
        String compressedList = row.getCompressedBinaryStringPostingList();
        ArrayList<Integer> pList = resetValuesFromGaps(deltaDecode(compressedList));
        return Collections.enumeration(pList);
    }


    private int updateCurrPostingListAndFrequencies(String token) {
        int rowInd = findRowIndex(token);
        if (rowInd == -1) return -1;
        Row row = table.get(rowInd);
        String compressedList = row.getCompressedBinaryStringPostingList();
        ArrayList<Integer> pList = deltaDecode(compressedList);
        assert pList.size() % 2 == 0;
        currReviewIDs = resetValuesFromGaps(new ArrayList<>(pList.subList(0, pList.size() / 2)));
        currFrequencies = new ArrayList<>(pList.subList(pList.size() / 2, pList.size()));
        return 0;
    }

    private ArrayList<Integer> resetValuesFromGaps(ArrayList<Integer> pList) {
        int prevValue = 0;
        for (int i = 0; i < pList.size(); i++) {
            int realValue = prevValue + pList.get(i);
            pList.set(i, realValue);
            prevValue = realValue;
        }
        return pList;
    }

    /**
     * @param reviewIDs
     * @param frequencies
     * @return merged posting list of ids and their frequencies.
     */
    private ArrayList<Integer> mergedPList(ArrayList<Integer> reviewIDs, ArrayList<Integer> frequencies) {
        assert reviewIDs.size() == frequencies.size();
        ArrayList<Integer> mergedPList = new ArrayList<>();
        for (int i = 0; i < reviewIDs.size(); i++) {
            mergedPList.add(reviewIDs.get(i));
            mergedPList.add(frequencies.get(i));
        }
        return mergedPList;
    }

    /**
     * @param token
     * @return the index of the row in the table that matches the given token
     */
    private int findRowIndex(String token) {

        int currKthRowIndex;

        int numOfRowsWithTermPointers = (table.size() + k - 1) / k;
        int currKthRow = (numOfRowsWithTermPointers / 2);// - 1; // the i'th term pointer
        int logLimit = (int) (Math.log(numOfRowsWithTermPointers) / Math.log(2)) + 1; // = log_2(numofrowswithTPs)+1
        int currKRowsLowerBound = 0;
        int currKRowsUpperBound = numOfRowsWithTermPointers;

        do {
            currKthRowIndex = currKthRow * k;
            int offset = getOffsetInBlock(currKthRowIndex, token);
            if (offset >= 0) {
                return currKthRowIndex + offset;
            }
            if (offset == SMALLER) {
                currKRowsUpperBound = currKthRow;
            } else if (offset == LARGER) {
                currKRowsLowerBound = currKthRow;
            }
            currKthRow = (currKRowsLowerBound + currKRowsUpperBound) / 2;
            logLimit--;
        }
        while (logLimit >= 0);
        return -1;
    }


    /**
     * @param currKthRowIndex
     * @param token
     * @return offset of word in block if the token is in the block,
     * SMALLER/LARGER if word is lexicographically smaller/larger then all words in the block
     */
    private int getOffsetInBlock(int currKthRowIndex, String token) {
        int currStrPtr = table.get(currKthRowIndex).getTermPtr();

        String word;
        String previousTerm = null;

        word = getWord(currKthRowIndex, currStrPtr, 0, previousTerm);
        if (token.compareTo(word) < 0) {
            return SMALLER;
        }


        for (int offset = 0; offset < k; offset++) {
            if (currKthRowIndex + offset >= table.size()) {
                return LARGER;
            }
            word = getWord(currKthRowIndex, currStrPtr, offset, previousTerm);
            if (token.equals(word)) {
                return offset;
            }

            if (offset == 0) {
                currStrPtr += table.get(currKthRowIndex).getLength();
            } else if (offset < k - 1) {
                currStrPtr += table.get(currKthRowIndex + offset).getLength()
                        - table.get(currKthRowIndex + offset).getPrefixSize();
            }
            previousTerm = word;
        }
        return LARGER;
    }

    /**
     * @param currStrPtr current pointer in the long compressed string
     * @param offset     between 1 and k-1
     * @return the word in the kth row + offset row
     */
    private String getWord(int currKthRowIndex, int currStrPtr, int offset, String previousTerm) {
        Row row = table.get(currKthRowIndex + offset);

        String prefix, suffix;
        if (offset == 0) {
            return compressedAllTermStr.substring(currStrPtr, currStrPtr + row.getLength());
        }

        assert previousTerm != null;

        int suffixSize = -1;
        int prefSize = row.getPrefixSize();
        prefix = previousTerm.substring(0, prefSize);

        if (offset < k - 1) {
            suffixSize = row.getLength() - prefSize;
        }
        if (offset == k - 1) {
            int nextTermPtr = currKthRowIndex + k < table.size() ? table.get(currKthRowIndex + k).getTermPtr() : compressedAllTermStr.length() - 1;
            suffixSize = nextTermPtr - currStrPtr;
        }

        suffix = compressedAllTermStr.substring(currStrPtr, currStrPtr + suffixSize);
        return prefix + suffix;
    }
}
