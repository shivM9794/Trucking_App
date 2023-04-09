package osattransport.com.osatdriver;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import osattransport.com.osatdriver.models.OrderDetails;

/**
 * Created by Harpreet on 2/16/2018.
 */

public class all_orders_adapter extends BaseAdapter {

    Activity activity;

    ArrayList<OrderDetails> data;

    public all_orders_adapter(Activity activity, ArrayList<OrderDetails> data){

        this.activity=activity;
        this.data=data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View views=activity.getLayoutInflater().inflate(R.layout.orders_list_design,null);

        OrderDetails obj=data.get(i);
        TextView order_no=(TextView)views.findViewById(R.id.order_number);
        TextView fromPlace=(TextView)views.findViewById(R.id.fromPlace);
        TextView toPlace=(TextView)views.findViewById(R.id.toPlace);
        TextView date=(TextView)views.findViewById(R.id.date);
        TextView material_type=(TextView)views.findViewById(R.id.material_type);
        TextView weight=(TextView)views.findViewById(R.id.weight);
        TextView height=(TextView)views.findViewById(R.id.height);
        TextView length=(TextView)views.findViewById(R.id.length);

        order_no.setText(obj.getOrderid());
        date.setText(obj.getDate());
        fromPlace.setText(obj.getFromAddress()+", "+obj.getFromCity()+", "+obj.getFromState());
        toPlace.setText(obj.getToAddress()+", "+obj.getToCity()+", "+obj.getToState());
        material_type.setText(obj.getMaterial());
        weight.setText(obj.getWeight());
        height.setText(obj.getHeight()+" ft.");
        length.setText(obj.getLength()+" ft.");

        return views;
    }
}
