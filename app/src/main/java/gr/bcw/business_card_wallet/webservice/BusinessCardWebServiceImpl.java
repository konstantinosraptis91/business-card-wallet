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
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by konstantinos on 18/4/2017.
 */

public class BusinessCardWebServiceImpl implements BusinessCardWebService {

    public static final String TAG = BusinessCardWebServiceImpl.class.getSimpleName();

    private interface BusinessCardAPI {

        @POST(BUSINESS_CARD)
        Call<Void> createBusinessCard(@Body BusinessCard businessCard);

        @GET(BUSINESS_CARD + "/" + UserWebService.USER + "/" + "{id}")
        Call<List<BusinessCard>> findByUserId(@Path("id") long id, @Header(AUTHORIZATION_HEADER_KEY) String authToken);

    }

    @Override
    public BusinessCard createBusinessCard(@NotNull BusinessCard businessCard) throws WebServiceException {

        String message;
        Call<Void> createBusinessCardCall = ServiceGenerator.createService(BusinessCardAPI.class).createBusinessCard(businessCard);

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
        Call<List<BusinessCard>> walletCall = ServiceGenerator.createService(BusinessCardAPI.class).findByUserId(id, token);

        try {
            Response<List<BusinessCard>> response = walletCall.execute();
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

    private long extractBusinessCardId(String location) {
        String[] results = location.split("/");
        long id = Long.parseLong(results[results.length - 1]);
        return id;
    }

}
