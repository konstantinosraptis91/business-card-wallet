package gr.bcw.business_card_wallet.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import gr.bcw.business_card_wallet.activity.LoginActivity;
import gr.bcw.business_card_wallet.R;
import gr.bcw.business_card_wallet.util.TokenUtils;
import gr.bcw.business_card_wallet.util.UserUtils;

/**
 * Created by konstantinos on 17/3/2017.
 */

public class SettingsFragmentOptions extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_options, container, false);

        Button updateUser = (Button) view.findViewById(R.id.button_update_user);
        Button logout = (Button) view.findViewById(R.id.logout_button);

        updateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView title = (TextView) getActivity().findViewById(R.id.custom_action_bar);
                title.setText(R.string.action_bar_update_user_title);
                getActivity().findViewById(R.id.fragment_update_user).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.preferences_fragment_1).setVisibility(View.GONE);
                getActivity().findViewById(R.id.preferences_fragment_2).setVisibility(View.GONE);
            }
        });

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
