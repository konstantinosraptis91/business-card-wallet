package gr.bcw.business_card_wallet.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.adapter.ProfessionAdapter;
import gr.bcw.business_card_wallet.model.Profession;
import gr.bcw.business_card_wallet.webservice.ProfessionWebService;
import gr.bcw.business_card_wallet.webservice.ProfessionWebServiceImpl;
import gr.bcw.business_card_wallet.webservice.WebService;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;
import io.realm.Realm;

// import android.app.Fragment;

/**
 * Created by konstantinos on 11/9/2017.
 */

public class SearchForProfessionFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = SearchForProfessionFragment.class.getSimpleName();

    private FindProfessionByNameTask findProfessionByNameTask = null;
    private Realm realm;
    private ActionBar actionBar;
    private View progressView;
    private ListView professionListView;
    private ProfessionAdapter professionAdapter;
    private EditText searchEditText;

    // UI references
//    private EditText emailView;
//    private EditText phoneNumberView;
//    private EditText addressView;
//    private EditText websiteView;
//    private View progressView;
//    private View createView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.hide();

        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        actionBar.hide();
    }

    @Override
    public void onClick(View v) {
        // get profession name here
        TextView profNameView = (TextView) v;
        String profName = profNameView.getText().toString();

        Bundle b = new Bundle();
        b.putString("prof-name", profName);

        getActivity().getIntent().putExtras(b);
        getFragmentManager().popBackStackImmediate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_search, container, false);
        professionAdapter = new ProfessionAdapter(getActivity(), new ArrayList<Profession>(), SearchForProfessionFragment.this);
        professionListView = (ListView) fragmentView.findViewById(R.id.professionListView);
        professionListView.setAdapter(professionAdapter);

        progressView = fragmentView.findViewById(R.id.progress);

        ImageButton clearBtn = (ImageButton) fragmentView.findViewById(R.id.clear_button);
        searchEditText = (EditText) fragmentView.findViewById(R.id.search_editText);

        // change search editText hint
        searchEditText.setHint(R.string.search_profession_hint);

        // when search pressed
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    attemptSearchForProfessions();

                    // hide soft keyboard
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    return true;
                }
                return false;
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
            }
        });

        // Retrieve current user from realm db
//        final User dbUser = new UserStorageHandler().findUserById(realm, UserUtils.getID(getActivity()));
//
//        emailView = (EditText) view.findViewById(R.id.email_editText);
//        phoneNumberView = (EditText) view.findViewById(R.id.phoneNumber_editText);
//        addressView = (EditText) view.findViewById(R.id.address1_editText);
//        websiteView = (EditText) view.findViewById(R.id.website_editText);
//
//        ImageButton searchForProfessionBtn = (ImageButton) view.findViewById(R.id.search_profession_button);
//        ImageButton searchForCompanyBtn = (ImageButton) view.findViewById(R.id.search_company_button);
//
//        searchForProfessionBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        searchForCompanyBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        // Change action button text
//        Button createBtn = (Button) view.findViewById(R.id.button_action);
//        createBtn.setText(R.string.button_action_create);
//
//        createBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                attemptCreateBC();
//            }
//        });
//
//        progressView = view.findViewById(R.id.progress);
//        createView = view.findViewById(R.id.business_card_form);

        return fragmentView;
    }

    private void attemptSearchForProfessions() {
        if (findProfessionByNameTask != null) {
            return;
        }

        // Reset errors


        // extract values from editTexts and store them at the time of creation attempt
        String profName = searchEditText.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // validation check here


        if (cancel) {
            // There was an error; don't attempt update and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user update attempt.
            showProgress(true);

            findProfessionByNameTask = new FindProfessionByNameTask(profName);
            findProfessionByNameTask.execute(new ProfessionWebServiceImpl());
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

            professionListView.setVisibility(show ? View.GONE : View.VISIBLE);
            professionListView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    professionListView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            professionListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class FindProfessionByNameTask extends AsyncTask<WebService, Void, Boolean> {

        private String profName;
        private String message = "";
        private List<Profession> professionList;

        public FindProfessionByNameTask(String profName) {
            this.profName = profName;
        }

        @Override
        protected Boolean doInBackground(WebService... params) {
            ProfessionWebService service = (ProfessionWebService) params[0];
            boolean result = false;

            try {
                professionList = service.findByName(profName);
                result = true;
            } catch (WebServiceException ex) {
                message = ex.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            findProfessionByNameTask = null;
            showProgress(false);

            if (result) {
                // clear adapter
                professionAdapter.clear();

                for (Profession p : professionList) {
                    professionAdapter.add(p);
                }
                professionAdapter.notifyDataSetChanged();
            } else {
                // something went wrong
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onCancelled() {
            findProfessionByNameTask = null;
            showProgress(false);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}

