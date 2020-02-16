package com.example.techworkshops;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.techworkshops.model.Student;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private TextView userNameTv,userEmailTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        userNameTv = navigationView.getHeaderView(0).findViewById(R.id.user_name_in_nav_header);
        userEmailTv = navigationView.getHeaderView(0).findViewById(R.id.user_email_in_nav_header);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_log_in, R.id.nav_sign_up,
                R.id.nav_browse_workshops, R.id.nav_dashboard)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        if (firebaseAuth.getCurrentUser() != null) {

            NavGraph navGraph = navController.getGraph();
            navGraph.setStartDestination(R.id.nav_dashboard);
            navController.setGraph(navGraph);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("students").whereEqualTo("emailId",firebaseAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Student student = queryDocumentSnapshots.toObjects(Student.class).get(0);
                            userEmailTv.setVisibility(View.VISIBLE);
                            userNameTv.setVisibility(View.VISIBLE);
                            userNameTv.setText(student.getName());
                            userEmailTv.setText(student.getEmailId());
                        }
                    });
        }

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_logout) {
            if (firebaseAuth.getCurrentUser() != null){
                firebaseAuth.signOut();
                userEmailTv.setText("");
                userNameTv.setText("");
                if (navController.getCurrentDestination().getId() != R.id.nav_browse_workshops){
                    navController.navigate(R.id.nav_browse_workshops);
                }

            } else {
                Toast.makeText(this, "You have not logged in yet", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
