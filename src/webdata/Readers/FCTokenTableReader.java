package webdata.Readers;

import webdata.DictionaryObjects.Tables.Rows.Row;
import webdata.DictionaryObjects.Tables.Rows.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import static webdata.Constants.BinarySearchConstants.LARGER;
import static webdata.Constants.BinarySearchConstants.SMALLER;
import static webdata.Constants.HyperParameters.k;
import static webdata.DictionaryObjects.Tables.PostingLists.DeltaPostingListCompressor.deltaDecode;

public class FCTokenTableReader {
    private final ArrayList<Row> table;
    private final String allTermString;

    ArrayList<Integer> currReviewIDs;
    ArrayList<Integer> currFrequencies;

    public FCTokenTableReader(ArrayList<Row> table, String allTermString) {
        this.table = table;
        this.allTermString = allTermString;
    }

    /**
     * Return the number of reviews containing a given token (i.e., word)
     * Returns 0 if there are no reviews containing this token
     */
    public int getTokenFrequency(String token) {
        int rowInd = findRowIndex(token);
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
        updateCurrPostingListAndFrequencies(token);
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
        updateCurrPostingListAndFrequencies(token);
        return Collections.enumeration(mergedPList(currReviewIDs, currFrequencies));
    }

    private void updateCurrPostingListAndFrequencies(String token) {
        int rowInd = findRowIndex(token);
        Row row = table.get(rowInd);
        String compressedList = row.getCompressedBinaryStringPostingList();
        ArrayList<Integer> pList = deltaDecode(compressedList);
        assert pList.size() % 2 == 0;
        currReviewIDs = resetValuesFromGaps(new ArrayList<>(pList.subList(0, pList.size() / 2)));
        currFrequencies = new ArrayList<>(pList.subList(pList.size() / 2, pList.size()));
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
        int numOfRowsWithTermPointers = table.size() / k;
        int currKthRow = numOfRowsWithTermPointers / 2; // the i'th term pointer

        int currRowIndex = currKthRow * k;

        SerializableKthRow kTermRow = (SerializableKthRow) table.get(currRowIndex);
        int currTermPtrValue = kTermRow.getTermPtr();

        int termLength = kTermRow.getLength();
        String currKTerm = allTermString.substring(currTermPtrValue, currTermPtrValue + termLength);

        while (currRowIndex + k < allTermString.length()) {


            int offset = wordInBlock(currRowIndex, token, currKTerm, currTermPtrValue);
            if (offset >= 0) {
                return currRowIndex + offset;
            }
            if (offset == SMALLER) {
                currKthRow /= 2;
            } else if (offset == LARGER) {
                currKthRow += currKthRow / 2;
            }
            currRowIndex = currKthRow * k;
            kTermRow = (SerializableKthRow) table.get(currRowIndex);
            currTermPtrValue = kTermRow.getTermPtr();
            termLength = kTermRow.getLength();
            currKTerm = allTermString.substring(currTermPtrValue, currTermPtrValue + termLength);
        }
        throw new NoSuchElementException();


    }



    /**
     * @param KthRowIndex
     * @param token
     * @return index of offset in block if word is in K block
     * else smaller or larger (lexicographic) than the words in the block
     */
    private int wordInBlock(int KthRowIndex, String token, String KTerm, int currTermPtrValue) {

        if (KTerm.equals(token)) return 0;
        if (token.compareTo(KTerm) < 0) return SMALLER;

        int nextKthRowIndex = KthRowIndex + k;
        int currStrPtr = currTermPtrValue + KTerm.length();
        int currTermLength;
        Row row;
        for (int i = 1; i < k; i++) {
            row = table.get(KthRowIndex + i);
            if (i == k - 1) {
                currTermLength = ((SerializableKthRow) table.get(nextKthRowIndex)).getTermPtr() - currStrPtr;
            } else {
                currTermLength = row.getLength();
            }
            String prefix = KTerm.substring(0, row.getPrefixSize());
            int suffixLength = currTermLength - row.getPrefixSize();
            String suffix = allTermString.substring(currStrPtr, suffixLength);
            if (token.equals(prefix + suffix)) {
                return i;
            }
            currStrPtr += currTermLength;
        }
        return LARGER;
    }
}
