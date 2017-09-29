package com.nivgelbermann.fooddiarydemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class AddEditActivity extends AppCompatActivity {
    private static final String TAG = "AddEditActivity";

//    @BindView(R.id.add_edit_toolbar)
//    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ButterKnife.bind(this);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // TODO Set text to "Create item"/"Edit item" according to whether activity was created with a POJO included in the bundle
        // TODO Pass onto activity the tab (month) from which it was opened + FoodItem object if selected
        getSupportActionBar().setTitle("Create Item:");
        getSupportActionBar().setElevation(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.menu_addedit_cancel:
                onBackPressed();
                break;

            case R.id.menu_addedit_delete:
                // TODO Handle item deletion after implementing opening activity by clicking an item
                break;

            // Handles home-button behaviour in pre-21sdk
            case android.R.id.home:
                onBackPressed();
                break;

            default:
//                throw new InvalidParameterException(TAG + ".onOptionsItemSelected called with invalid MenuItem " + item.getTitle());
                Toast.makeText(this, "An error has occurred, changes have not been saved", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
