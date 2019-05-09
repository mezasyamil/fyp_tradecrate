package com.izzat.syamil.tradecrate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.izzat.syamil.tradecrate.Home.TradePlace;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import model.InventoryItem;
import model.Tradeable;
import util.FireDatabase;

public class InventoryView extends AppCompatActivity implements View.OnClickListener, Switch.OnCheckedChangeListener{

    private FrameLayout frameLayout;
    private ImageButton back_button;
    private BottomSheetBehavior botSheetBehav;
    private Tradeable selectedItem;
    private InventoryItem mineSelected;
    private TextView i_name,trader_name, i_status, i_location, i_description;
    private ImageView i_picture, p_picture, expandIPicture;
    private CircleImageView expandPPicture;
    private Chip ratingReviewChip;
    private Switch statusSwitch;
    private ViewStub botViewStub;
    private AppCompatButton addToBasket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_template);
        selectedItem = (Tradeable) getIntent().getExtras().getParcelable("view selected item");

        //BOTTOM SHEET
        frameLayout = findViewById(R.id.bottomSht);
        if(!selectedItem.isSelf()){
            frameLayout.addView(getLayoutInflater().inflate(R.layout.inventory_info_other, frameLayout, false));
        }   else    {
            frameLayout.addView(getLayoutInflater().inflate(R.layout.inventory_info_mine, frameLayout, false));
        }

        try{

            back_button = findViewById(R.id.back_button);
            back_button.setOnClickListener(this);

            i_name = findViewById(R.id.traderName);
            i_name.setText(selectedItem.getTraderName());

            trader_name = findViewById(R.id.iName);
            trader_name.setText(selectedItem.getTradeable().getItemName());

            i_status = findViewById(R.id.tradeStatus);
            if(selectedItem.getTradeable().isAvailable()){
                i_status.setText("Available");
                i_status.setTextColor(getColor(R.color.yeap));

            }   else    {
                i_status.setText("Out of Stock");
                i_status.setTextColor(getColor(R.color.nope));
            }

            i_location = findViewById(R.id.location_disp);
            i_location.setText(selectedItem.getTradeable().getItemLocation());

            i_description = findViewById(R.id.description_disp);
            i_description.setText(selectedItem.getTradeable().getItemDescription());

            ratingReviewChip = findViewById(R.id.RnRchip);
            ratingReviewChip.setOnClickListener(this);

            //objects that aren't in both layout
            if(selectedItem.isSelf()) {

                ImageButton uploadImage = findViewById(R.id.uploadPhoto);
                uploadImage.setVisibility(View.VISIBLE);

                statusSwitch = findViewById(R.id.switch1);
                statusSwitch.setOnCheckedChangeListener(this);
                if(selectedItem.getTradeable().isAvailable()){
                    statusSwitch.setChecked(true);
                    setStatusSwitch(true);

                }   else    {
                    statusSwitch.setChecked(false);
                    setStatusSwitch(false);
                }

            }   else    {
                addToBasket = findViewById(R.id.addToBasket);
                addToBasket.setOnClickListener(this);
            }

        }   catch (NullPointerException e){
            e.printStackTrace();

        }

        //profile picture
        p_picture = findViewById(R.id.p_picture);
        Picasso.with(this)
                .load(selectedItem.getTraderPicture())
                .fit()
                .into(p_picture);

        expandPPicture = findViewById(R.id.profile_picExpand);
        Picasso.with(this)
                .load(selectedItem.getTraderPicture())
                .fit()
                .into(expandPPicture);

        //item picture
        i_picture = findViewById(R.id.item_image);
        Picasso.with(this)
                .load(selectedItem.getTradeable().getItemImageURL())
                .placeholder(getDrawable(R.drawable.ic_insert_photo_gray_200dp))
                .fit()
                .centerCrop()
                .into(i_picture);

        expandIPicture = findViewById(R.id.iImageExpand);
        Picasso.with(this)
                .load(selectedItem.getTradeable().getItemImageURL())
                .centerCrop()
                .fit()
                .into(expandIPicture);

        setBotSheetBehav(
                BottomSheetBehavior.from(findViewById(R.id.bottomSht))
        );

        if(botSheetBehav.getState() == BottomSheetBehavior.STATE_EXPANDED){
            expandPPicture.setVisibility(View.VISIBLE);
            expandIPicture.setVisibility(View.VISIBLE);

        }   else    {
            expandPPicture.setVisibility(View.INVISIBLE);
            expandIPicture.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_button:
                finish();
                break;

            case R.id.RnRchip:
                botSheetBehav.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;

            case R.id.addToBasket:
                Intent i = new Intent(this, TradePlace.class);
                i.putExtra("interested item", selectedItem);
                startActivity(i);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        setStatusSwitch(isChecked);
        new FireDatabase().setStatus(isChecked, selectedItem.getTradeable().getKey());
    }

        private void setStatusSwitch(boolean isAvailable){

            if(isAvailable){
                i_status.setText("Available");
                i_status.setTextColor(getColor(R.color.yeap));

            }   else {
                i_status.setText("Out of Stock");
                i_status.setTextColor(getColor(R.color.nope));

            }
        }

    public void setBotSheetBehav(final BottomSheetBehavior botSheetBehav) {
        this.botSheetBehav = botSheetBehav;

        //botSheetBehav.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        botSheetBehav.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

              /*  switch (newState){
                    case BottomSheetBehavior.STATE_EXPANDED:
                        expandIPicture.setVisibility();
                }*/

                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    expandPPicture.setVisibility(View.VISIBLE);
                    expandIPicture.setVisibility(View.VISIBLE);
                    i_name.setVisibility(View.INVISIBLE);

                }   else    {
                    expandPPicture.setVisibility(View.INVISIBLE);
                    expandIPicture.setVisibility(View.INVISIBLE);
                    i_name.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }
}
