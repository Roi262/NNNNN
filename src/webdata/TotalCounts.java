package webdata;

import java.io.Serializable;

public class TotalCounts implements Serializable {
    public static final long getSerialversionUID = 1L;

    private int totalNumOfReviews;
    private int totalNumOfTokens;

    public TotalCounts(){
        this.totalNumOfReviews = 0;
        this.totalNumOfTokens = 0;
    }

    public void setTotalNumOfReviews(int totalNumOfReviews) {
        this.totalNumOfReviews = totalNumOfReviews;
    }

    public void updateTotalNumOfTokens(int totalNumOfTokens) {
        this.totalNumOfTokens += totalNumOfTokens;
    }
    public int getTotalNumOfReviews() {
        return totalNumOfReviews;
    }

    public int getTotalNumOfTokens() {
        return totalNumOfTokens;
    }

    @Override
    public String toString() {
        return new StringBuffer(this.totalNumOfReviews)
                .append(this.totalNumOfTokens).toString();
    }
}
