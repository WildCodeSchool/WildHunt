package fr.indianacroft.wildhunt;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import static fr.indianacroft.wildhunt.R.id.buttonHomeJoueurQuitChallenge;
import static fr.indianacroft.wildhunt.R.id.navigation_leave;

public class PlayerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String mUserId;
    private String mUser_name;
    private String mUser_quest;
    private String mQuest_name;
    private String mUser_indice;
    private String mQuest_description;
    private String mLife_duration;
    private String mName_challenge;
    private String mDiff_challenge;
    private String mHint_challenge;
    private String mKey_challenge;
    private String mCreatorId;
    private String mQuestId;
    ImageView imageViewAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


//        final Button buttonHint = (Button) findViewById(R.id.buttonHomeJoueurHint);
        final TextView textViewPlayerActivityHint = (TextView) findViewById(R.id.textViewPlayerActivityHint);

        // Pour recuperer la key d'un user (pour le lier a une quête)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mUserId = sharedPreferences.getString("mUserId", mUserId);
        Log.d("key", mUserId);

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

        // Bottom Navigation bar
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
//        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setAnimation(null);
        bottomNavigationView.setSelectedItemId(R.id.navigation_validate);

        // Avatar
        imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        // POUR CHANGER L'AVATAR SUR LA PAGE AVEC CELUI CHOISI
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Avatar").child(mUserId);
//        // Load the image using Glide
//        if (storageReference.getDownloadUrl().isSuccessful()){
//            Glide.with(getApplicationContext())
//                    .using(new FirebaseImageLoader())
//                    .load(storageReference)
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .into(imageViewAvatar);
//        }

        // On appele les methodes declarées plus bas (pour chercher l'user, la quete, les challenges)
        searchUser();

        View navigation_validate = findViewById(R.id.navigation_validate);
        navigation_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mUser_quest.equals("Pas de qûete pour l'instant")) {
                    Intent intent = new Intent(getApplicationContext(), PlayerActivity_PopUp.class);
                    intent.putExtra("mChallengeKey", mKey_challenge); //On envoie l'ID du challenge
                    intent.putExtra("mCreatorId", mCreatorId);
                    intent.putExtra("mQuestId", mQuestId);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.error_noquest, Toast.LENGTH_SHORT).show();
                }
            }
        });


//        Button buttonSendSolution = (Button) findViewById(R.id.buttonHomeJoueurSendSolution);
//        buttonSendSolution.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(!mUser_quest.equals("Pas de qûete pour l'instant")) {
//                    Intent intent = new Intent(getApplicationContext(), HomeJoueur_PlayerPopUp.class);
//                    intent.putExtra("mChallengeKey", mKey_challenge); //On envoie l'ID du challenge
//                    startActivity(intent);
//                }
//                else{
//                    Toast.makeText(getApplicationContext(), R.string.error_noquest, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

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
        }else if (id == R.id.nav_lobby) {
            Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_create) {
            startActivity(new Intent(getApplicationContext(), CreateQuestActivity.class));
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(getApplicationContext(), ValidateQuestActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_delete) {
            startActivity(new Intent(getApplicationContext(), ConnexionActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // METHODE POUR TROUVER USER
    private void searchUser() {
//        final Button buttonHint = (Button) findViewById(R.id.buttonHomeJoueurHint);
        final TextView textViewPlayerActivityHint = (TextView) findViewById(R.id.textViewPlayerActivityHint);
        final TextView textViewPlayerActivityHint2 = (TextView) findViewById(R.id.textViewPlayerActivityHint2);

        // On recupere toutes les données de l'user actuel
        final DatabaseReference refUser =
                FirebaseDatabase.getInstance().getReference().child("User").child(mUserId);
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mUser_name = user.getUser_name();
                mUser_quest = user.getUser_quest();
                mUser_indice = user.getUser_indice();
                Log.d("indice", mUser_indice);

                // Indice a montrer si indice déja utilisé c'est a dire True dans la bdd
                if (mUser_indice.equalsIgnoreCase("true")) {
                    textViewPlayerActivityHint.setVisibility(View.VISIBLE);
                    textViewPlayerActivityHint2.setVisibility(View.VISIBLE);
//                    buttonHint.setVisibility(View.GONE);
                } else {
                    textViewPlayerActivityHint.setVisibility(View.GONE);
                    textViewPlayerActivityHint2.setVisibility(View.GONE);
                }
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
                            builder.setTitle("Supprimer la partie")
                                    .setMessage("Etes vous sur de vouloir abandonner la partie")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            if(!mUser_quest.equals("Pas de qûete pour l'instant")) {
                                                Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                                                intent.putExtra("mChallengeKey", mKey_challenge);
                                                startActivity(intent);
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();


                        }
                    });



                        // Indice au clic
                        // TODO enlever les points au clic de l'indice
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
                                textViewPlayerActivityHint.setVisibility(View.VISIBLE);
                                textViewPlayerActivityHint2.setVisibility(View.VISIBLE);
//                                buttonHint.setBackgroundColor(Color.RED);
                                refUser.child("user_indice").setValue("true");
                            }
                        }
                    }
                });

