package gr.bcw.business_card_wallet.webservice;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.validation.constraints.NotNull;

import gr.bcw.business_card_wallet.model.WalletEntry;
import gr.bcw.business_card_wallet.model.retriever.BusinessCardResponse;
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
 * Created by konstantinos on 5/5/2017.
 */

public class WalletEntryWebServiceImpl implements WalletEntryWebService {

    public static final String TAG = WalletEntryWebServiceImpl.class.getSimpleName();

    private interface WalletEntryAPI {

        @POST(WALLET_ENTRY)
        Call<BusinessCardResponse> saveWalletEntry(@Body WalletEntry entry, @Header(AUTHORIZATION_HEADER_KEY) String authToken);

        @GET(WALLET_ENTRY + "/" + UserWebService.USER + "/" + "{id}")
        Call<List<BusinessCardResponse>> findAllBusinessCardsByUserId(@Path("id") long id, @Header(AUTHORIZATION_HEADER_KEY) String authToken);

        @DELETE(WALLET_ENTRY)
        Call<Void> deleteWalletEntry(@Query("userId") long userId, @Query("businessCardId") long businessCardId, @Header(AUTHORIZATION_HEADER_KEY) String authToken);

    }

    @Override
    public BusinessCardResponse saveWalletEntry(WalletEntry entry, String token) throws WebServiceException {

        String message;
        BusinessCardResponse card;
        Call<BusinessCardResponse> saveWalletEntryCall = ServiceGenerator.createService(WalletEntryAPI.class).saveWalletEntry(entry, token);

        try {
            Response<BusinessCardResponse> response = saveWalletEntryCall.execute();
            int responseCode = response.code();

            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                card = response.body();
            } else {

                switch (responseCode) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        throw new WebServiceException("Unauthorized Access");
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

        return card;
    }

    @Override
    public List<BusinessCardResponse> getWallet(long id, @NotNull String token) throws WebServiceException {

        String message;
        List<BusinessCardResponse> cards;
        Call<List<BusinessCardResponse>> walletCall = ServiceGenerator.createService(WalletEntryAPI.class).findAllBusinessCardsByUserId(id, token);

        try {
            Response<List<BusinessCardResponse>> response = walletCall.execute();
            int responseCode = response.code();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                cards = response.body();
            } else {

                switch (responseCode) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        throw new WebServiceException("Unauthorized Access", HttpURLConnection.HTTP_UNAUTHORIZED);
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        throw new WebServiceException("User's wallet did not found", HttpURLConnection.HTTP_NOT_FOUND);
                    case HttpURLConnection.HTTP_CONFLICT:
                        throw new WebServiceException("Server Conflict", HttpURLConnection.HTTP_CONFLICT);
                    case HttpURLConnection.HTTP_NO_CONTENT:
                        throw new WebServiceException("No business cards in wallet", HttpURLConnection.HTTP_NO_CONTENT);
                    default:
                        throw new WebServiceException("Server returned response code: ", responseCode);
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
    public void deleteWalletEntry(WalletEntry entry, String token) throws WebServiceException {

        String message;
        Call<Void> deleteWalletEntryCall = ServiceGenerator.createService(WalletEntryWebServiceImpl.WalletEntryAPI.class).deleteWalletEntry(entry.getUserId(), entry.getBusinessCardId(), token);

        try {
            Response<Void> response = deleteWalletEntryCall.execute();
            int responseCode = response.code();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                message = "Deleted";
            } else {

                switch (responseCode) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        throw new WebServiceException("Unauthorized Access");
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        throw new WebServiceException("Wallet Entry does not Exist");
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

}
