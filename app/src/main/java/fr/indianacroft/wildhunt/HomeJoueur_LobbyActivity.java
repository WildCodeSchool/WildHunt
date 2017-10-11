package fr.indianacroft.wildhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by pierre on 9/26/17.
 */

public class HomeJoueur_LobbyActivity extends Fragment {
    private static final String TAG = HomeJoueur_LobbyActivity.class.getSimpleName();
    private FirebaseDatabase ref;
    private DatabaseReference childRef;
    private String mUserId;


    // Methode utilisée pour afficher une ligne en dessous de chaque item du recycler view
    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(HomeJoueur_LobbyActivity context) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homejoueur_lobbyactivity, container, false);

        // Pour recuperer la key d'un user (pour le lier a une quête)
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUserId = preferences.getString("mUserId", "");
        // On recupere la qûete dans laquelle il est
        // je recupere la KEY de la quête choisi grâce a son nom
        DatabaseReference refUserQuest =
                FirebaseDatabase.getInstance().getReference().child("User").child(mUserId).child("user_quest");
        refUserQuest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String questKey = child.getKey(); // ID de la quête
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        // Pour remplir la liste des quêtes avec les quêtes créees!!!
        final RecyclerView recyclerViewLobby = (RecyclerView) view.findViewById(R.id.recyclerViewHomeJoueurLobby);
        recyclerViewLobby.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Quest");


        final FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<Quest, HomeJoueur_LobbyHolder>(
                Quest.class,
                R.layout.homejoueur_lobby,
                HomeJoueur_LobbyHolder.class,
                ref) {
            @Override
            public void populateViewHolder(HomeJoueur_LobbyHolder holder, Quest bdd, int position) {
                holder.setQuest_name(bdd.getQuest_name());
                holder.setQuest_description(bdd.getQuest_description());
            }
        };

        // Set the adapter avec les données et la ligne de separation
        recyclerViewLobby.addItemDecoration(new SimpleDividerItemDecoration(this));
        recyclerViewLobby.setAdapter(mAdapter);

        // Bouton pour créer sa party
        // TODO coder l'intent pour envoyver vers HomeGameMaster_CreateQuest uniquement
        Button buttonCreateQuest = (Button) view.findViewById(R.id.buttonLobbyCreateParty);
        buttonCreateQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToFragment = new Intent(getActivity(), HomeGameMasterActivity.class);
                intentToFragment.putExtra("menuFragment", "createQuest");
                startActivity(intentToFragment);
            }
        });










        // On affiche la description de la party / quete au clic sur sa ligne.
        // Au clic sur une autre ligne ferme les descriptions ouvert avant.
        HomeJoueur_LobbyHolder.setOnClickListener(new HomeJoueur_LobbyHolder.ClickListener() {
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
                            HomeJoueur_LobbyHolder other = (HomeJoueur_LobbyHolder) recyclerViewLobby.findViewHolderForAdapterPosition(i);
                            other.mDescriptionPartyLobby.setVisibility(View.GONE);
                            other.mJoinPartyLobby.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        return view;
    }
}
