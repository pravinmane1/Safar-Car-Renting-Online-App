package com.clzproject.safar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.clzproject.safar.sql.DatabaseHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AdminDashboardActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    TextView headerName,headerEmail,headerBalance;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase db;
    DatabaseReference mRef;
    CardView allCars,carBookings,users,logout;

    boolean val=false;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_admin_dashboard);

        //setStatusBarGradiant(this);
        //((ScrollView) findViewById(R.id.container)).setPadding(0,0,0,getSoftButtonsBarSizePort(this));


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();

        allCars = findViewById(R.id.all_cars);
        carBookings = findViewById(R.id.car_bookings);
        users = findViewById(R.id.users);
        logout = findViewById(R.id.logout);

        allCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this,CarsActivity.class));
            }
        });
        carBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this,AllTripsActivity.class));
            }
        });
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminDashboardActivity.this,AllUsersActivity.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminDashboardActivity.this);
                builder1.setMessage("Click Yes To Logout");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                DatabaseHelper databaseHelper = new DatabaseHelper(AdminDashboardActivity.this);
                                boolean success = databaseHelper.deleteData();


                                if (success){
                                    mAuth.signOut();
                                    mGoogleSignInClient.signOut();

                                    // mGoogleSignInClient.signOut();
                                    Toast.makeText(AdminDashboardActivity.this,"Successfully Signed Out", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AdminDashboardActivity.this,LoginActivity.class));
                                    finish();
                                }
                                else{
                                    Toast.makeText(AdminDashboardActivity.this, "SQL database error", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder1.create();
                alert.show();
            }
        });


        String id = mAuth.getUid();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))//"111458686493-4j7c3h8hkh0pbas985vlhj1i1esmniu0.apps.googleusercontent.com"
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        mRef = db.getReference("Users");

    }

    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));

        }
    }

    @Override
    public void onBackPressed() {
        if(!val) {
            Toast.makeText(AdminDashboardActivity.this, "Press Again To Exit", Toast.LENGTH_SHORT).show();
            val = true;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    val=false;
                }
            }, 2000);
        }
        else {
            super.onBackPressed();
        }

    }

    public static int getSoftButtonsBarSizePort(Activity activity) {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

}
