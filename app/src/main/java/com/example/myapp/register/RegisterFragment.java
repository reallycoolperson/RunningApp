package com.example.myapp.register;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapp.MainActivity;
import com.example.myapp.NavGraphDirections;
import com.example.myapp.R;
import com.example.myapp.data.User;
import com.example.myapp.databinding.FragmentRegisterBinding;
import com.example.myapp.login.UserViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private NavController navController;
    private MainActivity mainActivity;
    private UserViewModel userViewModel;
    private String encodedimage = "";
    private int flag_default_picture = 1;



    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
        userViewModel = new ViewModelProvider(mainActivity).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        FloatingActionButton fab = binding.floatingActionButton;


        ////////////////////////////////slika//////////////////////////////////
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseProfilePicture(); //da se prikaze dialog pa se bira galerija ili take photo
            }
        });

        ////////////////////////////////registracija//////////////////////////////////
        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fullname = binding.imeiprezime.getEditText().getText().toString();
                String username = binding.username.getEditText().getText().toString();
                String password = binding.password.getEditText().getText().toString();
                String email = binding.email.getEditText().getText().toString();

                if (!fullname.equals("") && !username.equals("") && !password.equals("") && !email.equals("")) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            String filename = "";
                            if(flag_default_picture==0) filename = "" + username  + ".jpg";

                            Log.d("ispis", "filename je " + filename);
                            String[] field = new String[6];
                            field[0] = "fullname";
                            field[1] = "username";
                            field[2] = "password";
                            field[3] = "email";
                            field[4] = "upload";
                            field[5] = "filename";


                            String[] data = new String[6];
                            data[0] = binding.imeiprezime.getEditText().getText().toString();
                            data[1] = binding.username.getEditText().getText().toString();
                            data[2] = binding.password.getEditText().getText().toString();
                            data[3] = binding.email.getEditText().getText().toString();
                            data[4] = encodedimage;
                            data[5] = filename;


                            PutData putData = new PutData("http://192.168.1.50:80/loginRegister/signup.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    Log.d("tu", "prvi1: ");

                                    if(result.equals("success"))
                                    {
                                        userViewModel.insertUser(new User(0, data[0], data[3], data[2], data[1], filename));
                                        navController.navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment2());
                                        Log.d("tu", "prvi4: ");
                                        Toast.makeText(mainActivity, mainActivity.getResources().getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                                    }
                                    else if (result.equals("taken_username_email"))
                                    {
                                        binding.username.getEditText().setError(mainActivity.getResources().getString(R.string.username_taken));
                                        binding.email.getEditText().setError(mainActivity.getResources().getString(R.string.email_taken));
                                    }
                                    else if (result.equals("taken_username"))
                                    {
                                        binding.username.getEditText().setError(mainActivity.getResources().getString(R.string.username_taken));
                                    }
                                    else if  (result.equals("taken_email"))
                                    {
                                        binding.email.getEditText().setError(mainActivity.getResources().getString(R.string.email_taken));
                                    }
                                   // Log.d("ispis3", result);
                                }
                            }
                        }
                    });

                }//end_if
                else
                {
                    if (fullname.equals("")) binding.imeiprezime.getEditText().setError(mainActivity.getResources().getString(R.string.error_login_register));
                    if (username.equals("")) binding.username.getEditText().setError(mainActivity.getResources().getString(R.string.error_login_register));
                    if (email.equals("")) binding.email.getEditText().setError(mainActivity.getResources().getString(R.string.error_login_register));
                    if (password.equals("")) binding.password.getEditText().setError(mainActivity.getResources().getString(R.string.error_login_register));

                }
            }
        });//end_registracija
        return binding.getRoot();
    }

        private void chooseProfilePicture()
    {
            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
            LayoutInflater inflater2= getLayoutInflater();
            View dialogView = inflater2.inflate(R.layout.alert_dialog_profile_picture, null);
            builder.setCancelable(true);
            builder.setView(dialogView);

            ImageView imageViewADPPCamera = dialogView.findViewById(R.id.imageViewADPPCamera);
            ImageView imageViewADPPGallery = dialogView.findViewById(R.id.imageViewADPPGallery);

            final AlertDialog alertDialogProfilePicture = builder.create();
            alertDialogProfilePicture.show();

            imageViewADPPCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkAndRequestPermissions()) { //trazimo dozvolu za kameru
                        takePictureFromCamera();
                        alertDialogProfilePicture.dismiss();
                    }
                }
            });

            imageViewADPPGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    takePictureFromGallery();
                    alertDialogProfilePicture.dismiss();
                }
            });
        }

        private void takePictureFromGallery(){
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);
        }

        private void takePictureFromCamera(){
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePicture.resolveActivity(mainActivity.getPackageManager()) != null){
                startActivityForResult(takePicture, 2);
            }
        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == mainActivity.RESULT_OK) {
                    flag_default_picture = 0;
                    Uri uri = data.getData();
                    binding.imageView.setImageURI(uri);
                    try (InputStream inputStream = mainActivity.getContentResolver().openInputStream(uri)) {
                        Bitmap bitmapImage = BitmapFactory.decodeStream(inputStream);
                        encodebitmap(bitmapImage);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if (resultCode == mainActivity.RESULT_OK) {
                    flag_default_picture = 0;
                    Bundle bundle = data.getExtras();
                    Bitmap bitmapImage = (Bitmap) bundle.get("data");
                    binding.imageView.setImageBitmap(bitmapImage);
                    encodebitmap(bitmapImage);
                }
                break;
        }
    }

    //slika je enkodovana kao base64
    private void encodebitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] byteofimages=byteArrayOutputStream.toByteArray();
        encodedimage= Base64.encodeToString(byteofimages, Base64.DEFAULT);
    }


    private boolean checkAndRequestPermissions(){
        if(Build.VERSION.SDK_INT >= 23){
            int cameraPermission = ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.CAMERA);
            if(cameraPermission == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.CAMERA}, 20);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 20 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            takePictureFromCamera();
        }
        else
            Toast.makeText(mainActivity, "Permission not Granted", Toast.LENGTH_SHORT).show();
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }




}
