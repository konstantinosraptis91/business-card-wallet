package gr.bcw.business_card_wallet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import gr.bcw.business_card_wallet.LoginActivity;
import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.util.TokenUtils;
import gr.bcw.business_card_wallet.util.UserUtils;

/**
 * Created by konstantinos on 17/3/2017.
 */

public class SettingsFragmentOptions extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_options, container, false);
        Button logout = (Button) view.findViewById(R.id.logout_button);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // delete user id
                UserUtils.removeID(getActivity());
                // delete token
                TokenUtils.removeToken(getActivity());

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }

}
