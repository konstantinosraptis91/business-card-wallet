package gr.bcw.business_card_wallet.webservice;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;

import gr.bcw.business_card_wallet.model.Company;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by konstantinos on 19/9/2017.
 */

public class CompanyWebServiceImpl implements CompanyWebService {

    public static final String TAG = CompanyWebServiceImpl.class.getSimpleName();

    private interface CompanyAPI {

        @GET(COMPANY)
        Call<List<Company>> findByName(@Query("name") String name);

    }

    @Override
    public List<Company> findByName(String name) throws WebServiceException {
        String message;
        List<Company> companyList;
        Call<List<Company>> findByNameCall = ServiceGenerator.createService(CompanyWebServiceImpl.CompanyAPI.class).findByName(name);

        try {
            Response<List<Company>> response = findByNameCall.execute();
            int responseCode = response.code();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                companyList = response.body();
            } else {

                switch (responseCode) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        throw new WebServiceException("Unauthorized Access");
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        throw new WebServiceException("User did not found");
                    case HttpURLConnection.HTTP_CONFLICT:
                        throw new WebServiceException("Server Conflict");
                    case HttpURLConnection.HTTP_NO_CONTENT:
                        throw new WebServiceException("No Companies");
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

        return companyList;
    }
}
