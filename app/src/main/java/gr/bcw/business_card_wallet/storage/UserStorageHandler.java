package gr.bcw.business_card_wallet.storage;

import gr.bcw.business_card_wallet.model.User;
import io.realm.Realm;

/**
 * Created by konstantinos on 20/3/2017.
 */

public class UserStorageHandler {

    private static final String TAG = UserStorageHandler.class.getSimpleName();

    public void saveUser(Realm realm, long id, String firstName, String lastName) {
        realm.beginTransaction();
        User theUser = realm.createObject(User.class, id);
        theUser.setFirstName(firstName);
        theUser.setLastName(lastName);
        realm.commitTransaction();
    }

    public User findUserById(Realm realm, long id) {
        return realm.where(User.class).equalTo("id", id).findFirst();
    }

    public void updateUser(Realm realm, long id, String firstName, String lastName) {
        User theUser = new User();
        theUser.setId(id);
        theUser.setFirstName(firstName);
        theUser.setLastName(lastName);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(theUser);
        realm.commitTransaction();
    }

}
