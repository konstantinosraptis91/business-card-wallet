package gr.bcw.business_card_wallet.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import gr.bcw.business_card_wallet.activity.MainActivity;
import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.model.User;
import gr.bcw.business_card_wallet.storage.UserStorageHandler;
import gr.bcw.business_card_wallet.util.TokenUtils;
import gr.bcw.business_card_wallet.util.UserUtils;
import gr.bcw.business_card_wallet.webservice.UserWebService;
import gr.bcw.business_card_wallet.webservice.UserWebServiceImpl;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;
import io.realm.Realm;

/**
 * Created by konstantinos on 17/4/2017.
 */

public class UpdateUserFragment extends Fragment {

    private static final String TAG = UpdateUserFragment.class.getSimpleName();

    /**
     * Keep track of the update task to ensure we can cancel it if requested.
     */
    private UpdateUserByIdTask updateTask = null;

    private Realm realm;

    // UI references
    private EditText emailView;
    private EditText pass1View;
    private EditText pass2View;
    private EditText firstNameView;
    private EditText lastNameView;
    private View progressView;
    private View updateView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        view.setVisibility(View.GONE);

        // Retrieve current user from realm db
        final User dbUser = new UserStorageHandler().findUserById(realm, UserUtils.getID(getActivity()));

        emailView = (EditText) view.findViewById(R.id.email_editText);
        pass1View = (EditText) view.findViewById(R.id.password_editText_1);
        pass2View = (EditText) view.findViewById(R.id.password_editText_2);
        firstNameView = (EditText) view.findViewById(R.id.firstName_editText);
        lastNameView = (EditText) view.findViewById(R.id.lastName_editText);

        // Set EditTexts with currents db user values (for first name and last name for now)
        if (dbUser != null) {
            firstNameView.setText(dbUser.getFirstName());
            lastNameView.setText(dbUser.getLastName());
        } else {
            Log.d(TAG, "DB User is Null");
        }

        // Change action button text
        Button updateBtn = (Button) view.findViewById(R.id.button_action);
        updateBtn.setText(R.string.button_action_save);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptUpdate();
            }
        });

        firstNameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String text = firstNameView.getText().toString();

                if (dbUser != null) {
                    if (hasFocus && text.equals(dbUser.getFirstName())) {
                        firstNameView.setText("");
                    } else {
                        if (text.equals("")) {
                            firstNameView.setText(dbUser.getFirstName());
                        }
                    }
                } else {
                    Log.d(TAG, "DB User is Null");
                }
            }
        });

        lastNameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String text = lastNameView.getText().toString();

                if (dbUser != null) {
                    if (hasFocus && text.equals(dbUser.getLastName())) {
                        lastNameView.setText("");
                    } else {
                        if (text.equals("")) {
                            lastNameView.setText(dbUser.getLastName());
                        }
                    }
                } else {
                    Log.d(TAG, "DB User is Null");
                }
            }
        });

        progressView = view.findViewById(R.id.progress);
        updateView = view.findViewById(R.id.user_form);

        return view;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 2;
    }

    private void attemptUpdate() {
        if (updateTask != null) {
            return;
        }

        // Reset errors
        emailView.setError(null);
        pass1View.setError(null);
        pass2View.setError(null);
        firstNameView.setError(null);
        lastNameView.setError(null);

        // extract values from editTexts and store them at the time of save attempt
        String email = emailView.getText().toString();
        String pass1 = pass1View.getText().toString();
        String pass2 = pass2View.getText().toString();
        String firstName = firstNameView.getText().toString();
        String lastName = lastNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        User theUser = new User();

        // Check for a valid email
        if (!TextUtils.isEmpty(email)) {

            if (isEmailValid(email)) {
                theUser.setEmail(email);
            } else {
                emailView.setError(getString(R.string.error_invalid_email));
                focusView = emailView;
                cancel = true;
            }
        }

        // Check for a valid password
        if (!TextUtils.isEmpty(pass1) && !isPasswordValid(pass1)) {
            pass1View.setError(getString(R.string.error_invalid_password));
            focusView = pass1View;
            cancel = true;
        }

        if (!TextUtils.isEmpty(pass2) && !isPasswordValid(pass2)) {
            pass2View.setError(getString(R.string.error_invalid_password));
            focusView = pass2View;
            cancel = true;
        }

        if (!TextUtils.isEmpty(pass1) && !TextUtils.isEmpty(pass2) &&
                isPasswordValid(pass1) && isPasswordValid(pass2)) {

            if (pass1.equals(pass2)) {
                theUser.setPassword(pass1);
            } else {
                pass2View.setError(getString(R.string.error_passwords_not_match));
                focusView = pass2View;
                cancel = true;
            }
        }

        // Check for a valid first name
        if (!TextUtils.isEmpty(firstName)) {
           theUser.setFirstName(firstName);
        }

        // Check for a valid last name
        if (!TextUtils.isEmpty(lastName)) {
            theUser.setLastName(lastName);
        }

        if (cancel) {
            // There was an error; don't attempt update and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user update attempt.
            showProgress(true);

            long id = UserUtils.getID(getActivity());
            String token = TokenUtils.getToken(getActivity());

            updateTask = new UpdateUserByIdTask(id, theUser, token);
            updateTask.execute(new UserWebServiceImpl());
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            updateView.setVisibility(show ? View.GONE : View.VISIBLE);
            updateView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    updateView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            updateView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class UpdateUserByIdTask extends AsyncTask<UserWebService, Void, Boolean> {

        private long id;
        private User user;
        private String token;
        private String message = "";

        public UpdateUserByIdTask(long id, User user, String token) {
            this.id = id;
            this.user = user;
            this.token = token;
        }

        @Override
        protected Boolean doInBackground(UserWebService... params) {
            UserWebService service = params[0];
            Boolean result = false;

            try {
                result = service.updateUserById(id, user, token);
            } catch (WebServiceException ex) {
                message = ex.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            updateTask = null;
            showProgress(false);

            if (result) {
                // user updated successfully
                Log.d(TAG, "update performed successfully");

                // save new user in realm db
                new UserStorageHandler().updateUser(realm, id, user.getFirstName(), user.getLastName());

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            } else {
                // user didn't updated successfully
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            updateTask = null;
            showProgress(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
