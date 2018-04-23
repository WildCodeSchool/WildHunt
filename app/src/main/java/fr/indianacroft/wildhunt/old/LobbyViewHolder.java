package fr.indianacroft.wildhunt.old;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import fr.indianacroft.wildhunt.R;

/**
 * Created by wilder on 27/09/17.
 */

public class LobbyViewHolder extends RecyclerView.ViewHolder {

    public TextView mNamePartyLobby, mDescriptionPartyLobby, mNbChallengeLobby;
    public Button mDiscoverPartyLobby, mJoinPartyLobby;
    private String mUserId;

    public LobbyViewHolder(final View itemView) {
        super(itemView);

        // Pour recuperer la key d'un user (pour le lier a une quête)
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
        mUserId = preferences.getString("mUserId", "");

        mNamePartyLobby = itemView.findViewById(R.id.lobbyName);

        mDescriptionPartyLobby = itemView.findViewById(R.id.textViewLobbyDescription);
        mDescriptionPartyLobby.setVisibility(View.GONE);

        mJoinPartyLobby = itemView.findViewById(R.id.buttonLobbyJoin);
        mJoinPartyLobby.setVisibility(View.GONE);

        mNbChallengeLobby = itemView.findViewById(R.id.textViewNbreChallenge);
        mNbChallengeLobby.setVisibility(View.GONE);

        // Discover quest details
        mDiscoverPartyLobby = itemView.findViewById(R.id.buttonLobbyDetails);
        mDiscoverPartyLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                itemView.callOnClick();
                final String quest_name = mNamePartyLobby.getText().toString();


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
                                        // La quete a déja été faite
                                        if (questname.equals(mNamePartyLobby.getText().toString())){
                                            mJoinPartyLobby.setVisibility(View.GONE);
                                            mDescriptionPartyLobby.setVisibility(View.GONE);
                                            mNbChallengeLobby.setVisibility(View.GONE);
                                            //Toast.makeText(itemView.getContext(), R.string.toast_error_party2, Toast.LENGTH_LONG).show();
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

                // Avoid user to join his own quest
                // Get name of created quest
                DatabaseReference data1 = FirebaseDatabase.getInstance().getReference();
                DatabaseReference data2 = data1.child("User").child(mUserId).child("user_createdquestName");
                // Compare quest created to quest_name
                data2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userCreatedQuestName = dataSnapshot.getValue(String.class);
                        if (quest_name.equals(userCreatedQuestName)) {
                            mJoinPartyLobby.setVisibility(View.GONE);
                            mNbChallengeLobby.setVisibility(View.GONE);
                            mDescriptionPartyLobby.setText(R.string.impossible_lobby);
                            //Toast.makeText(itemView.getContext(), R.string.toast_error_party, Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                    // Pour donner le nombre de challenges contenu dans une partie et l'afficher
                    refUser.child("Quest").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                Quest quest = dsp.getValue(Quest.class);

                                if (quest.getQuest_name().equals(quest_name)) {
                                    String questKey = dsp.getKey();
                                    refUser.child("Challenge").child(questKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshotChall) {

                                            int nbreChallenge = (int) dataSnapshotChall.getChildrenCount();
                                            mNbChallengeLobby.setText(itemView.getResources().getString(R.string.nbreChallenge, (nbreChallenge)));


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
        });

        // Join quest
        mJoinPartyLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
            // je recupere le nom de la quete selectionné
            final String quest_name = mNamePartyLobby.getText().toString();

            // Get name of created quest
            DatabaseReference data1 = FirebaseDatabase.getInstance().getReference();
            DatabaseReference data2 = data1.child("User").child(mUserId).child("user_createdquestName");
            // Compare quest created to quest_name
            data2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userCreatedQuestName = dataSnapshot.getValue(String.class);

//                    if (quest_name.equals(userCreatedQuestName)) {
//                        mJoinPartyLobby.setEnabled(false);
////                        Toast.makeText(getApplicationContext(), "errrorrrrr", Toast.LENGTH_LONG).show();
//                    } else {
                        // je recupere la KEY de la quête choisi grâce a son nom
                        DatabaseReference refQuestName = FirebaseDatabase.getInstance().getReference().child("Quest");
                        refQuestName.orderByChild("quest_name").equalTo(quest_name).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    final String questKey = child.getKey(); // ID de la quête

                                    // Recup challenge id ds dossier challenge grace a a la questKey
                                    DatabaseReference refChallengeId = FirebaseDatabase.getInstance().getReference().child("Challenge").child(questKey);
                                    refChallengeId.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                String challengeKey = child.getKey(); // ID du challenge

                                                // On assigne l'ID du challenge au joueur
                                                DatabaseReference refChallengeUser =
                                                        FirebaseDatabase.getInstance().getReference().child("User").child(mUserId).child("user_challenge");
                                                refChallengeUser.setValue(challengeKey);
                                                // On assigne l'ID de la qûete à l'utilisateur
                                                DatabaseReference refUserQuest =
                                                        FirebaseDatabase.getInstance().getReference().child("User").child(mUserId).child("user_quest");
                                                refUserQuest.setValue(questKey);
                                                FirebaseDatabase.getInstance().getReference("User").child(mUserId).child("user_indice").setValue("false");

                                                Intent intent = new Intent(view.getContext(), PlayerActivity.class);
                                                view.getContext().startActivity(intent);
                                                return;
                                            }
                                            Toast.makeText(view.getContext(), R.string.error_nochallenge, Toast.LENGTH_SHORT).show();

                                        }
                                        @Override
                                        public void onCancelled (DatabaseError databaseError){
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
        });

        //listener set on ENTIRE ROW, you may set on individual components within a row.
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }

    private static LobbyViewHolder.ClickListener mClickListener;

    //Interface to send callbacks...
    public interface ClickListener{
        void onItemClick(View view, int position);
    }

    public static void setOnClickListener(LobbyViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }



///////// POUR RENTRER LES DONNEES DANS LE RECYCLER VIEW \\\\\\\\\\\\\\\
    //______________________________________________________\\

    // Pour mettre le nom de la party / quête dans le reclycer view
    public void setQuest_name(String quest_name) {
        mNamePartyLobby.setText(quest_name);
    }
    // pour mettre la description de la party / quête dans le recycler view
    public void setQuest_description(String quest_description) {
        mDescriptionPartyLobby.setText(quest_description);
    }
}