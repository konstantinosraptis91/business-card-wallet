package gr.bcw.business_card_wallet.webservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by konstantinos on 15/4/2017.
 */

public class ServiceGenerator {

    // private static final String SERVER_URL = "http://192.168.1.3:8080/api/";
    private static final String SERVER_URL = "http://192.168.1.16:9000/api/";

    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setLenient()
            .create();

    private static Retrofit.Builder builder =  new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson));

    private static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }

}
