package gr.bcw.business_card_wallet.storage;

import android.content.Context;
import android.support.annotation.Nullable;

import gr.bcw.business_card_wallet.model.User;
import io.realm.Realm;

/**
 * Created by konstantinos on 20/3/2017.
 */

public class UserStorageHandler {

    public synchronized void saveUser(Context context, long id, long businessCardId, String firstName, String lastName) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        User theUser = realm.createObject(User.class, id);
        theUser.setFirstName(firstName);
        theUser.setLastName(lastName);
        realm.commitTransaction();
    }

    public synchronized @Nullable User findUserById(Context context, long id) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        return realm.where(User.class).equalTo("id", id).findFirst();
    }

}
