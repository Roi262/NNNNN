package webdata.DictionaryObjects.Tables.Rows;

import java.io.Serializable;

public interface Row extends Serializable {
    String getCompressedBinaryStringPostingList();
    int getLength();
    int getPrefixSize();
}
