package com.example.svyatoslav.drawable;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {


    final int CAMERA_CAPTURE = 1;
    final int PIC_CROP = 2;
    private Uri picUri;
    private TextView Name, Surname, Phone, nbName, nbPhone;
    private static String Longitude, Latitude;
    private LoginButton facebookLoginBtn;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Name = (TextView) findViewById(R.id.tv_main_name);
        Surname = (TextView) findViewById(R.id.tv_main_surname);
        Phone = (TextView) findViewById(R.id.tv_main_phone);

        callbackManager = CallbackManager.Factory.create();
        facebookLoginBtn = (LoginButton) findViewById(R.id.btn_main_loginButton);
        facebookLoginBtn.setReadPermissions("email");

        // Callback registration
        facebookLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            try {
                // Намерение для запуска камеры
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(captureIntent, CAMERA_CAPTURE);
            } catch ( ActivityNotFoundException e ){
                // Выводим сообщение об ошибке
                String errorMessage = "Ваше устройство не поддерживает съемку";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");

            startActivityForResult(intent, Config.REQUEST_IMAGE);
        } else if (id == R.id.nav_credentials) {
            Intent intent = new Intent(this, SecondActivity.class);
            startActivityForResult(intent, Config.REQUEST_CODE);
        } else if (id == R.id.nav_mark_place) {
            setupPlacePicker();
        } else if (id == R.id.nav_show_map) {
            Intent intent = new Intent(this, SecondActivity.class);
            intent.putExtra("latitude", Latitude);
            intent.putExtra("longitude", Longitude);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // Вернулись от приложения Камера
            if (requestCode == CAMERA_CAPTURE) {
                // Получим Uri снимка
                picUri = data.getData();
                // кадрируем его
                performCrop();
            }
            // Вернулись из операции кадрирования
            else if(requestCode == PIC_CROP){
                Bundle extras = data.getExtras();
                // Получим кадрированное изображение
                Bitmap thePic = extras.getParcelable("data");
                // передаём его в ImageView
                ImageView picView = (ImageView) findViewById(R.id.imageView);
                picView.setImageBitmap(thePic);
                ImageView RectangleImage = (ImageView) findViewById(R.id.iv_main_fromCamera);
                RectangleImage.setImageBitmap(thePic);
            }
        }
        if(requestCode == Config.REQUEST_IMAGE){
            ImageView CirleImage = (ImageView) findViewById(R.id.imageView);
            ImageView RectangleImage = (ImageView) findViewById(R.id.iv_main_fromGallery);
            Uri uri = data.getData();
            Log.d("URI", "Uri: " + uri.toString());
            Picasso.with(this)
                    .load(uri.toString())
                    .into(CirleImage);

            Picasso.with(this)
                    .load(uri.toString())
                    .into(RectangleImage);
        }
        if(requestCode == Config.REQUEST_CODE){
            String name = data.getStringExtra(Config.KEY_NAME);
            String surname = data.getStringExtra(Config.KEY_SURNAME);
            String phone = data.getStringExtra(Config.KEY_PHONE);

            nbName = (TextView) findViewById(R.id.tv_main_name_nb);
            //nbSurname = (TextView) findViewById(R.id.tv_main_surname_nb);
            nbPhone = (TextView) findViewById(R.id.tv_main_phone_nb);

            Name.setText(name);
            Surname.setText(surname);
            Phone.setText(phone);

            nbName.setText(name+" "+surname);
            //Surname.setText(surname);
            nbPhone.setText(phone);
        }
        if(requestCode == Config.REQUEST_PLACES){
            getPlaceData(data);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d("Facebook", data.toString());
    }

    private void performCrop(){
        try {
            // Намерение для кадрирования. Не все устройства поддерживают его
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch(ActivityNotFoundException anfe){
            String errorMessage = "Извините, но ваше устройство не поддерживает кадрирование";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void setupPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try{
            startActivityForResult(builder.build(this), Config.REQUEST_PLACES);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void getPlaceData(Intent data){
        Place place = PlacePicker.getPlace(this, data);
        String name = place.getName().toString();
        String address = place.getAddress().toString();
        TextView tv_main_placePickerReturn = (TextView) findViewById(R.id.tv_main_locationText);
        Latitude = String.valueOf(place.getLatLng().latitude);
        Longitude = String.valueOf(place.getLatLng().longitude);

        tv_main_placePickerReturn.setText(name + " " + address);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("OnConnnectionFailed", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

}
