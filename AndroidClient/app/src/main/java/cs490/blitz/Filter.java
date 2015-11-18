package cs490.blitz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

/**
 * Created by arthurchan35 on 11/17/2015.
 */
public class Filter extends Activity implements View.OnClickListener{
    Spinner ReqOrOffer;
    String ReqOrOfferStr;
    Spinner categorySpinner;
    String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);
        ReqOrOfferStr = null;
        selectedCategory = null;
        ReqOrOffer = (Spinner) findViewById(R.id.spReqOrOfferInFilter);
        categorySpinner = (Spinner) findViewById(R.id.spCategoryInFilter);
    }

    @Override
    public void onClick(View v) {

    }
}
