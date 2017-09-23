package gr.bcw.business_card_wallet.webservice;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;

import gr.bcw.business_card_wallet.model.Profession;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by konstantinos on 19/9/2017.
 */

public class ProfessionWebServiceImpl implements ProfessionWebService {

    public static final String TAG = ProfessionWebServiceImpl.class.getSimpleName();

    private interface ProfessionAPI {

        @GET(PROFESSION + "/search")
        Call<List<Profession>> searchByName(@Query("name") String name);

    }

    @Override
    public List<Profession> searchByName(String name) throws WebServiceException {

        String message;
        List<Profession> professionList;
        Call<List<Profession>> findByNameCall = ServiceGenerator.createService(ProfessionWebServiceImpl.ProfessionAPI.class).searchByName(name);

        try {
            Response<List<Profession>> response = findByNameCall.execute();
            int responseCode = response.code();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                professionList = response.body();
            } else {

                switch (responseCode) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        throw new WebServiceException("Unauthorized Access");
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        throw new WebServiceException("Profession did not found");
                    case HttpURLConnection.HTTP_CONFLICT:
                        throw new WebServiceException("Server Conflict");
                    case HttpURLConnection.HTTP_NO_CONTENT:
                        throw new WebServiceException("No Professions");
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

        return professionList;
    }
}
