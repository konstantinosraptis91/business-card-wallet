package gr.bcw.business_card_wallet.webservice;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.validation.constraints.NotNull;

import gr.bcw.business_card_wallet.model.BusinessCard;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by konstantinos on 18/4/2017.
 */

public class BusinessCardWebServiceImpl implements BusinessCardWebService {

    public static final String TAG = BusinessCardWebServiceImpl.class.getSimpleName();

    private interface BusinessCardAPI {

        @POST(BUSINESS_CARD)
        Call<Void> createBusinessCard(@Body BusinessCard businessCard, @Header(AUTHORIZATION_HEADER_KEY) String authToken);

        @GET(BUSINESS_CARD + "/" + UserWebService.USER + "/" + "{id}")
        Call<List<BusinessCard>> findByUserId(@Path("id") long id, @Header(AUTHORIZATION_HEADER_KEY) String authToken);

        @GET(BUSINESS_CARD + "/" + UserWebService.USER)
        Call<List<BusinessCard>> findByUserName(@Query("firstname") String firstName, @Query("lastname") String lastName);

        @DELETE(BUSINESS_CARD + "/" + "{id}")
        Call<Void> deleteBusinessCardById(@Path("id") long id, @Header(AUTHORIZATION_HEADER_KEY) String authToken);

    }

    @Override
    public BusinessCard createBusinessCard(@NotNull BusinessCard businessCard, String token) throws WebServiceException {

        String message;
        Call<Void> createBusinessCardCall = ServiceGenerator.createService(BusinessCardAPI.class).createBusinessCard(businessCard, token);

        try {
            Response<Void> response = createBusinessCardCall.execute();
            int responseCode = response.code();

            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                String location = response.headers().get("Location");
                long id = extractBusinessCardId(location);

                businessCard.setId(id);

            } else {

                switch (responseCode) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        throw new WebServiceException("Unauthorized Access");
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        throw new WebServiceException("Business Card Owner (User) does not Exist");
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

        return businessCard;
    }

    @Override
    public List<BusinessCard> findByUserId(long id, String token) throws WebServiceException {

        String message;
        List<BusinessCard> cards;
        Call<List<BusinessCard>> findByUserIdCall = ServiceGenerator.createService(BusinessCardAPI.class).findByUserId(id, token);

        try {
            Response<List<BusinessCard>> response = findByUserIdCall.execute();
            int responseCode = response.code();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                cards = response.body();
            } else {

                switch (responseCode) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        throw new WebServiceException("Unauthorized Access");
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        throw new WebServiceException("User did not found");
                    case HttpURLConnection.HTTP_CONFLICT:
                        throw new WebServiceException("Server Conflict");
                    case HttpURLConnection.HTTP_NO_CONTENT:
                        throw new WebServiceException("No business cards");
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

        return cards;
    }

    @Override
    public List<BusinessCard> findByUserName(String firstName, String lastName) throws WebServiceException {

        String message;
        List<BusinessCard> cards;
        Call<List<BusinessCard>> findByUserNameCall = ServiceGenerator.createService(BusinessCardAPI.class).findByUserName(firstName, lastName);

        try {
            Response<List<BusinessCard>> response = findByUserNameCall.execute();
            int responseCode = response.code();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                cards = response.body();
            } else {

                switch (responseCode) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        throw new WebServiceException("Unauthorized Access");
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        throw new WebServiceException("User did not found");
                    case HttpURLConnection.HTTP_CONFLICT:
                        throw new WebServiceException("Server Conflict");
                    case HttpURLConnection.HTTP_NO_CONTENT:
                        throw new WebServiceException("No business cards");
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

        return cards;

    }

    @Override
    public void deleteBusinessCardById(long id, String token) throws WebServiceException {

        String message;
        Call<Void> deleteBusinessCardByIdCall = ServiceGenerator.createService(BusinessCardAPI.class).deleteBusinessCardById(id, token);

        try {
            Response<Void> response = deleteBusinessCardByIdCall.execute();
            int responseCode = response.code();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                message = "Deleted";
            } else {

                switch (responseCode) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        throw new WebServiceException("Unauthorized Access");
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        throw new WebServiceException("Business Card does not Exist");
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

    }

    private long extractBusinessCardId(String location) {
        String[] results = location.split("/");
        long id = Long.parseLong(results[results.length - 1]);
        return id;
    }

}
