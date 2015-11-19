package cs490.blitz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Filter extends Activity implements View.OnClickListener{
    Spinner ReqOrOffer;
    String ReqOrOfferStr;
    ArrayAdapter<CharSequence> adapterOfRoO;

    Spinner categorySpinner;
    String selectedCategory;
    ArrayAdapter<CharSequence> adapterOfCateg;

    EditText bountyU, bountyL, searchUser, searchTitle;
    int intBountyU, intBountyL;
    String strSearchUser, strSearchTitle;
    Button apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);

        ReqOrOffer = (Spinner) findViewById(R.id.spReqOrOfferInFilter);
        ReqOrOfferStr = null;
        adapterOfRoO = ArrayAdapter.createFromResource(this, R.array.Request_Offer, android.R.layout.simple_spinner_item);

        categorySpinner = (Spinner) findViewById(R.id.spCategoryInFilter);
        selectedCategory = null;
        adapterOfCateg = ArrayAdapter.createFromResource(this, R.array.category_list, android.R.layout.simple_spinner_item);

        bountyU = (EditText) findViewById(R.id.etBountyUpper);
        intBountyU = Integer.MAX_VALUE;
        bountyL = (EditText) findViewById(R.id.etBountyLower);
        intBountyL = Integer.MIN_VALUE;
        searchUser = (EditText) findViewById(R.id.etSearchUser);
        strSearchUser  = null;
        searchTitle = (EditText) findViewById(R.id.etSearchTitle);
        strSearchTitle = null;
        apply = (Button) findViewById(R.id.bApplyFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bApplyFilter:
                break;
        }
    }


}
