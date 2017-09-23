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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import gr.bcw.business_card_wallet.adapter.CompanyAdapter;
import gr.bcw.business_card_wallet.model.Company;
import gr.bcw.business_card_wallet.webservice.CompanyWebService;
import gr.bcw.business_card_wallet.webservice.CompanyWebServiceImpl;
import gr.bcw.business_card_wallet.webservice.WebService;
import gr.bcw.business_card_wallet.webservice.exception.WebServiceException;
import io.realm.Realm;

/**
 * Created by konstantinos on 19/9/2017.
 */

public class SearchForCompanyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = SearchForCompanyFragment.class.getSimpleName();

    private FindCompanyByNameTask findCompanyByNameTask = null;
    private Realm realm;
    private ActionBar actionBar;
    private View progressView;
    private ListView companyListView;
    private CompanyAdapter companyAdapter;
    private EditText searchEditText;

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
        // get company name here
        TextView compNameView = (TextView) v;
        String compName = compNameView.getText().toString();

        Bundle b = new Bundle();
        b.putString("comp-name", compName);

        getActivity().getIntent().putExtras(b);

        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(SearchForCompanyFragment.this);
        trans.commit();
        manager.popBackStack();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_search, container, false);
        companyAdapter = new CompanyAdapter(getActivity(), new ArrayList<Company>(), SearchForCompanyFragment.this);
        companyListView = (ListView) fragmentView.findViewById(R.id.entityListView);
        companyListView.setAdapter(companyAdapter);

        progressView = fragmentView.findViewById(R.id.progress);

        ImageButton clearBtn = (ImageButton) fragmentView.findViewById(R.id.clear_button);
        ImageButton backBtn = (ImageButton) fragmentView.findViewById(R.id.goBack_button);

        searchEditText = (EditText) fragmentView.findViewById(R.id.search_editText);

        // change search editText hint
        searchEditText.setHint(R.string.search_company_hint);

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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goBack();
            }
        });

        return fragmentView;
    }

    private void goBack() {
        getActivity().onBackPressed();
    }

    private void attemptSearchForProfessions() {
        if (findCompanyByNameTask != null) {
            return;
        }

        // Reset errors
        searchEditText.setError(null);

        // extract values from editTexts and store them at the time of creation attempt
        String compName = searchEditText.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // validation check here
        if (TextUtils.isEmpty(compName)) {
            searchEditText.setError(getString(R.string.error_field_required));
            focusView = searchEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt update and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user update attempt.
            showProgress(true);

            findCompanyByNameTask = new FindCompanyByNameTask(compName);
            findCompanyByNameTask.execute(new CompanyWebServiceImpl());
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

            companyListView.setVisibility(show ? View.GONE : View.VISIBLE);
            companyListView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    companyListView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            companyListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class FindCompanyByNameTask extends AsyncTask<WebService, Void, Boolean> {

        private String compName;
        private String message = "";
        private List<Company> companyList;

        public FindCompanyByNameTask(String compName) {
            this.compName = compName;
        }

        @Override
        protected Boolean doInBackground(WebService... params) {
            CompanyWebService service = (CompanyWebService) params[0];
            boolean result = false;

            try {
                companyList = service.searchByName(compName);
                result = true;
            } catch (WebServiceException ex) {
                message = ex.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            findCompanyByNameTask = null;
            showProgress(false);

            if (result) {
                // clear adapter
                companyAdapter.clear();

                for (Company c : companyList) {
                    companyAdapter.add(c);
                }
                companyAdapter.notifyDataSetChanged();
            } else {
                // something went wrong
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onCancelled() {
            findCompanyByNameTask = null;
            showProgress(false);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
