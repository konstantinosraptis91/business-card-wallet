package gr.bcw.business_card_wallet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import gr.bcw.business_card_wallet.R;

/**
 * Created by konstantinos on 5/5/2017.
 */

public class MyBusinessCardsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_business_cards, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.businessCardsListView);

        // Inflate layout for this fragment
        return rootView;
    }

}
