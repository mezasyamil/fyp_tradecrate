package util;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.izzat.syamil.tradecrate.Home.TradePlace;
import com.izzat.syamil.tradecrate.R;
import com.izzat.syamil.tradecrate.Trade_Inbox.TradeRequestView;

import java.time.LocalDate;
import java.util.Calendar;

public class CalendarDialog extends DialogFragment implements View.OnClickListener, CalendarView.OnDateChangeListener{

    private CalendarView calendarView;
    private AppCompatButton yeap, nope;
    private int day = 0, month = 0, year = 0;
    private LocalDate today, picked;
    private String fromWhichActivity = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calendar_dialog, container, false);

        try{
            fromWhichActivity = getArguments().getString("fromActivity");

        }
        catch ( NullPointerException e){
            e.printStackTrace();

        }

        calendarView = v.findViewById(R.id.calendarView);
        yeap = v.findViewById(R.id.accept);
        nope = v.findViewById(R.id.cancel);

        Calendar cal =  Calendar.getInstance();
        this.day = cal.get(Calendar.DAY_OF_WEEK);
        this.month = cal.get(Calendar.MONTH) + 1;
        this.year = cal.get(Calendar.YEAR);

        today = LocalDate.now();

        calendarView.setOnDateChangeListener(this);
        yeap.setOnClickListener(this);
        nope.setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.accept:
                try{
                    if(picked.compareTo(today) < 0){
                        Snackbar.make(getView(), "Date picked has passed today.", Snackbar.LENGTH_SHORT).show();

                    }   else if(picked.compareTo(today) > 0 && fromWhichActivity.equals("TradePlace"))    {
                        ((TradePlace)getActivity()).setPickedDate(day, month, year);
                        dismiss();

                    }   else    {
                        ((TradeRequestView)getActivity()).setPickedDate(day, month, year);
                        dismiss();

                    }

                }   catch (NullPointerException e){
                    e.printStackTrace();

                }

                break;

            case R.id.cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int y, int m, int dayOfMonth) {
        this.year = y;
        this.month = m + 1;
        this.day = dayOfMonth;
        picked = LocalDate.of(year, month, day);

    }
}
