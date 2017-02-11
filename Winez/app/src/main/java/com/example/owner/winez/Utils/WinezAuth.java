package com.example.owner.winez.Utils;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.example.owner.winez.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Ziv on 29/01/2017.
 */

public class WinezAuth {
    private FirebaseAuth mAuth;
    private static WinezAuth _instance;
    private User currentUser;
    private OnAuthChangeListener onAuthChangeListener;

    private WinezAuth() {
        this.mAuth = FirebaseAuth.getInstance();

        // Listening for auth changes
        this.mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser usr = firebaseAuth.getCurrentUser();
                if (usr != null) {
                    getCurrentUser(usr);
                }
            }
        });
    }


    public static WinezAuth getInstance() {
        if (_instance == null) {
            _instance = new WinezAuth();
        }

        return _instance;
    }

    /**
     * Current connected user
     *
     * @return
     */
    public User getCurrentUser() {
        return this.currentUser;
    }

    public void registerUser(String email,
                             String password,
                             @NonNull Activity activity,
                             @NonNull OnCompleteListener<AuthResult> onComplete) {
        this.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, onComplete);

    }

    /**
     * Checks if firebase is authenticated
     *
     * @return
     */
    public boolean isAuthenticated() {
        return this.mAuth.getCurrentUser() != null;
    }

    public void fetchUser(){
        this.mAuth = FirebaseAuth.getInstance();
        this.getCurrentUser(mAuth.getCurrentUser());
    }
    private void getCurrentUser(final FirebaseUser usr) {
        // Getting current user from db
        WinezDB.getInstance().getSingle(User.class.getSimpleName(), User.class, usr.getUid(), new WinezDB.GetOnCompleteResult<User>() {
            @Override
            public void onResult(User data) {
                // Making sure we got the right user
                if (data != null && (currentUser == null || currentUser.getUid().compareTo(data.getUid()) != 0)) {
                    currentUser = data;
                    if (onAuthChangeListener != null) {
                        onAuthChangeListener.onLogin(currentUser);
                    }
                }
            }

            @Override
            public void onCancel(String err) {

            }
        });
    }

    public String getUserUid(){
        return this.mAuth.getCurrentUser().getUid();
    }

    public Task<AuthResult> authenticate(String email, String password) {
        return this.mAuth.signInWithEmailAndPassword(email,password);
    }

    public void setOnAuthChangeListener(OnAuthChangeListener onAuthChange){
        this.onAuthChangeListener = onAuthChange;
    }


    public void signOut(){
        this.mAuth.signOut();
        this.currentUser = null;
        if (this.onAuthChangeListener!= null){
            this.onAuthChangeListener.onLogout();
        }
    }

    public void setUserFromLocal(User toReturn) {
        this.currentUser = toReturn;
    }

    public interface OnAuthChangeListener{
        void onLogin(User usr);
        void onLogout();
    }
}
