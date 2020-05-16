package webdata.DictionaryObjects.Tables;

import webdata.DictionaryObjects.DictionaryObject;
import webdata.DictionaryObjects.Tables.PostingLists.PostingList;
import webdata.DictionaryObjects.Tables.Rows.Row;
import webdata.DictionaryObjects.Tables.Rows.SerializableKthRow;
import webdata.DictionaryObjects.Tables.Rows.SerializableLastRowInBlock;
import webdata.DictionaryObjects.Tables.Rows.SerializableMidRow;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static webdata.Constants.HyperParameters.k;

public abstract class FCTable implements DictionaryObject {

    TreeMap<String, PostingList> sortedDict; // sorted by key


    /*****************Compressed Table Objects*****************/
    protected ArrayList<Row> serializableTable;
    protected String compressedStringDict;


    /*****************CONSTRUCTOR*****************/
    public FCTable() {
        this.sortedDict = new TreeMap<>();
        this.serializableTable = new ArrayList<>();

    }

    /*****************GETTERS**********************/

    public ArrayList<Row> getSerializableTable() {
        return serializableTable;
    }

    public String getCompressedStringDict() {
        return compressedStringDict;
    }

    /***********************************************/


    public void compressAndCreate() {
        int currIndex = 0;
        int prefixSize;
        int currTermPtr = 0;

        String previousKthTerm = null;
        String term;
        String croppedTerm;
        String compressedPostingList;

        StringBuilder concatStrBuilder = new StringBuilder();

        PostingList postingList;

        for (Map.Entry<String, PostingList> entry : sortedDict.entrySet()) {
            term = entry.getKey();
            postingList = entry.getValue();

//            update serializable table
            compressedPostingList = postingList.getCompressedPostingList();
            prefixSize = FCTable.getPrefixSize(previousKthTerm, term, currIndex);
            Row row = getRow(currIndex, compressedPostingList, term.length(), prefixSize, currTermPtr);
            serializableTable.add(row);

//            update the long string
            croppedTerm = FCTable.cropTerm(prefixSize, term);
            concatStrBuilder.append(croppedTerm);

//            update for next iteration
            previousKthTerm = term;
            currTermPtr += croppedTerm.length();
            currIndex++;
        }
        compressedStringDict = concatStrBuilder.toString();
    }


    /**
     * @return Row object pertaining to type of row
     */
    protected Row getRow(int currIndex, String compressedBinaryStringPostingList, int length, int prefixSize, int termPointer) {
        switch (currIndex % k) {
            case 0: //Kth row
                return new SerializableKthRow(compressedBinaryStringPostingList, length, termPointer);
            case k - 1: //last row in block
                return new SerializableLastRowInBlock(compressedBinaryStringPostingList, prefixSize);
            default: //any middle row
                return new SerializableMidRow(compressedBinaryStringPostingList, length, prefixSize);
        }
    }


    /**
     * @return the prefix size
     */
    protected static int getPrefixSize(String previousKthTerm, String term, int rowIndex) {
        if (rowIndex % k == 0) { // irrelevant if rowIndex is a kth index
            return -1;
        }
        int i = 0;
        while (i < previousKthTerm.length() && i < term.length() && (previousKthTerm.charAt(i) == term.charAt(i))) i++;
        return i;
    }

    /**
     * @param prefixSize
     * @param term
     * @return cropped suffix of term
     */
    protected static String cropTerm(int prefixSize, String term) {
        if (prefixSize != -1) {
            String prefix = term.substring(0, prefixSize);
            assert prefix.length() == prefixSize : "Cropping string at wrong index.";
            term = term.substring(prefixSize);
            return term;
        } else {
            return term;
        }
    }
}
