package gr.bcw.business_card_wallet.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    public static final int REQUEST_EXIT = 400;
    public static final int RESULT_OK = 200;

    private Realm realm;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_custom);

        TextView activityTitle = (TextView) findViewById(R.id.custom_action_bar);
        activityTitle.setText(R.string.action_bar_login_title);

        setContentView(R.layout.activity_login);

        realm = Realm.getDefaultInstance();

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email_login);

        mPasswordView = (EditText) findViewById(R.id.password_login);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        Button signUpButton = (Button) findViewById(R.id.go_to_sign_up_button);
        signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check if password empty
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute(new UserWebServiceImpl());
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 2;
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void signUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivityForResult(intent, LoginActivity.REQUEST_EXIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginActivity.REQUEST_EXIT) {
            if (resultCode == LoginActivity.RESULT_OK) {
                finish();
            }
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<UserWebService, Void, User> {

        private final String email;
        private final String password;
        private String message = "";

        UserLoginTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected User doInBackground(UserWebService... params) {

            UserWebService service = params[0];
            User theUser = null;

            try {
                theUser =  service.authenticate(email, password);
            } catch (WebServiceException ex) {
                message = ex.getMessage();
            }

            return theUser;
        }

        @Override
        protected void onPostExecute(final User theUser) {
            mAuthTask = null;
            showProgress(false);

            if (theUser != null) {

                // override old token with new one
                TokenUtils.saveToken(LoginActivity.this, theUser.getToken());
                // override id (to avoid any server side changes conflicts)
                UserUtils.saveID(LoginActivity.this, theUser.getId());

                // Check if user info are stored in local realm db. if not store them
                User dbUser = realm.where(User.class).equalTo("id", theUser.getId()).findFirst();

                // if database user null, make a call to server in order to get user info
                if (dbUser == null) {
                    FindUserByIdTask findUserByIdTask = new FindUserByIdTask(theUser.getId(), theUser.getToken());
                    findUserByIdTask.execute(new UserWebServiceImpl());
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    // dbUser id will be the same with user id here
                    intent.putExtra("id", dbUser.getId());
                    startActivity(intent);
                    finish();
                }

            } else {
                Log.d(TAG, message);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }

    private class FindUserByIdTask extends AsyncTask<UserWebService, Void, User> {

        private String token;
        private long id;
        private String message = "";

        public FindUserByIdTask(long id, String token) {
            this.id = id;
            this.token = token;
        }

        @Override
        protected User doInBackground(UserWebService... params) {
            UserWebService service = params[0];
            User theUser = null;

            try {
                theUser = service.findUserById(id, token);
            } catch (WebServiceException ex) {
                message = ex.getMessage();
            }

            return theUser;
        }

        @Override
        protected void onPostExecute(User user) {

            if (user != null) {
                // save user using realm db
                new UserStorageHandler().saveUser(realm, user.getId(), user.getFirstName(), user.getLastName());

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("id", user.getId());
                startActivity(intent);
                finish();
            } else {
                Log.d(TAG, message);
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mEmailView.setText("");
        mPasswordView.setText("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}

