package webdata.DictionaryObjects.Tables.PostingLists;


import java.util.ArrayList;

public class DeltaPostingListCompressor {

    /**
     * compresses posting List
     */
    public static String compressList(ArrayList<Integer> list) {
        StringBuilder str = new StringBuilder();
        for (Integer integer : list) {
            str.append(deltaEncode(integer));
        }
        return str.toString();
    }

    public static ArrayList<Integer> deltaDecode(String encoding) {
        ArrayList<Integer> decodedList = new ArrayList<>();
        while (encoding.length() > 0) {
            int firstLen = decodeUnary(encoding);
            encoding = encoding.substring(firstLen);
            int secondLen = decodeOffset(firstLen, encoding);
            encoding = encoding.substring(firstLen - 1);
            int num = decodeOffset(secondLen, encoding);
            encoding = encoding.substring(secondLen - 1);
            decodedList.add(num);
        }
        return decodedList;
    }

    static Integer decodeOffset(int length, String encoding) {
        String binaryString = "1" + encoding.substring(0, length - 1);
        return Integer.parseInt(binaryString, 2);
    }


    /**
     * @param num
     * @return binary string of encoded delta value of num
     */
    static String deltaEncode(Integer num) {
        String numInBinary = Integer.toBinaryString(num);
        int len = numInBinary.length();
        return gammaEncode(len) + getOffset(numInBinary);
    }

    static String gammaEncode(Integer num) {
        String numInBinary = Integer.toBinaryString(num);
        return getGammaLengthInUnary(numInBinary) + getOffset(numInBinary);
    }

    static String getGammaLengthInUnary(String num) {
        int len = num.length();
        return getUnary(len);
    }

    static String getOffset(String num) {
        return num.substring(1);
    }


    static String getUnary(int num) {
        return "1".repeat(Math.max(0, num - 1)) + "0";
    }

    static int decodeUnary(String encoding) {
        int i = 0;
        while (encoding.charAt(i) != '0') {
            i++;
        }
        i++;
        return i;
    }
}
