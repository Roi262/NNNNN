package webdata.DictionaryObjects.Tables.Rows;

public class SerializableKthRow implements Row {
    //    private int freq;
    private String compressedBinaryStringPostingList;
    private int length;

    public SerializableKthRow(String compressedBinaryStringPostingList, int length) {
        this.compressedBinaryStringPostingList = compressedBinaryStringPostingList;
        this.length = length;
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
        return -1;
    }
}
