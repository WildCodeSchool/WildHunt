package fr.indianacroft.wildhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LobbyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

//    private String mUserId;
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
    private String mUser_CreatedQuest;
    private String mUser_CreatedQuestName;
    ImageView imageViewAvatar, imageViewTest;
    private String mUserId, mCreatedQuestId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

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
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        // On appele les methodes declarées plus bas (pour chercher l'user, la quete, les challenges)
        searchUser();

        // Pour remplir la liste des quêtes avec les quêtes créees!!!
        final RecyclerView recyclerViewLobby = findViewById(R.id.recyclerViewHomeJoueurLobby);
        recyclerViewLobby.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Quest");

        final FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<Quest, LobbyViewHolder>(
                Quest.class,
                R.layout.lobby_recyclerview,
                LobbyViewHolder.class,
                ref) {
            @Override
            public void populateViewHolder(LobbyViewHolder holder, Quest bdd, int position) {
                holder.setQuest_name(bdd.getQuest_name());
                holder.setQuest_description(bdd.getQuest_description());
            }
        };

//        int position = 0;
//        if (position == recyclerViewLobby.getChildCount() - 1) {
//            recyclerViewLobby.smoothScrollToPosition(position);
//        }

        // Set the adapter avec les données et la ligne de separation
        recyclerViewLobby.addItemDecoration(new LobbyActivity.SimpleDividerItemDecoration(this));
        recyclerViewLobby.setAdapter(mAdapter);

        // Bouton pour créer sa party
        Button buttonCreateQuest = findViewById(R.id.buttonLobbyCreateParty);
        buttonCreateQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getApplicationContext(), CreateQuestActivity.class));

            }
        });

        // On affiche la description de la party / quete au clic sur sa ligne.
        // Au clic sur une autre ligne ferme les descriptions ouvert avant.

        LobbyViewHolder.setOnClickListener(new LobbyViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final TextView textViewLobbyDescription = view.findViewById(R.id.textViewLobbyDescription);
                final Button buttonLobbyJoin = view.findViewById(R.id.buttonLobbyJoin);
                final TextView namePartyLobby = view.findViewById(R.id.lobbyName);
                final String quest_name = namePartyLobby.getText().toString();

                // On cherche si la quete qu'on cherche a joindre n'est pas dans les quetes faites
               final DatabaseReference refUser = FirebaseDatabase.getInstance().getReference();
                refUser.child("User").child(mUserId).child("quest_done").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       for (final DataSnapshot dsp : dataSnapshot.getChildren()) {
                           if (dsp.exists()){
                               refUser.child("Quest").child(dsp.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                       Quest quest = dataSnapshot.getValue(Quest.class);
                                       String questname = quest.getQuest_name();
                                       if (questname.equals(namePartyLobby.getText().toString())){
                                           buttonLobbyJoin.setVisibility(View.GONE);
                                           textViewLobbyDescription.setVisibility(View.GONE);
                                           textViewLobbyDescription.setText(R.string.impossible_lobby);
                                           Toast.makeText(getApplicationContext(), R.string.toast_error_party2, Toast.LENGTH_LONG).show();
                                       }
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



                if (quest_name.equals(mUser_CreatedQuestName)) {
                    buttonLobbyJoin.setVisibility(View.GONE);
                    textViewLobbyDescription.setText(R.string.impossible_lobby);
                    Toast.makeText(getApplicationContext(), R.string.toast_error_party, Toast.LENGTH_LONG).show();

                } else if (textViewLobbyDescription.getVisibility() == View.VISIBLE) {
                    textViewLobbyDescription.setVisibility(View.GONE);
                    buttonLobbyJoin.setVisibility(View.GONE);

                } else {
                    textViewLobbyDescription.setVisibility(View.VISIBLE);
                    buttonLobbyJoin.setVisibility(View.VISIBLE);

                    // Hide Other Description
                    for (int i = 0; i < mAdapter.getItemCount(); i++) {
                        if (i != position) {
                            LobbyViewHolder other = (LobbyViewHolder) recyclerViewLobby.findViewHolderForAdapterPosition(i);
                            if (other != null) {
                                other.mDescriptionPartyLobby.setVisibility(View.GONE);
                                other.mJoinPartyLobby.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
        });
    }

    // Methode utilisée pour afficher une ligne en dessous de chaque item du recycler view
    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(LobbyActivity context) {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        }
        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
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

    // METHODE POUR TROUVER USER
    private void searchUser() {

        // On recupere toutes les données de l'user actuel
        final DatabaseReference refUser =
                FirebaseDatabase.getInstance().getReference().child("User").child(mUserId);
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mUser_name = user.getUser_name();
                mUser_quest = user.getUser_quest();
                mUser_CreatedQuest = user.getUser_createdquestID();
                mUser_CreatedQuestName = user.getUser_createdquestName();
                searchQuest();
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
//                        mLife_duration = quest.getLife_duration();
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
                        mHint_challenge = challenge.getHint_challenge();
                        mDiff_challenge = challenge.getChallenge_difficulty();
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}