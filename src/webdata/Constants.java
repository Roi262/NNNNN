package webdata;

public class Constants {

    public static final String SEPARATOR = "#";

    public static class HyperParameters {
        public static final int k = 4;

    }

    public static class Features {
        public static final int SCORE = 0;
        public static final int HELP_N = 1;
        public static final int HELP_D = 2;
        public static final int REVIEW_LEN = 3;

    }

    public static class IndexFilePaths {
        public static final String FEATURES_DICT_PATH = "/Features_Dictionary";
        public static final String TOTAL_COUNTS_PATHS = "/TC_Counts";

        public static final String SERIALIZABLE_PROD_ID_TABLE_PATH = "/ProdID_Table";
        public static final String COMPRESSED_PROD_ID_DICT_STRING_PATH = "/ProdID_Dictionary_String";
        public static final String SERIALIZABLE_TOKEN_TABLE_PATH = "/Token_Table";
        public static final String COMPRESSED_TOKEN_DICT_STRING_PATH = "/Token_Dictionary_String";
    }


    public static class PrefixConstants {
        public static final String PROD_ID_PREFIX = "product/productId: ";
        public static final String REVIEW_ID_PREFIX = "review/userId: ";
        public static final String PROFILE_NAME_PREFIX = "review/profileName: ";
        public static final String HELP_PREFIX = "review/helpfulness: ";
        public static final String TIME_PREFIX = "review/time: ";
        public static final String SCORE_PREFIX = "review/score: ";
        public static final String SUMMARY_PREFIX = "review/summary: ";
        public static final String TEXT_PREFIX = "review/text: ";
    }

    public static class BinarySearchConstants {
        public static final int SMALLER = -1;
        public static final int LARGER = -2;
    }

    public enum RowType {
        Kth_ROW,
        MIDDLE,
        LAST
    }


}
