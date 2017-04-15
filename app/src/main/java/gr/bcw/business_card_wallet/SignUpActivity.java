package gr.bcw.business_card_wallet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import gr.bcw.business_card_wallet.model.User;
import gr.bcw.business_card_wallet.storage.UserStorageHandler;
import gr.bcw.business_card_wallet.util.TokenUtils;
import gr.bcw.business_card_wallet.util.UserUtils;
import gr.bcw.business_card_wallet.webservice.UserWebService;
import gr.bcw.business_card_wallet.webservice.UserWebServiceImpl;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;

/**
 * Created by konstantinos on 6/3/2017.
 */

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private SignUpTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mSignUpFormView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_custom);

        TextView activityTitle = (TextView) findViewById(R.id.custom_action_bar);
        activityTitle.setText(R.string.action_bar_sign_up_title);

        setContentView(R.layout.activity_signup);
        // Set up the sign up form.
        mEmailView = (EditText) findViewById(R.id.email_signUp);
        mFirstNameView = (EditText) findViewById(R.id.firstName_signUp);
        mLastNameView = (EditText) findViewById(R.id.lastName_signUp);

        mPasswordView = (EditText) findViewById(R.id.password_signUp);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptSignUp();
                    return true;
                }
                return false;
            }
        });

        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });

        mSignUpFormView = findViewById(R.id.signUp_form);
        mProgressView = findViewById(R.id.signUp_progress);
    }

    /**
     * Attempts to sign up with the credentials specified by the sign up form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignUp() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mFirstNameView.setError(null);
        mLastNameView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
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

        // Check for a valid First Name
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        // Check for a valid Last Name
        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user sign up attempt.
            showProgress(true);
            mAuthTask = new SignUpActivity.SignUpTask(email, password, firstName, lastName);
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

            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class SignUpTask extends AsyncTask<UserWebService, Void, User> {

        private final String email;
        private final String password;
        private final String firstName;
        private final String lastName;
        private String message = "";

        SignUpTask(String email, String password, String firstName, String lastName) {
            this.email = email;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        protected User doInBackground(UserWebService... params) {

            UserWebService service = params[0];
            User theUser = null;

            try {
                User user = new User();
                user.setEmail(email);
                user.setPassword(password);
                user.setFirstName(firstName);
                user.setLastName(lastName);

                theUser =  service.createUser(user);
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

                // save token to prefs
                TokenUtils.saveToken(SignUpActivity.this, theUser.getToken());
                // save user id to prefs
                UserUtils.saveID(SignUpActivity.this, theUser.getId());

                // save user using realm db
                new UserStorageHandler().saveUser(SignUpActivity.this, theUser.getId(), theUser.getFirstName(), theUser.getLastName());

                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.putExtra("id", theUser.getId());
                startActivity(intent);
                setResult(LoginActivity.RESULT_OK, null);
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

}
