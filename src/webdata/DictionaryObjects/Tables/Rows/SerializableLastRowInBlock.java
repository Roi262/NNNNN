package webdata.DictionaryObjects.Tables.Rows;

public class SerializableLastRowInBlock implements Row {
    private String compressedBinaryStringPostingList;
    private int prefixSize;

    public SerializableLastRowInBlock(String compressedBinaryStringPostingList, int prefixSize) {
        this.compressedBinaryStringPostingList = compressedBinaryStringPostingList;
        this.prefixSize = prefixSize;
    }

    @Override
    public String getCompressedBinaryStringPostingList() {
        return compressedBinaryStringPostingList;
    }

    @Override
    public int getLength() {
        return -1;
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

