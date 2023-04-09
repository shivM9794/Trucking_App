package osattransport.com.osatdriver;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class Main_fragment_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment_activity);


        SignIn sgn=new SignIn();
        getSupportFragmentManager().beginTransaction().add(R.id.main_container_1,sgn).disallowAddToBackStack().commit();
    }

}
