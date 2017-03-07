package gr.bcw.business_card_wallet.webservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import gr.bcw.business_card_wallet.SplashActivity;
import gr.bcw.business_card_wallet.model.User;
import gr.bcw.business_card_wallet.util.Constant;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

        @GET(Constant.USER)
        Call<List<User>> getUser(@Header(Constant.AUTHORIZATION_HEADER_KEY) String credentials,
                                 @Query("email") String email,
                                 @Query("firstName") String firstName,
                                 @Query("lastName") String lastName);

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

    public Call<List<User>> getUser(String username, String password) {
        Call<List<User>> getUserCall = service
                .getUser(Credentials.basic(username, password), null, null, null);
        return getUserCall;
    }

    public Call<User> getUserByEmail(String email) {
        return null;
    }

    public Call<User> getUserByFirstName(String firstName) {
        return null;
    }

    public Call<User> getUserByLastName(String lastName) {
        return null;
    }

    public Call<User> getUserByName(String firstName, String lastName) {
        return null;
    }

}
