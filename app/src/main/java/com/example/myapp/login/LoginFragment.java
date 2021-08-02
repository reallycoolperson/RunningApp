package com.example.myapp.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.myapp.ActivityWithDrawer;
import com.example.myapp.MainActivity;
import com.example.myapp.NavGraphDirections;
import com.example.myapp.R;
import com.example.myapp.data.User;
import com.example.myapp.databinding.FragmentLoginBinding;
import com.example.myapp.workouts.WorkoutCreateFragmentDirections;
import com.example.myapp.workouts.WorkoutListFragment;
import com.example.myapp.workouts.WorkoutListFragmentDirections;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.List;



public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private NavController navController;
    private MainActivity mainActivity;
    private UserViewModel userViewModel;

    public LoginFragment() {
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
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);


        /////////////////////////////////REMEMBER ME//////////////////////////////////////////
        final SharedPreferences[] preferences = {mainActivity.getSharedPreferences("checkbox", Context.MODE_PRIVATE)};
        String checkbox = preferences[0].getString("remember", "");
        if (checkbox.equals("true")) {
            Intent intent = new Intent(mainActivity, ActivityWithDrawer.class);
            startActivity(intent);
        } else if (checkbox.equals("false")) {
            //Toast.makeText(mainActivity, "first login man", Toast.LENGTH_SHORT).show();

        }

        final String[] checked = {"false"};
        binding.rememberMeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (button.isChecked()) {
                    checked[0] = "true";

                } else if (!button.isChecked()) {
                    checked[0] = "false";
                }
            }
        });

            //////////////////////////////////////LOGOVANJE///////////////////////////////////////
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String username = binding.username.getEditText().getText().toString();
                String password = binding.password.getEditText().getText().toString();

                if (!username.equals("") && !password.equals("")) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            String[] field = new String[2];
                            field[0] = "username";
                            field[1] = "password";

                            String[] data = new String[2];
                            data[0] = binding.username.getEditText().getText().toString();
                            data[1] = binding.password.getEditText().getText().toString();

                            PutData putData = new PutData("http://192.168.1.50:80/loginRegister/login.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    Log.d("lala", result);

                                    if (result.equals("Login Success")) {


                                        //ne moze bazi da pristupi iz main niti da je ne bi zablokiro
                                        AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {

                                                SharedPreferences preferences = mainActivity.getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("remember", checked[0]);
                                                List<User> user = userViewModel.getFullname(binding.username.getEditText().getText().toString());
                                                Log.d("is", "size " + user.get(0).getFullname());
                                                String fullname = user.get(0).getFullname();
                                                editor.putString("fullname", fullname);
                                                editor.putString("show_once", "1");

                                                editor.putString("username", binding.username.getEditText().getText().toString());
                                                editor.putString("filename", user.get(0).getFilename());

                                                Log.d("ispis", "filename=" + user.get(0).getFilename());

                                                editor.apply();

                                            }
                                        });

                                        Intent intent = new Intent(mainActivity, ActivityWithDrawer.class);
                                        startActivity(intent);



                                    } else {
                                        binding.username.getEditText().setError(mainActivity.getResources().getString(R.string.error_wrong_login));
                                        binding.password.getEditText().setError(mainActivity.getResources().getString(R.string.error_wrong_login));
                                    }

                                    //   Log.d("is", result);
                                }
                            }
                        }
                    });

                }
                else
                {

                    if (username.equals("")) binding.username.getEditText().setError(mainActivity.getResources().getString(R.string.error_login_register));
                    if (password.equals("")) binding.password.getEditText().setError(mainActivity.getResources().getString(R.string.error_login_register));

                }
            }
        }); //end_login

        ////////////////////////////////////KLIK - LINK ZA REGISTRACIJU///////////////////////////////////////
        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               navController.navigate(LoginFragmentDirections.actionLoginFragment2ToRegisterFragment());

            }
        });

        return binding.getRoot();

    } //end_onCreateView

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }



}
