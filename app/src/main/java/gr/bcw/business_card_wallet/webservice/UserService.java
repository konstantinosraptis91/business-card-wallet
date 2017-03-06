package gr.bcw.business_card_wallet.webservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gr.bcw.business_card_wallet.SplashActivity;
import gr.bcw.business_card_wallet.model.User;
import gr.bcw.business_card_wallet.util.Constant;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by konstantinos on 6/3/2017.
 */

public class UserService {

    public static final String TAG = SplashActivity.class.getSimpleName();

    private final Retrofit retrofit;
    private final UserAPI service;

    private interface UserAPI {
        @POST(Constant.USER)
        Call<User> saveUser(@Body User user);
    }

    public UserService() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        service = retrofit.create(UserAPI.class);
    }

    public Call<User> saveUser(User user) {
        Call<User> saveUserCall = service
                .saveUser(user);
        return saveUserCall;
    }

}
