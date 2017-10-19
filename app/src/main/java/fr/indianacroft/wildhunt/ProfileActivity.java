package fr.indianacroft.wildhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView imageViewAvatar, imageViewAvatar2;
    private String mUserId;
    final String userName = "NameKey";
    TextView textView6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Save data from SharedPref and put them into textview
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mUserId = sharedpreferences.getString("mUserId", mUserId);
        Log.d("key", mUserId);
        textView6 = (TextView) findViewById(R.id.textViewProfileDatas);
        String sharedPrefUserName = sharedpreferences.getString(userName, "");
        textView6.setText(sharedPrefUserName);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Drawer Menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        View headerview = navigationView.getHeaderView(0);
        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class));            }
        });

        // Avatar
        imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, getString(R.string.toast_error_profile), Toast.LENGTH_SHORT).show();
            }
        });

        // Lien PopUp
        imageViewAvatar2 = (ImageView) findViewById(R.id.imageViewAvatar2);
        imageViewAvatar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity_PopUp.class);
                startActivity(intent);
            }
        });


        // pour afficher le score

        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        DatabaseReference refScore = ref.getReference("User").child(mUserId);
        refScore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                int score = user.getScore();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // Drop Image from Firebase to upload it on Profil page
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference("Avatar");
        StorageReference pathReference = storageRef.child(mUserId);

            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(pathReference)
                    .error(R.drawable.pirate5)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageViewAvatar2);
    }

    // Drawer Menu
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        // TODO : remplacer les toasts par des liens ET faire en sorte qu'on arrive sur les pages de fragments
        if (id == R.id.nav_rules) {
            Intent intent = new Intent(getApplicationContext(), RulesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_play) {
            Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_lobby) {
            Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_create) {
            startActivity(new Intent(getApplicationContext(), CreateQuestActivity.class));
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(getApplicationContext(), ValidateQuestActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_delete) {
            startActivity(new Intent(getApplicationContext(), ConnexionActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Share via other apps
    private ShareActionProvider mShareActionProvider;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.nav_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return true;
    }
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}
