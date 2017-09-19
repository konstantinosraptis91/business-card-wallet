package gr.bcw.business_card_wallet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.fragment.SearchForCompanyFragment;
import gr.bcw.business_card_wallet.model.Company;

/**
 * Created by konstantinos on 19/9/2017.
 */

public class CompanyAdapter extends ArrayAdapter<Company> {

    public static final String TAG = ProfessionAdapter.class.getSimpleName();
    private SearchForCompanyFragment f;


    public CompanyAdapter(@NonNull Context context, List<Company> professionList, SearchForCompanyFragment f) {
        super(context, R.layout.row_company, professionList);
        this.f = f;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View companyRowView = inflater.inflate(R.layout.row_company, parent, false);
        final TextView companyRowTextView = (TextView) companyRowView.findViewById(R.id.companyRowTextView);

        final Company p = getItem(position);
        companyRowTextView.setText(p.getName());

        companyRowTextView.setOnClickListener(f);

        return companyRowView;
    }

}
