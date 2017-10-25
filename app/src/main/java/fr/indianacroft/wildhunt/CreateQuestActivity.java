package fr.indianacroft.wildhunt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ActionProvider;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CreateQuestActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button butAddNewChallenge, button_create_quest;
    TextView quest_title, empty, description_title;
    EditText name_quest, description_quest;
    ImageView imageViewAvatar, imageViewPirate;
//    Spinner spinner_quest;
    ListView listView;
    DatabaseReference childRef;
    private String mUserId, mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quest);

        // Pour recuperer la key d'un user (pour le lier a une quête)
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mUserId = sharedPreferences.getString("mUserId", mUserId);
        Log.d("key", mUserId);

        name_quest = findViewById(R.id.name_quest);
        description_quest = findViewById(R.id.description_quest);
        button_create_quest = findViewById(R.id.button_create_quest);

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
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Spinner
//        spinner_quest = (Spinner) findViewById(R.id.spinner_challenge);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
//                R.array.life_duration, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_quest.setAdapter(adapter);

        // Database
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        // childRef.push().getKey() is used to generate the different key
        final String questid = ref.child("Quest").push().getKey();

        // If user is new, can create quest, if no, can't
        button_create_quest = findViewById(R.id.button_create_quest);
        butAddNewChallenge = findViewById(R.id.butAddNewChallenge);
        quest_title = findViewById(R.id.quest_title);
        description_title = findViewById(R.id.description_title);
//        life_duration_quest_title = (TextView) findViewById(R.id.life_duration_quest_title);
//        imageViewPirate = findViewById(R.id.imageViewPirate);
        name_quest = findViewById(R.id.name_quest);
        description_quest = findViewById(R.id.description_quest);
        listView = findViewById(R.id.listViewChallengeCreated);
        empty = findViewById(R.id.empty);
        DatabaseReference db2 = ref.child("User").child(mUserId).child("user_createdquestID");
        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String questCreatedOrNot = dataSnapshot.getValue(String.class);
                if (questCreatedOrNot.equals("null")) {
                    button_create_quest.setVisibility(View.VISIBLE);
                    butAddNewChallenge.setVisibility(View.GONE);
                    quest_title.setVisibility(View.VISIBLE);
                    description_title.setVisibility(View.VISIBLE);
//                    imageViewPirate.setVisibility(View.GONE);
                    name_quest.setVisibility(View.VISIBLE);
                    description_quest.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    empty.setVisibility(View.GONE);

                    button_create_quest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String nameContent = name_quest.getText().toString().trim();
                            String descriptionContent = description_quest.getText().toString().trim();
//                            String spinnerContent = spinner_quest.getSelectedItem().toString();
                            // Impossible to create if nothing is written
                            if ((nameContent.equals("")) || (descriptionContent.equals(""))) {
                                Toast.makeText(getApplicationContext(), R.string.toast_challenge2, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.toast_createdquest, Toast.LENGTH_LONG).show();
                                //  DatabaseReference childRef = ref.getReference("form");
                                // On recupere la quete crée par l'user actuel pour link challenge a la quête
                                DatabaseReference refUser =
                                        FirebaseDatabase.getInstance().getReference().child("User").child(mUserId);
                                refUser.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        mUserName = user.getUser_name();
                                        ref.child("Quest").child(questid).child("quest_creatorName").setValue(mUserName);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                Quest quest = new Quest(nameContent, descriptionContent);

                                quest.setQuest_name(nameContent);
                                quest.setQuest_description(descriptionContent);
//                                quest.setLife_duration(spinnerContent);

                                ref.child("Quest").child(questid).setValue(quest);
                                ref.child("Quest").child(questid).child("quest_creatorId").setValue(mUserId);

                                name_quest.setText("");
                                description_quest.setText("");

                                //On lie la quête créee a un user à Firebase
                                ref.child("User").child(mUserId).child("user_createdquestID").setValue(questid);
                                ref.child("User").child(mUserId).child("user_createdquestName").setValue(nameContent);
                                // Et aux SharedPreferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("mCreatedQuest", questid);
                                editor.apply();
                            }
                        }
                    });
                } else {
                    button_create_quest.setVisibility(View.GONE);
                    butAddNewChallenge.setVisibility(View.VISIBLE);
                    quest_title.setVisibility(View.GONE);
                    description_title.setVisibility(View.GONE);
//                    imageViewPirate.setVisibility(View.VISIBLE);
                    name_quest.setVisibility(View.GONE);
                    description_quest.setVisibility(View.GONE);
                    listView.setEmptyView(findViewById(R.id.empty));

                    // Go to Challenge Activity on click
                    butAddNewChallenge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(CreateQuestActivity.this, ChallengesActivity.class);
                            startActivity(intent);
                        }
                    });

                    // ListView des challenges crées
                    ListAdapter adapterChall = new FirebaseListAdapter<Challenge>(
                            CreateQuestActivity.this,
                            Challenge.class,
                            R.layout.challenge_listview,
                            ref.child("Challenge").child(questCreatedOrNot)) {
                        @Override
                        protected void populateView(View v, Challenge challenge, int position) {
                            ((TextView) v.findViewById(R.id.listViewLabel)).setText(getString(R.string.defi, (position+1)));
                            ((TextView) v.findViewById(R.id.listViewChallengeName))
                                    .setText(challenge.getChallenge_name());
                            ((TextView) v.findViewById(R.id.listViewChallengeDifficulty))
                                    .setText(String.valueOf(challenge.getChallenge_difficulty()));
                        }
                    };
                    listView.setAdapter(adapterChall);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
