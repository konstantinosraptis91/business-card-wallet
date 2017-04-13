package gr.bcw.business_card_wallet;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by konstantinos on 26/3/2017.
 */

public class AddedBusinessCardsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_added_business_cards, container, false);
        ListView listView = (ListView) view.findViewById(R.id.addedBusinessCardsListView);



        // Inflate layout for this fragment
        return view;
    }
}
