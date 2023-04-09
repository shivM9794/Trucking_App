package osattransport.com.osatdriver;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class SliderDetailsActivity extends AppCompatActivity {
    ImageView image1;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_details);

        getSupportActionBar().setTitle("View Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        String image=getIntent().getExtras().get("Image").toString();

        image1=(ImageView) findViewById(R.id.image1);
        webView=(WebView) findViewById(R.id.webView);
            if (image.equalsIgnoreCase("1"))
                Picasso.with(this).load("http://osattransport.com/back-bone/images/slider/driver/1.jpg").error(R.drawable.delhicity).into(image1);
            else if (image.equalsIgnoreCase("2"))
                Picasso.with(this).load("http://osattransport.com/back-bone/images/slider/driver/2.jpg").error(R.drawable.delhicity).into(image1);
            else if (image.equalsIgnoreCase("3"))
                Picasso.with(this).load("http://osattransport.com/back-bone/images/slider/driver/3.jpg").error(R.drawable.delhicity).into(image1);

        webView.loadUrl("http://osattransport.com/bannerdetails.php?bannerid="+image+"&type=DRIVER");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return true;
    }
}
