package gr.bcw.business_card_wallet.webservice;

import javax.validation.constraints.NotNull;

import gr.bcw.business_card_wallet.model.User;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;

/**
 * Created by konstantinos on 15/4/2017.
 */

public interface UserWebService {

    String AUTHORIZATION_HEADER_KEY = "Authorization";
    String USER = "user";
    String AUTHENTICATE = "authenticate";

    User createUser(@NotNull final User user) throws WebServiceException;

    User authenticate(@NotNull String username, @NotNull String password) throws WebServiceException;

    User findUserById(long userId, @NotNull String token) throws WebServiceException;

    boolean updateUserById(long id, User user, String token) throws WebServiceException;

    boolean deleteUserById(long id, String token) throws WebServiceException;

}
