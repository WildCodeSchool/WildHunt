package fr.indianacroft.wildhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.ProgressBar;
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

    ImageView imageViewAvatar, imageViewAvatar2, imageView, imageViewMedal;
    private String mUserId, mUser_challenge, mUser_quest;
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
        textView6 = findViewById(R.id.textViewProfileDatas);
        String sharedPrefUserName = sharedpreferences.getString(userName, "");
        textView6.setText(sharedPrefUserName);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Drawer Menu
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        View headerview = navigationView.getHeaderView(0);
        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class));
            }
        });
        // Cannot access to "Gerer ma partie" if no validation pending
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("User");
        rootRef.child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("aValider")) {
                    Menu nav_Menu = navigationView.getMenu();
                    nav_Menu.findItem(R.id.nav_manage).setVisible(true);
                } else {
                    Menu nav_Menu = navigationView.getMenu();
                    nav_Menu.findItem(R.id.nav_manage).setVisible(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        // Cannot access to "Ma partie" if not playing to a quest
        DatabaseReference db = rootRef.child(mUserId).child("user_quest");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String questOrNot = dataSnapshot.getValue(String.class);
                if (questOrNot.equals("Pas de qûete pour l'instant")) {
                    Menu nav_Menu = navigationView.getMenu();
                    nav_Menu.findItem(R.id.nav_play).setVisible(false);
                } else {
                    Menu nav_Menu = navigationView.getMenu();
                    nav_Menu.findItem(R.id.nav_play).setVisible(true);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Avatar
        imageViewAvatar = findViewById(R.id.imageViewAvatar);
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, getString(R.string.toast_error_profile), Toast.LENGTH_SHORT).show();
            }
        });

        // Lien PopUp
        imageViewAvatar2 = findViewById(R.id.imageViewAvatar2);
        imageViewAvatar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity_PopUp.class);
                startActivity(intent);
            }
        });

        // Pour afficher le score et le grade personnalisé
        imageView = findViewById(R.id.imageView);
        imageViewMedal = findViewById(R.id.imageView2);
        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        DatabaseReference refScore = ref.getReference("User").child(mUserId);
        refScore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mUser_quest = user.getUser_quest();
                mUser_challenge = user.getUser_challenge();
                int score = user.getScore();

                // Progress Bar
                int nv = 0;
                float currentPercent = 0;
                ProgressBar simpleProgressBar = findViewById(R.id.progressBar2);
                simpleProgressBar.setMax(100);
                TextView min = findViewById(R.id.nbrMin);
                TextView max = findViewById(R.id.nbrMax);
                simpleProgressBar.getProgressDrawable().setColorFilter(
                        getResources().getColor(R.color.colorYellow), android.graphics.PorterDuff.Mode.SRC_IN);
                simpleProgressBar.setScaleY(3f);

                // Score
                TextView affPoint = findViewById(R.id.point);
                affPoint.setText(String.valueOf(score));
                if (score < 100){
                    //touriste
                    TextView touriste = findViewById(R.id.titre);
                    touriste.setText(R.string.touriste);
                    imageView.setImageResource(R.drawable.jake1);
                    imageViewMedal.setImageResource(R.drawable.medal_bronze);
                    currentPercent = score/100f;
                    min.setText("0");
                    max.setText("100");
                } else if (score < 400) {
                    //voyageur
                    TextView voyageur = findViewById(R.id.titre);
                    voyageur.setText(R.string.voyageur);
                    imageView.setImageResource(R.drawable.jake2);
                    imageViewMedal.setImageResource(R.drawable.medal_silver);
                    nv = 1;
                    currentPercent = (score-100f)/(400f-100f);
                    min.setText("100");
                    max.setText("400");
                } else if (score < 1000) {
                    //conquerant
                    TextView conquerant = findViewById(R.id.titre);
                    conquerant.setText(R.string.conquerant);
                    imageView.setImageResource(R.drawable.jake3);
                    imageViewMedal.setImageResource(R.drawable.medal_gold);
                    nv = 2;
                    currentPercent = (score-400f)/(1000f-400f);
                    min.setText("400");
                    max.setText("1000");
                } else {
                    //dominateur du monde
                    TextView dominateur = findViewById(R.id.titre);
                    dominateur.setText(R.string.dominateur);
                    imageView.setImageResource(R.drawable.jake4);
                    imageViewMedal.setImageResource(R.drawable.medal_gold2);
                    nv = 3;
                    currentPercent = 1f;
                    min.setText("1000");
                    max.setText("Infini");
                }

                simpleProgressBar.setProgress(Math.round(currentPercent*100));

                if ((mUser_quest.equals("Pas de qûete pour l'instant")) || (mUser_challenge.equals("Pas de défi pour l'instant"))) {
                    TextView titreQueste = findViewById(R.id.name_quest);
                    titreQueste.setText(mUser_quest);
                    TextView titreChallenge = findViewById(R.id.challenge_name);
                    titreChallenge.setText(mUser_challenge);
                } else {
                    FirebaseDatabase ref = FirebaseDatabase.getInstance();
                    DatabaseReference refQuest = ref.getReference().child("Quest").child(mUser_quest);
                    refQuest.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Quest quest = dataSnapshot.getValue(Quest.class);
                            String mQuest_name = quest.getQuest_name();
                            TextView titreQueste = findViewById(R.id.name_quest);
                            titreQueste.setText(mQuest_name);

                            FirebaseDatabase ref = FirebaseDatabase.getInstance();
                            DatabaseReference refChallenge = ref.getReference().child("Challenge").child(mUser_quest).child(mUser_challenge);
                            refChallenge.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Challenge challenge = dataSnapshot.getValue(Challenge.class);
                                    String  mChallenge_name = challenge.getChallenge_name();
                                    TextView titreChallenge = findViewById(R.id.challenge_name);
                                    titreChallenge.setText(mChallenge_name);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
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
                    .error(R.drawable.jake5)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageViewAvatar2);
    }

    // Drawer Menu
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        // Handle navigation view item clicks here.
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
        } else if (id == R.id.nav_credits) {
            startActivity(new Intent(getApplicationContext(), CreditsActivity.class));
        } else if (id == R.id.nav_delete) {
            startActivity(new Intent(getApplicationContext(), ConnexionActivity.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
