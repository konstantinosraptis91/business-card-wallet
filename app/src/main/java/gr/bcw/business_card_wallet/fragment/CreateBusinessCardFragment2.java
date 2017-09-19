package gr.bcw.business_card_wallet.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.activity.MainActivity;
import gr.bcw.business_card_wallet.model.BusinessCard;
import gr.bcw.business_card_wallet.model.User;
import gr.bcw.business_card_wallet.model.sender.BusinessCardRequest;
import gr.bcw.business_card_wallet.storage.UserStorageHandler;
import gr.bcw.business_card_wallet.util.TokenUtils;
import gr.bcw.business_card_wallet.util.UserUtils;
import gr.bcw.business_card_wallet.webservice.BusinessCardWebService;
import gr.bcw.business_card_wallet.webservice.BusinessCardWebServiceImpl;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;
import io.realm.Realm;

// import android.app.Fragment;

/**
 * Created by konstantinos on 11/9/2017.
 */

public class CreateBusinessCardFragment2 extends Fragment {

    public static final String TAG = CreateBusinessCardFragment2.class.getSimpleName();

    /**
     * Keep track of the create bc task to ensure we can cancel it if requested.
     */
    private CreateBusinessCardTask createTask = null;

    private Realm realm;

    // UI references
    private ActionBar actionBar;
    private EditText emailView;
    private EditText phoneNumberView;
    private EditText addressView;
    private EditText websiteView;
    private EditText professionEditText;
    private EditText companyEditText;
    private View progressView;
    private View createView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_custom);

        TextView activityTitle = (TextView) getActivity().findViewById(R.id.custom_action_bar);
        activityTitle.setText(R.string.action_bar_create_business_card_title);

        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!actionBar.isShowing()) {
            actionBar.show();
        }

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_custom);
        TextView activityTitle = (TextView) getActivity().findViewById(R.id.custom_action_bar);
        activityTitle.setText(R.string.action_bar_create_business_card_title);

        // set prof & comp name
        Bundle b = getActivity().getIntent().getExtras();

        if (b != null && b.containsKey("prof-name")) {
            professionEditText.setText(b.getString("prof-name"));
            professionEditText.requestFocus();
            getActivity().getIntent().removeExtra("prof-name");
        }

        if (b != null && b.containsKey("comp-name")) {
            companyEditText.setText(b.getString("comp-name"));
            companyEditText.requestFocus();
            getActivity().getIntent().removeExtra("comp-name");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_create_business_card, container, false);

        // Retrieve current user from realm db
        final User dbUser = new UserStorageHandler().findUserById(realm, UserUtils.getID(getActivity()));

        emailView = (EditText) fragmentView.findViewById(R.id.email_editText);
        phoneNumberView = (EditText) fragmentView.findViewById(R.id.phoneNumber_editText);
        addressView = (EditText) fragmentView.findViewById(R.id.address1_editText);
        websiteView = (EditText) fragmentView.findViewById(R.id.website_editText);
        professionEditText = (EditText) fragmentView.findViewById(R.id.profession_editText);
        companyEditText = (EditText) fragmentView.findViewById(R.id.company_editText);

        ImageButton searchForProfessionBtn = (ImageButton) fragmentView.findViewById(R.id.search_profession_button);
        ImageButton searchForCompanyBtn = (ImageButton) fragmentView.findViewById(R.id.search_company_button);

        searchForProfessionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchForProfessionFragment searchProfFrag = new SearchForProfessionFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.fragment_container, searchProfFrag);
                // allow user to go back
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        searchForCompanyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchForCompanyFragment searchCompFrag = new SearchForCompanyFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.fragment_container, searchCompFrag);
                // allow user to go back
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        // Change action button text
        Button createBtn = (Button) fragmentView.findViewById(R.id.button_action);
        createBtn.setText(R.string.button_action_create);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptCreateBC();
            }
        });

        progressView = fragmentView.findViewById(R.id.progress);
        createView = fragmentView.findViewById(R.id.business_card_form);

        return fragmentView;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private void attemptCreateBC() {
        if (createTask != null) {
            return;
        }

        // Reset errors
        emailView.setError(null);
        phoneNumberView.setError(null);
        addressView.setError(null);
        websiteView.setError(null);
        professionEditText.setError(null);
        companyEditText.setError(null);

        // extract values from editTexts and store them at the time of creation attempt
        String email = emailView.getText().toString();
        String phoneNumber = phoneNumberView.getText().toString();
        String address = addressView.getText().toString();
        String website = websiteView.getText().toString();
        String profession = professionEditText.getText().toString();
        String company = companyEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        BusinessCard card = new BusinessCard();

        // Check for a valid email
        if (!TextUtils.isEmpty(email)) {

            if (isEmailValid(email)) {
                card.setEmail1(email);
            } else {
                emailView.setError(getString(R.string.error_invalid_email));
                focusView = emailView;
                cancel = true;
            }
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

            // card conf goes here
            card.setUserId(id);
            card.setPhoneNumber1(phoneNumber);
            card.setAddress1(address);
            card.setUniversal(true);
            card.setWebsite(website);

            BusinessCardRequest cardRequest = new BusinessCardRequest();
            cardRequest.setBusinessCard(card);
            cardRequest.setCompanyName(company);
            cardRequest.setProfessionName(profession);

            createTask = new CreateBusinessCardTask(cardRequest, token);
            createTask.execute(new BusinessCardWebServiceImpl());
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

            createView.setVisibility(show ? View.GONE : View.VISIBLE);
            createView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    createView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            createView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class CreateBusinessCardTask extends AsyncTask<BusinessCardWebService, Void, Boolean> {

        private BusinessCardRequest cardRequest;
        private String token;
        private String message = "";

        public CreateBusinessCardTask(BusinessCardRequest cardRequest, String token) {
            this.cardRequest = cardRequest;
            this.token = token;
        }

        @Override
        protected Boolean doInBackground(BusinessCardWebService... params) {
            BusinessCardWebService service = params[0];
            Boolean result = false;

            try {
                cardRequest = service.createBusinessCardV2(cardRequest,  token);
                result = true;
            } catch (WebServiceException ex) {
                message = ex.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            createTask = null;
            showProgress(false);

            if (result) {
                // bc created successfully
                Log.d(TAG, "Card creation performed successfully");
                Toast.makeText(getActivity(), "success", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            } else {
                // bc didn't created successfully
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            createTask = null;
            showProgress(false);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}

