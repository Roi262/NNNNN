package webdata.DictionaryObjects.Tables.Rows;

/**
 * A serializeable middle row
 */
public class SerializableMidRow implements Row {
    private String compressedBinaryStringPostingList;
    private int length;
    private int prefixSize;

    public SerializableMidRow(String compressedBinaryStringPostingList, int length, int prefixSize) {
        this.compressedBinaryStringPostingList = compressedBinaryStringPostingList;
        this.length = length;
        this.prefixSize = prefixSize;
    }

    @Override
    public String getCompressedBinaryStringPostingList() {
        return compressedBinaryStringPostingList;
    }

    @Override
    public int getLength() {
        return length;
    }
    @Override
    public int getPrefixSize() {
        return prefixSize;
    }

    @Override
    public int getTermPtr() {
        return -1;
    }
}
