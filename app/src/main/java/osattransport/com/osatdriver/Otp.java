package osattransport.com.osatdriver;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import osattransport.com.osatdriver.Utils.Globals;
import osattransport.com.osatdriver.Utils.SessionManagement;
import osattransport.com.osatdriver.Utils.UIUtilities;
import osattransport.com.osatdriver.Utils.UserSession;


public class Otp extends Fragment {

    EditText otpText;
    TextView phoneText, resendOTP;
    Button verify;
    String phone;

    UIUtilities uiUtilities;
    SessionManagement sessionManagement;
    public Otp()
    {
        phone="";
    }

    @Override
    public void setArguments(Bundle args)
    {
        super.setArguments(args);
        phone=args.get("phone").toString();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.internet_not_found, container, false);
        TextView text=(TextView)v.findViewById(R.id.Retry);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Otp.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container_1, new Otp()).addToBackStack(null).commit();
            }
        });
        if(Globals.checkInternetConnection(getActivity())) {
            v = inflater.inflate(R.layout.otp, container, false);

            otpText = (EditText) v.findViewById(R.id.otpText);
            phoneText = (TextView) v.findViewById(R.id.phoneText);
            resendOTP = (TextView) v.findViewById(R.id.resend);
            verify = (Button) v.findViewById(R.id.verfiy);
            uiUtilities = new UIUtilities();
            sessionManagement = new SessionManagement(Otp.this.getActivity());

            phoneText.setText(Globals.phoneno);

            resendOTP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Random random = new Random();
                    int rannumber = random.nextInt(100000);

                    uiUtilities.sendSMS(Globals.phoneno, rannumber);

                    Toast.makeText(getActivity(), "OTP has been resend successfully!", Toast.LENGTH_SHORT).show();
                }
            });
            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (otpText.getText().toString() == null || otpText.getText().toString().trim().length() == 0) {
                        uiUtilities.showToast(Otp.this.getActivity(), "Please Enter OTP!!");

                    } else if (Integer.parseInt(otpText.getText().toString()) == Globals.otpcode) {
                        if (UserSession.name == null || UserSession.name.equals("") || UserSession.name.equals("null") || UserSession.name.length() <= 0) {
                            sessionManagement.setUserID(Integer.parseInt(UserSession.userID));

                            Intent intent = new Intent(Otp.this.getActivity(), Profile.class);
                            startActivity(intent);
                        } else {
                            sessionManagement.createLoginSession(Globals.phoneno, Globals.Usertype);
                            sessionManagement.setUserID(Integer.parseInt(UserSession.userID));
                            sessionManagement.checkLogin();
                            Otp.this.getActivity().finish();
                        }
                    }
                }
            });
        }
         return v;
    }
}
