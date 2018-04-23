package fr.indianacroft.wildhunt.old;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fr.indianacroft.wildhunt.R;

public class ValidateQuestActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button butAddNewChallenge;
    Button button_create_quest;
    EditText name_quest;
    EditText description_quest;
    Spinner spinner_quest;
    FirebaseDatabase ref;
    DatabaseReference childRef;
    ImageView imageViewAvatar;
    private String mUserId;
    private String mUserName;
    // Share via other apps
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_activity_validate_quest);

        // Pour recuperer la key d'un user (pour le lier a une quête)
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
        // Cannot access to "Ma partie" if not playing to a quest
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("User");
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

        imageViewAvatar = findViewById(R.id.imageViewAvatar);
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        // METHODE POUR TROUVER CHALLENGE
        DatabaseReference refUser =
                FirebaseDatabase.getInstance().getReference().child("User").child(mUserId);
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // on recupere la qûete créee par un user
                User user = dataSnapshot.getValue(User.class);
                final String questId = user.getUser_createdquestID();

                // On parcourt les challenges à valider dans le dossier de la qûete créee (id creator) par un user
                // Sur firebase ca correspond a user_createdquestid
                DatabaseReference refAvalider = FirebaseDatabase.getInstance().
                        getReference("User").child(mUserId).child("aValider").child(questId);
                refAvalider.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<Pair> mapChallengeToValidate = new ArrayList<Pair>((int) dataSnapshot.getChildrenCount());
                        for (final DataSnapshot dsp : dataSnapshot.getChildren()) {
                            // On créer un tableau de la taille de tout les challenges présent

                            // On recupere l'Id du challenge qu'on analyse
                            final String challengeIdToValidate = dsp.getKey();

                            DatabaseReference refChallengeIdToValidate =
                                    FirebaseDatabase.getInstance().getReference("Challenge").child(questId).child(challengeIdToValidate);
                            refChallengeIdToValidate.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //On recupere le nom du challenge correspondant a l'ID
                                    Challenge challenge = dataSnapshot.getValue(Challenge.class);
                                    final String challengeName = challenge.getChallenge_name();
                                    int i = 0;
                                    // On verifie les users dans ce challenge
                                    for (DataSnapshot dsp2 : dsp.child("User").getChildren()) {
                                        // On verifie si un user doit etre validé (false)
                                        if ((boolean) dsp2.getValue() == false) {

                                            // On recupere l'ID de l'user qui doit être validé
                                            final String userIdToValidate = dsp2.getKey();

                                            DatabaseReference refUserToValidate = FirebaseDatabase.getInstance().getReference("User").child(userIdToValidate);
                                            refUserToValidate.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    // On recupere son pseudo
                                                    User user = dataSnapshot.getValue(User.class);
                                                    String userName = user.getUser_name();
                                                    // On link dans un Pair son pseudo avec le nom du challenge
                                                    Pair<String, String> pair = new Pair<String, String>(challengeIdToValidate, userIdToValidate);
                                                    mapChallengeToValidate.add(pair);

                                                    final ValidateAdapter myAdapter = new ValidateAdapter(getApplicationContext(), mapChallengeToValidate);
                                                    ListView listview = findViewById(R.id.listView);
                                                    listview.setAdapter(myAdapter);
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });
                                        }
                                        i++;
                                    }
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
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
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
