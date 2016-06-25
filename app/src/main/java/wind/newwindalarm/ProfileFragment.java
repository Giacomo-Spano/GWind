package wind.newwindalarm;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    OnSignInClickListener mCallback;

    private TextView mUserNameTextView;
    private TextView mEMailTextView;
    private ImageView mUserImageView;

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

        v.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSignInClick();
                //showUserProfile();
            }
        });
        v.findViewById(R.id.sign_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSignOutClick();
                //showUserProfile();
            }
        });
        v.findViewById(R.id.disconnect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onDisconnectClick();
                //showUserProfile();
            }
        });

        TextView tv = (TextView) v.findViewById(R.id.regIdTextView);
        tv.setText(AlarmPreferences.getRegId(this.getActivity()));

        mUserNameTextView = (TextView) v.findViewById(R.id.userTextView);
        tv.setText(AlarmPreferences.getRegId(this.getActivity()));
        mEMailTextView = (TextView) v.findViewById(R.id.EmailTextView);
        tv.setText(AlarmPreferences.getRegId(this.getActivity()));
        mUserImageView = (ImageView) v.findViewById(R.id.userImageView);
        //tv.setText(AlarmPreferences.getRegId(this.getActivity()));

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
            mEMailTextView.setText(mProfile.email);
            Bitmap bitmap = mProfile.userImage;//((BitmapDrawable) profile.userImage.getDrawable()).getBitmap();

            //mUserImageView.setImageBitmap(MainActivity.getCircleBitmap(bitmap));

            //mUserImageView.setImageBitmap(bitmap);
        } else {
            mUserNameTextView.setText("No user");
            mEMailTextView.setText("");
            //mUserImageView.setImageResource(R.drawable.wind);
        }
    }

}