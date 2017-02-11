package com.example.owner.winez;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.owner.winez.Model.Model;
import com.example.owner.winez.Utils.WinezAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import com.example.owner.winez.Model.User;

public class RegisterFrag extends Fragment {
    private EditText username;
    private EditText email;
    private EditText password;
    private Button enter;
    private CheckBox alreadyUser;
    private OnClickNotUser onClickNotUser;
    private OnClickUser onClickUser;

    public RegisterFrag() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_register, container, false);
        username = (EditText) view.findViewById(R.id.reg_username);
        email = (EditText) view.findViewById(R.id.reg_email);
        password = (EditText) view.findViewById(R.id.reg_password);
        enter = (Button) view.findViewById(R.id.reg_enter);
        alreadyUser = (CheckBox) view.findViewById(R.id.reg_already_user_check);
        onClickNotUser = new OnClickNotUser();
        onClickUser = new OnClickUser();
        enter.setOnClickListener(onClickNotUser);

        // Change between login mode to register mode
        alreadyUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    enter.setOnClickListener(onClickUser);
                    username.setEnabled(false);
                    username.setInputType(InputType.TYPE_NULL);
                } else {
                    enter.setOnClickListener(onClickNotUser);
                    username.setEnabled(true);
                    username.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                }
            }
        });
        return view;
    }

    private class OnClickNotUser implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            // Disabling button
            enter.setEnabled(false);

            // Validating fields
            if (enter.getText().length() == 0 ||
                password.getText().length() == 0 ||
                username.getText().length() ==0){
                Toast.makeText(getActivity(),
                        "Registration failed",
                        Toast.LENGTH_SHORT).show();
                enter.setEnabled(true);
            } else {
                WinezAuth.getInstance().
                        registerUser(email.getText().toString(),
                                password.getText().toString(),
                                getActivity(),
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        // Checking if add was successful
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(getActivity(),
                                                    "Registration failed",
                                                    Toast.LENGTH_SHORT).show();
                                            enter.setEnabled(true);
                                        } else {
                                            // Adding user to db
                                            User usrToAdd =
                                                    new User(username.getText().toString(),
                                                            email.getText().toString(),
                                                            task.getResult().getUser().getUid());
                                            Model.getInstance().saveCurrentUser(usrToAdd).addOnCompleteListener(getActivity(),
                                                    new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getActivity(),
                                                                    "Successful registration!",
                                                                    Toast.LENGTH_SHORT).show();
                                                            getActivity().getFragmentManager().beginTransaction().remove(RegisterFrag.this).commit();
                                                        }
                                                    });
                                        }
                                    }
                                });
            }
        }
    }
    private class OnClickUser implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            enter.setEnabled(false);
            Model.getInstance()
                    .authenticate(email.getText().toString(),
                            password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()){
                        Toast.makeText(getActivity(),
                                "Login failed! :(",
                                Toast.LENGTH_SHORT).show();
                        enter.setEnabled(true);
                    } else{
                        Toast.makeText(getActivity(),
                                "Login Successful!",
                                Toast.LENGTH_SHORT).show();
                        getActivity().getFragmentManager().popBackStack();
                    }
                }
            });
        }
    }
}
