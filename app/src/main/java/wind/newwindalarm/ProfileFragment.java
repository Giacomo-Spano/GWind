package wind.newwindalarm;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

public class ProfileFragment extends Fragment {

    OnSignInClickListener mCallback;

    private TextView mUserNameTextView;
    private TextView mEMailTextView;
    private ImageView mUserImageView;
    private TextView mRegIdTextView;
    private SignInButton mSignonButton;
    private Button mSignoutButton;
    private Button mDisconnectButton;

    UserProfile mProfile;
    protected boolean initialized = false;

    // Container Activity must implement this interface
    public interface OnSignInClickListener {
        public void onSignInClick();
        public void onSignOutClick();
        public void onDisconnectClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnSignInClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnAlarmListener");
        }
    }

    @SuppressWarnings("deprecation")
    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < 23) {
            try {
                mCallback = (OnSignInClickListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnAlarmListener");
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v;
        v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Updating the action bar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profilo");

        mSignonButton = (com.google.android.gms.common.SignInButton) v.findViewById(R.id.sign_in_button);
        mSignonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSignInClick();
            }
        });

        mSignoutButton = (Button) v.findViewById(R.id.sign_out_button);
        mSignoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSignOutClick();
            }
        });

        mDisconnectButton = (Button) v.findViewById(R.id.disconnect_button);
        mDisconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onDisconnectClick();
            }
        });

        mRegIdTextView = (TextView) v.findViewById(R.id.regIdTextView);
        mRegIdTextView.setText(AlarmPreferences.getRegId(this.getActivity()));

        mUserNameTextView = (TextView) v.findViewById(R.id.userTextView);
        mEMailTextView = (TextView) v.findViewById(R.id.EmailTextView);
        mUserImageView = (ImageView) v.findViewById(R.id.userImageView);

        initialized = true;

        showUserProfile();

        return v;
    }
    public void setProfile(UserProfile profile) {
        this.mProfile = profile;
        showUserProfile();
    }

    private void showUserProfile() {

        if(!initialized) return;

        if (mProfile != null) {
            mUserNameTextView.setText(mProfile.userName);
            mUserNameTextView.setVisibility(View.VISIBLE);
            mEMailTextView.setText(mProfile.email);
            mEMailTextView.setVisibility(View.VISIBLE);
            mUserImageView.setVisibility(View.VISIBLE);
            mUserImageView.setImageBitmap(mProfile.userImage);
            mRegIdTextView.setVisibility(View.VISIBLE);
            mSignonButton.setVisibility(View.GONE);
            mSignoutButton.setVisibility(View.VISIBLE);
            mDisconnectButton.setVisibility(View.VISIBLE);
        } else {
            mUserNameTextView.setText("No user");
            mUserNameTextView.setVisibility(View.GONE);
            mEMailTextView.setText("");
            mEMailTextView.setVisibility(View.GONE);
            mUserImageView.setImageResource(R.drawable.wind);
            mUserImageView.setVisibility(View.GONE);
            mRegIdTextView.setVisibility(View.GONE);
            mSignonButton.setVisibility(View.VISIBLE);
            mSignoutButton.setVisibility(View.GONE);
            mDisconnectButton.setVisibility(View.GONE);
        }
    }

}