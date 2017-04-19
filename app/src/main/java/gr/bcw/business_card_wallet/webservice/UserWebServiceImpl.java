package gr.bcw.business_card_wallet.webservice;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;

import javax.validation.constraints.NotNull;

import gr.bcw.business_card_wallet.model.User;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by konstantinos on 6/3/2017.
 */

public class UserWebServiceImpl implements UserWebService {

    public static final String TAG = UserWebServiceImpl.class.getSimpleName();

    private interface UserAPI {

        @POST(USER)
        Call<String> createUser(@Body User user);

        @POST(USER + "/" + AUTHENTICATE)
        Call<String> authenticate(@Header(AUTHORIZATION_HEADER_KEY) String credentials);

        @GET(USER + "/" + "{id}")
        Call<User> findUserById(@Path("id") long id, @Header(AUTHORIZATION_HEADER_KEY) String authToken);

        @PUT(USER + "/" + "{id}")
        Call<Void> updateUser(@Path("id") long id, @Body User user, @Header(AUTHORIZATION_HEADER_KEY) String authToken);

    }

    @Override
    public User createUser(@NotNull User user) throws WebServiceException {

        String message;
        Call<String> createUserCall = ServiceGenerator.createService(UserAPI.class).createUser(user);

        try {
            Response<String> response = createUserCall.execute();
            int responseCode = response.code();

            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                String token = response.body();
                String location = response.headers().get("Location");
                long id = extractUserId(location);

                user.setId(id);
                user.setToken(token);

            } else {

                switch (responseCode) {
                    case HttpURLConnection.HTTP_CONFLICT:
                        throw new WebServiceException("User with email " + user.getEmail() + " already exist");
                    default:
                        throw new WebServiceException("Server returned response code: " + responseCode);
                }

            }

        } catch (IOException ex) {
            if (ex instanceof SocketTimeoutException) {
                message = "Connection Time out. Please try again.";
            } else {
                message = ex.getMessage();
            }
            throw new WebServiceException(message);
        }

        return user;
    }

    @Override
    public User authenticate(@NotNull String username, @NotNull String password) throws WebServiceException {

        User theUser;
        String message;
        Call<String> authenticateCall = ServiceGenerator.createService(UserAPI.class).authenticate(Credentials.basic(username, password));


        try {
            Response<String> response = authenticateCall.execute();
            int responseCode = response.code();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String token = response.body();
                String location = response.headers().get("Location");
                long id = extractUserId(location);

                theUser = new User();
                theUser.setId(id);
                theUser.setToken(token);

            } else {

                if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new WebServiceException("Unauthorized Access");
                }

                throw new WebServiceException("Server returned response code: " + responseCode);
            }

        } catch (IOException ex) {
            if (ex instanceof SocketTimeoutException) {
                message = "Connection Time out. Please try again.";
            } else {
                message = ex.getMessage();
            }
            throw new WebServiceException(message);
        }

        return theUser;
    }

    @Override
    public User findUserById(long userId, @NotNull String token) throws WebServiceException {

        User theUser;
        String message;
        Call<User> findUserByIdCall = ServiceGenerator.createService(UserAPI.class).findUserById(userId, token);

        try {
            Response<User> response = findUserByIdCall.execute();
            int responseCode = response.code();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                theUser = response.body();
            } else {

                switch (responseCode) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        throw new WebServiceException("Unauthorized Access");
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        throw new WebServiceException("User does not Exist");
                    case HttpURLConnection.HTTP_CONFLICT:
                        throw new WebServiceException("Server Conflict");
                    default:
                        throw new WebServiceException("Server returned response code: " + responseCode);
                }
            }

        } catch (IOException ex) {
            if (ex instanceof SocketTimeoutException) {
                message = "Connection Time out. Please try again.";
            } else {
                message = ex.getMessage();
            }
            throw new WebServiceException(message);
        }

        return theUser;
    }

    @Override
    public boolean updateUserById(long id, User user, String token) throws WebServiceException {

        boolean result;
        String message;
        Call<Void> updateUserCall = ServiceGenerator.createService(UserAPI.class).updateUser(id, user, token);

        try {
            Response<Void> response = updateUserCall.execute();
            int responseCode = response.code();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                result = true;
            } else {

                switch (responseCode) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        throw new WebServiceException("Unauthorized Access");
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        throw new WebServiceException("User does not Exist");
                    case HttpURLConnection.HTTP_CONFLICT:
                        // throw new WebServiceException("Server Conflict");
                        throw new WebServiceException("email not available");
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        throw new WebServiceException("Bad Request");
                    default:
                        throw new WebServiceException("Server returned response code: " + responseCode);
                }
            }

        } catch (IOException ex) {
            if (ex instanceof SocketTimeoutException) {
                message = "Connection Time out. Please try again.";
            } else {
                message = ex.getMessage();
            }
            throw new WebServiceException(message);
        }

        return result;
    }

    private long extractUserId(String location) {
        String[] results = location.split("/");
        long id = Long.parseLong(results[results.length - 1]);
        return id;
    }

}