//                buttonHint.setOnClickListener(new View.OnClickListener() {
//                    boolean isClicked = false;
//
//                    @Override
//                    public void onClick(View view) {
//                        //si l'indice est déclaré false dans la bdd cest qu'il n'a jamais été utilisé
//                        if (mUser_indice.equalsIgnoreCase("false")) {
//                            if (!isClicked) {
//                                isClicked = true;
//                                Toast.makeText(getApplicationContext(), R.string.warning_hint, Toast.LENGTH_SHORT).show();
//                            } else if (isClicked) {
//                                textViewPlayerActivityHint.setVisibility(View.VISIBLE);
//                                buttonHint.setBackgroundColor(Color.RED);
//                                refUser.child("user_indice").setValue("true");
//                            }
//                        }
//                    }
//                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // METHODE POUR TROUVER QUETE
    private void searchQuest() {
        //On recupere toutes les données de la quete de l'user
        final DatabaseReference refUserQuest = FirebaseDatabase.getInstance().getReference().child("Quest");
        refUserQuest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Quest quest = dsp.getValue(Quest.class);
                    // On recupere la qûete liée a un user
                    if (mUser_quest.equals(dsp.getKey())) {

                        mQuest_name = quest.getQuest_name();
                        Log.d(mQuest_name, "quest");
                        mQuest_description = quest.getQuest_description();


                        final TextView playerActivityQuestName = (TextView) findViewById(R.id.playerActivityNameQuestTitle);
                        playerActivityQuestName.setText(mQuest_name);
                        searcChallenges();
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // METHODE POUR TROUVER CHALLENGE
    private void searcChallenges() {
        final TextView textViewPlayerActivityHint = (TextView) findViewById(R.id.textViewPlayerActivityHint);

        final Button playerActivityNumChallenge = (Button) findViewById(R.id.playerActivityNumChallenge);

        // On recupere les données des challenges
        DatabaseReference refUserChallenge = FirebaseDatabase.getInstance().getReference().child("Challenge").child(mUser_quest);
        refUserChallenge.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Challenge challenge = dsp.getValue(Challenge.class);
                    // On recupere les challenges qui correspondent a la qûete
                    if (challenge.getChallenge_questId().equals(mUser_quest)) {
                        mKey_challenge = dsp.getKey();
                        mName_challenge = challenge.getChallenge_name();
                        Log.d(mName_challenge, "tag");
                        mHint_challenge = challenge.getHint_challenge();
                        mDiff_challenge = challenge.getChallenge_difficulty();
                        mCreatorId = challenge.getChallenge_creatorID();
                        mQuestId = challenge.getChallenge_questId();

                        // On change la page dynamiquement !!
                        // Reference to an image file in Firebase Storage
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Quest").child(mUser_quest).child(mKey_challenge);
                        // ImageView in your Activity
                        final ImageView imageViewPhotoChallenge = (ImageView) findViewById(R.id.imageViewHomeJoueurToFind);
                        final TextView playerActivityDuration = (TextView) findViewById(R.id.textViewDifficulty);


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
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // Bottom Navigation Bar
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            searchUser();
//            searchQuest();
//            searcChallenges();
//            final TextView textViewPlayerActivityHint = (TextView) findViewById(R.id.textViewPlayerActivityHint);
//            boolean isClicked = false;
            final DatabaseReference refUser =
                    FirebaseDatabase.getInstance().getReference().child("User").child(mUserId);

            switch (item.getItemId()) {
                case R.id.navigation_validate:
//                    item.setCheckable(false);
//                    Button buttonSendSolution = (Button) findViewById(R.id.buttonHomeJoueurSendSolution);
//                    buttonSendSolution.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if(!mUser_quest.equals("Pas de qûete pour l'instant")) {
//                                Intent intent = new Intent(getApplicationContext(), HomeJoueur_PlayerPopUp.class);
//                                intent.putExtra("mChallengeKey", mKey_challenge); //On envoie l'ID du challenge
//                                startActivity(intent);
//                            }
//                            else{
//                                Toast.makeText(getApplicationContext(), R.string.error_noquest, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
                    return true;
                case R.id.navigation_hint:
//                    item.setCheckable(false);
//                    buttonHint.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
                            //si l'indice est déclaré false dans la bdd cest qu'il n'a jamais été utilisé
//                            if (mUser_indice.equalsIgnoreCase("false")) {
//                                if (!isClicked) {
//                                    isClicked = true;
//                                    Toast.makeText(getApplicationContext(), R.string.warning_hint, Toast.LENGTH_SHORT).show();
//                                } else if (isClicked) {
//                                    textViewPlayerActivityHint.setVisibility(View.VISIBLE);
////                                    buttonHint.setBackgroundColor(Color.RED);
//                                    refUser.child("user_indice").setValue("true");
//                                }
//                            }
//                        }
//                    });
                    return true;
                case R.id.navigation_leave:
//                    item.setCheckable(false);
                    return true;
            }
            return false;
        }
    };
//    public static class BottomNavigationViewHelper {
//        public static void disableShiftMode(BottomNavigationView view) {
//            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
//            try {
//                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
//                shiftingMode.setAccessible(true);
//                shiftingMode.setBoolean(menuView, false);
//                shiftingMode.setAccessible(false);
//                for (int i = 0; i < menuView.getChildCount(); i++) {
//                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
//                    //noinspection RestrictedApi
//                    item.setShiftingMode(false);
//                    // set once again checked value, so view will be updated
//                    //noinspection RestrictedApi
//                    item.setChecked(item.getItemData().isChecked());
//                }
//            } catch (NoSuchFieldException e) {
//                Log.e("BNVHelper", "Unable to get shift mode field", e);
//            } catch (IllegalAccessException e) {
//                Log.e("BNVHelper", "Unable to change value of shift mode", e);
//            }
//        }
//    }
}