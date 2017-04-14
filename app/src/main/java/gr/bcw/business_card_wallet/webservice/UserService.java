package gr.bcw.business_card_wallet.webservice;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gr.bcw.business_card_wallet.SplashActivity;
import gr.bcw.business_card_wallet.model.User;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by konstantinos on 6/3/2017.
 */

public class UserService extends WebService {

    public static final String TAG = SplashActivity.class.getSimpleName();

    protected static final String USER = "user";
    protected static final String AUTHENTICATE = "authenticate";

    private final Retrofit retrofit;
    private final UserAPI service;

    private interface UserAPI {

        @POST(USER)
        Call<String> saveUser(@Body User user);

        @POST(USER + "/" + AUTHENTICATE)
        Call<String> authenticate(@Header(AUTHORIZATION_HEADER_KEY) String credentials);

        @GET(USER + "/" + "{id}")
        Call<User> findUserById(@Path("id") long id, @Header(AUTHORIZATION_HEADER_KEY) String authToken);

        @PUT(USER + "/" + "{id}")
        Call<Void> updateUser(@Path("id") long id, @Body User user, @Header(AUTHORIZATION_HEADER_KEY) String authToken);

    }

    public UserService(Context context) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        service = retrofit.create(UserAPI.class);
    }

    public Call<String> saveUser(User user) {
        Call<String> saveUserCall = service.saveUser(user);
        return saveUserCall;
    }

    public Call<String> authenticate(String username, String password) {
        Call<String> authenticateCall = service.authenticate(Credentials.basic(username, password));
        return authenticateCall;
    }

    public Call<User> findUserById(long id, String token) {
        Call<User> findUserByIdCall = service.findUserById(id, token);
        return findUserByIdCall;
    }

    public Call<Void> updateUser(long id, User user, String token) {
        Call<Void> updateUserCall = service.updateUser(id, user, token);
        return updateUserCall;
    }

}
