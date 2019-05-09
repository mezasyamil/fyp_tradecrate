package util;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import com.izzat.syamil.tradecrate.Home.TradePlace;
import com.izzat.syamil.tradecrate.R;
import com.izzat.syamil.tradecrate.Trade_Inbox.TradeRequestView;

public class LocationDialog extends DialogFragment {

    private EditText vInput;
    private AppCompatButton insert, cancel;
    private String fromWhichActivity = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.location_dialog, container, false);

        try{
            fromWhichActivity = getArguments().getString("fromActivity");

        }
        catch ( NullPointerException e){
            e.printStackTrace();

        }

        vInput = v.findViewById(R.id.venueInput);
        insert = v.findViewById(R.id.insertVenue);

        if(fromWhichActivity.equals("TradePlace")){

            vInput.setText(
                    ((TradePlace)getActivity()).getLocation()
            );

            ((TradePlace)getActivity()).setLocationInput(vInput.getEditableText().toString());

            insert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TradePlace)getActivity()).setLocationInput(vInput.getEditableText().toString());
                    dismiss();
                }
            });

        }   else   {

            vInput.setText(
                    ((TradeRequestView)getActivity()).getLocation()
            );

            ((TradeRequestView)getActivity()).setLocationInput(vInput.getEditableText().toString());

            insert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((TradeRequestView)getActivity()).setLocationInput(vInput.getEditableText().toString());

                    dismiss();
                }
            });


        }


        cancel = v.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss();
                Log.e("test", fromWhichActivity);
            }
        });

        return v;
    }
}
