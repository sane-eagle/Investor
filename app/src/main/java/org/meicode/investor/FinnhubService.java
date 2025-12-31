package org.meicode.investor;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FinnhubService {
    // This endpoint gets real-time quotes (price, change, etc.)
    @GET("quote")
    Call<StockResponse> getQuote(
            @Query("symbol") String symbol,
            @Query("token") String apiKey
    );
}