package fr.indianacroft.wildhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class HomeJoueur_PlayerActivity extends Fragment {

    Button butNewChallenge;
    Button butNewChallenge2;
    ImageView imageViewCancel;
    ImageView imageViewCancel2;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.homejoueur_playeractivity, container, false);

        final Button buttonHint = (Button) rootView.findViewById(R.id.buttonHomeJoueurHint);
        final TextView textViewPlayerActivityHint = (TextView) rootView.findViewById(R.id.textViewPlayerActivityHint);


        // Pour recuperer la key d'un user (pour le lier a une quête)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
        mUserId = sharedPreferences.getString("mUserId", mUserId);
        Log.d("key", mUserId);
        /////////////////////////////////////////////////////////////////

        // On appele les methodes declarées plus bas (pour chercher l'user, la quete, les challenges)
        searchUser(rootView);

        Button buttonSendSolution = (Button) rootView.findViewById(R.id.buttonHomeJoueurSendSolution);

        buttonSendSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mUser_quest.equals("Pas de qûete pour l'instant")) {
                    Intent intent = new Intent(getActivity(), HomeJoueur_PlayerPopUp.class);
                    intent.putExtra("mChallengeKey", mKey_challenge); //On envoie l'ID du challenge
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getContext(), R.string.error_noquest, Toast.LENGTH_SHORT).show();
                }
            }
        });


        return rootView;
    }



    // METHODE POUR TROUVER USER
    private void searchUser(final View rootView) {
        final Button buttonHint = (Button) rootView.findViewById(R.id.buttonHomeJoueurHint);
        final TextView textViewPlayerActivityHint = (TextView) rootView.findViewById(R.id.textViewPlayerActivityHint);

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
                    buttonHint.setBackgroundColor(Color.RED);
                } else {
                    textViewPlayerActivityHint.setVisibility(View.GONE);
                }
                searchQuest(rootView);

                // Indice au clic
                // TODO enlever les points au clic de l'indice
                buttonHint.setOnClickListener(new View.OnClickListener() {
                    boolean isClicked = false;

                    @Override
                    public void onClick(View view) {
                        //si l'indice est déclaré false dans la bdd cest qu'il n'a jamais été utilisé
                        if (mUser_indice.equalsIgnoreCase("false")) {
                            if (!isClicked) {
                                isClicked = true;
                                Toast.makeText(getContext(), R.string.warning_hint, Toast.LENGTH_SHORT).show();
                            } else if (isClicked) {
                                textViewPlayerActivityHint.setVisibility(View.VISIBLE);
                                buttonHint.setBackgroundColor(Color.RED);
                                refUser.child("user_indice").setValue("true");
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


    // METHODE POUR TROUVER QUETE
    private void searchQuest(final View rootView) {
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

                        final TextView playerActivityQuestName = (TextView) rootView.findViewById(R.id.playerActivityNameQuestTitle);
                        playerActivityQuestName.setText(mQuest_name);
                        searcChallenges(rootView);
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
    private void searcChallenges(final View rootView) {
        final TextView textViewPlayerActivityHint = (TextView) rootView.findViewById(R.id.textViewPlayerActivityHint);

        final Button playerActivityNumChallenge = (Button) rootView.findViewById(R.id.playerActivityNumChallenge);

        // On recupere les données des challenges
        DatabaseReference refUserChallenge = FirebaseDatabase.getInstance().getReference().child("Challenge");
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


                        // On change la page dynamiquement !!


                        // Reference to an image file in Firebase Storage
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Quest").child(mUser_quest).child(mKey_challenge);
                        // ImageView in your Activity
                        final ImageView imageViewPhotoChallenge = (ImageView) rootView.findViewById(R.id.imageViewPlayerActivityPhoto);
                        // Load the image using Glide
                            Glide.with(getContext())
                                    .using(new FirebaseImageLoader())
                                    .load(storageReference)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(imageViewPhotoChallenge);



                        textViewPlayerActivityHint.setText(mHint_challenge);
                        playerActivityNumChallenge.setText(mName_challenge);
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
