package fr.indianacroft.wildhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

        // Avatar
        imageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

//        // POUR CHANGER L'AVATAR SUR LA PAGE AVEC CELUI CHOISI
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

        // Pour remplir la liste des quêtes avec les quêtes créees!!!
        final RecyclerView recyclerViewLobby = (RecyclerView) findViewById(R.id.recyclerViewHomeJoueurLobby);
        recyclerViewLobby.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Quest");

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
        Button buttonCreateQuest = (Button) findViewById(R.id.buttonLobbyCreateParty);
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
                TextView textViewLobbyDescription = (TextView) view.findViewById(R.id.textViewLobbyDescription);
                Button buttonLobbyJoin = (Button) view.findViewById(R.id.buttonLobbyJoin);

                if (textViewLobbyDescription.getVisibility() == View.VISIBLE) {
                    textViewLobbyDescription.setVisibility(View.GONE);
                    buttonLobbyJoin.setVisibility(View.GONE);
                }else {
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
        } else if (id == R.id.nav_delete) {
            startActivity(new Intent(getApplicationContext(), ConnexionActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                        mLife_duration = quest.getLife_duration();
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
                        Log.d(mName_challenge, "tag");
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