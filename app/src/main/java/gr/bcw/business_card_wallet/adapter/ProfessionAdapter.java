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
import gr.bcw.business_card_wallet.fragment.SearchForProfessionFragment;
import gr.bcw.business_card_wallet.model.Profession;

/**
 * Created by konstantinos on 19/9/2017.
 */

public class ProfessionAdapter extends ArrayAdapter<Profession> {

    public static final String TAG = ProfessionAdapter.class.getSimpleName();
    private SearchForProfessionFragment f;


    public ProfessionAdapter(@NonNull Context context, List<Profession> professionList, SearchForProfessionFragment f) {
        super(context, R.layout.row_profession, professionList);
        this.f = f;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View professionRowView = inflater.inflate(R.layout.row_profession, parent, false);
        final TextView professionRowTextView = (TextView) professionRowView.findViewById(R.id.professionRowTextView);

        final Profession p = getItem(position);
        professionRowTextView.setText(p.getName());

        professionRowTextView.setOnClickListener(f);

        return professionRowView;
    }

}


