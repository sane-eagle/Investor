package org.meicode.investor;
import com.google.gson.annotations.SerializedName;

public class StockResponse {
    @SerializedName("c") public double currentPrice;
    @SerializedName("dp") public double percentChange;
    @SerializedName("h") public double highPrice;
    @SerializedName("l") public double lowPrice;
}