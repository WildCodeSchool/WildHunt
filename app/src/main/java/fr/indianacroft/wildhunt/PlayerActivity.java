package fr.indianacroft.wildhunt;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class PlayerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView imageViewAvatar;
    private String mUserId, mUser_quest, mUser_name, mQuest_description, mQuest_name, mUser_indice,
            mName_challenge, mDiff_challenge, mHint_challenge, mKey_challenge, mCreatorId, mQuestId,
            mUser_challenge;
    private int mNbrePoints, mUser_score;
    private ValueEventListener mListener = null;
    private DatabaseReference mRefUser = null;
    private String mIndice = "false";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // To find User Key and link it to a quest
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mUserId = sharedPreferences.getString("mUserId", mUserId);
        Log.d("key", mUserId);

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

        // Bottom Navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setAnimation(null);
        bottomNavigationView.setSelectedItemId(R.id.navigation_validate);

        // Avatar
        imageViewAvatar = findViewById(R.id.imageViewAvatar);
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        // On appele les methodes declarées plus bas (pour chercher l'user, la quete, les challenges)
        searchUser();

        View navigation_validate = findViewById(R.id.navigation_validate);
        navigation_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mUser_quest.equals("Pas de qûete pour l'instant")) {
                    Intent intent = new Intent(getApplicationContext(), PlayerActivity_PopUp.class);
                    intent.putExtra("mChallengeKey", mKey_challenge); //On envoie l'ID du challenge
                    intent.putExtra("mCreatorId", mCreatorId);
                    intent.putExtra("mQuestId", mQuestId);
                    intent.putExtra("mUser_indice", mIndice);
                    startActivity(intent);
                    mRefUser.removeEventListener(mListener);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_noquest, Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    // METHODE POUR TROUVER USER
    private void searchUser() {


        // On recupere toutes les données de l'user actuel
        mRefUser = FirebaseDatabase.getInstance().getReference().child("User").child(mUserId);
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mUser_name = user.getUser_name();
                mUser_quest = user.getUser_quest();             // on recupere la quete dans laquelle il est actuellement
                mUser_indice = user.getUser_indice();           // on recupere son indice
                mUser_challenge = user.getUser_challenge();     // on recupere le challenge dans lequel il est actuellement
                mUser_score = user.getScore();                  // on recupere son score

                Log.d("indice", mUser_indice);

                searchQuest();

                View navigation_leave = findViewById(R.id.navigation_leave);
                navigation_leave.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(PlayerActivity.this, R.style.MyDialog);
                        } else {
                            builder = new AlertDialog.Builder(PlayerActivity.this);
                        }
                        AlertDialog show = builder.setTitle(R.string.title_alertdialog_abort)
                                .setMessage(R.string.alertdialog_abort)
                                .setPositiveButton(R.string.abandonpartie, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseReference refUserQuest = FirebaseDatabase.getInstance()
                                                .getReference().child("User")
                                                .child(mUserId).child("user_quest");
                                        refUserQuest.setValue("Pas de qûete pour l'instant");
                                        startActivity(new Intent(getApplicationContext(), LobbyActivity.class));
                                    }
                                })

                                .setNegativeButton(R.string.abandondefi, new DialogInterface.OnClickListener() {


                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseReference abandonDefi = FirebaseDatabase.getInstance().getReference().child("User")
                                                .child(mUserId).child("user_challenge");
                                        //abandonDefi.setValue("Pas de défi pour l'instant"); CA FAIT BUGGUER
                                        // Modifier le champ dans le user Player pour mettre le challenge en done !
                                        mRefUser.child("challenge_done")
                                                .child(mUser_challenge).child("state").setValue("true");
                                        mRefUser.child("user_indice").setValue("false");
                                        startActivity(new Intent(getApplicationContext(), PlayerActivity.class));
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mRefUser.addListenerForSingleValueEvent(mListener);
    }

    // METHODE POUR TROUVER QUETE
    private void searchQuest() {
        //On recupere toutes les données de la quete de l'user
        final DatabaseReference refUserQuest = FirebaseDatabase.getInstance().getReference().child("Quest").child(mUser_quest);
        refUserQuest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Quest quest = dataSnapshot.getValue(Quest.class);
                // On recupere la qûete liée a un user
                if (quest != null) {
                    mQuest_name = quest.getQuest_name();
                    Log.d(mQuest_name, "quest");
                    mQuest_description = quest.getQuest_description();
                    final TextView playerActivityQuestName = findViewById(R.id.playerActivityNameQuestTitle);
                    playerActivityQuestName.setText(mQuest_name);
                    searchChallenges();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // METHODE POUR TROUVER TOUT LES CHALLENGES
    private void searchChallenges() {

        // On recupere les données des challenges
        DatabaseReference refUserChallenge = FirebaseDatabase.getInstance().getReference().child("Challenge").child(mUser_quest);
        refUserChallenge.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String[] mapChallenges = new String[((int) dataSnapshot.getChildrenCount())];
                int i = 0;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Challenge challenge = dsp.getValue(Challenge.class);

                    // On recupere les challenges qui correspondent a la qûete
                    // On les ajoutes au tableau
                    mapChallenges[i] = dsp.getKey();

                    i++;
                }
                // Si le challenge dans le tableau correspond au challenge en cours du joueur
                for (int j = 0; j < mapChallenges.length; j++) {
                    if (mapChallenges[j].equals(mUser_challenge)) {

                        // On verifie si ce challenge n'est pas dans le tableau des dones
                        final DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("User").child(mUserId);
                        final int finalJ = j;
                        refUser.child("challenge_done").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Tableau des challenges dones
                                final String[] mapChallengesDone = new String[((int) dataSnapshot.getChildrenCount())];
                                final int[] i = {0};
                                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                    final String challengeKey = dsp.getKey();
                                    User checkDone = dsp.getValue(User.class);
                                    String check = checkDone.getState();
                                    if (check.equals("true")) {
                                        mapChallengesDone[i[0]] = challengeKey;
                                    }
                                    i[0]++;
                                }

                                //On parcourt les challenges dones
                                for (int h = 0; h < mapChallengesDone.length; h++) {
                                    if (mapChallenges[finalJ].equals(mapChallengesDone[h])) {
                                        final DatabaseReference refUser = FirebaseDatabase.getInstance().getReference();
                                        // Le défi est bon, on passe au suivant !!

                                        // Si le challenge actuel correspond a un challenge done
                                        // on passe au challenge d'indice suivant
                                        if (finalJ + 1 < mapChallenges.length) {
                                            mUser_challenge = mapChallenges[finalJ + 1];

                                            // On assigne l'ID du challenge au joueur
                                            refUser.child("User").child(mUserId).child("user_challenge").setValue(mUser_challenge);
                                            refUser.child("User").child(mUserId).child("user_indice").setValue("false");

                                        } else {
                                            // Tous les défis ont été faits !!!
                                            refUser.child("User").child(mUserId).child("user_challenge").setValue("Pas de défi pour l'instant");
                                            refUser.child("User").child(mUserId).child("user_quest").setValue("Pas de qûete pour l'instant");
                                            // Modifier le champ dans le user Player pour mettre le challenge en done !
                                            refUser.child("User").child(mUserId).child("quest_done").child(mUser_quest).child("state").setValue("true");

                                            startActivity(new Intent(getApplicationContext(), LobbyActivity.class));
                                            if (mRefUser!=null && mListener!=null){

                                                mRefUser.removeEventListener(mListener);
                                                startActivity(new Intent (getApplicationContext(), LobbyActivity.class));
                                                Toast.makeText(PlayerActivity.this,
                                                        R.string.toast_finish, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }

                                // On set le challenge sur la page
                                final TextView textViewPlayerActivityHint = findViewById(R.id.textViewPlayerActivityHint);
                                final Button playerActivityNumChallenge = findViewById(R.id.playerActivityNumChallenge);

                                // On recupere les données des challenges

                                DatabaseReference refUserChall = FirebaseDatabase.getInstance().getReference().child("Challenge").child(mUser_quest).child(mUser_challenge);
                                refUserChall.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        Challenge challenge = dataSnapshot.getValue(Challenge.class);
                                        // On recupere les challenges qui correspondent a la qûete

                                        mKey_challenge = dataSnapshot.getKey();
                                        mName_challenge = challenge.getChallenge_name();
                                        mHint_challenge = challenge.getHint_challenge();
                                        mDiff_challenge = challenge.getChallenge_difficulty();
                                        mCreatorId = challenge.getChallenge_creatorID();
                                        mQuestId = challenge.getChallenge_questId();
                                        mNbrePoints = challenge.getChallenge_nbrePoints();

                                        // On change la page dynamiquement !!
                                        // Reference to an image file in Firebase Storage
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Quest").child(mUser_quest).child(mKey_challenge);
                                        // ImageView in your Activity
                                        final ImageView imageViewPhotoChallenge = findViewById(R.id.imageViewHomeJoueurToFind);
                                        final TextView playerActivityDuration = findViewById(R.id.textViewDifficulty);

                                        // Load the image using Glide
                                        Glide.with(getApplicationContext())
                                                .using(new FirebaseImageLoader())
                                                .load(storageReference)
                                                .skipMemoryCache(true)
                                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .into(imageViewPhotoChallenge);
                                        textViewPlayerActivityHint.setText(mHint_challenge);
                                        playerActivityNumChallenge.setText(mName_challenge);
                                        playerActivityDuration.setText(mDiff_challenge);

                                        final TextView textViewPlayerActivityHint = findViewById(R.id.textViewPlayerActivityHint);

                                        // Indice a montrer si indice déja utilisé c'est a dire True dans la bdd
                                        if (mUser_indice.equalsIgnoreCase("true")) {

                                        } else {
                                            textViewPlayerActivityHint.setText(R.string.hint_need);
                                        }

                                        // Indice au clic
                                        View navigation_hint = findViewById(R.id.navigation_hint);
                                        navigation_hint.setOnClickListener(new View.OnClickListener() {
                                            boolean isClicked = false;

                                            @Override
                                            public void onClick(View v) {
                                                //si l'indice est déclaré false dans la bdd cest qu'il n'a jamais été utilisé
                                                if (mUser_indice.equalsIgnoreCase("false")) {
                                                    if (!isClicked) {
                                                        isClicked = true;
                                                        Toast.makeText(getApplicationContext(), R.string.warning_hint, Toast.LENGTH_SHORT).show();
                                                    } else if (isClicked) {
                                                        textViewPlayerActivityHint.setText(mHint_challenge);
//
                                                        mRefUser.child("user_indice").setValue("true");
                                                        mIndice = "true";
                                                    }
                                                }
                                            }
                                        });

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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    // Bottom Navigation Bar
    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_validate:
                    return true;
                case R.id.navigation_hint:
                    return true;
                case R.id.navigation_leave:
                    return true;
            }
            return false;
        }
    };

    // Share via other apps
    private ShareActionProvider mShareActionProvider;
}