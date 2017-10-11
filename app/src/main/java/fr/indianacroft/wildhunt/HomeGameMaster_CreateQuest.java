package fr.indianacroft.wildhunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeGameMaster_CreateQuest extends Fragment {

    Button butAddNewChallenge;
    Button button_create_quest;
    EditText name_quest;
    EditText description_quest;
    Spinner spinner_quest;
    FirebaseDatabase ref;
    DatabaseReference childRef;
    private String mUserId;
    private String mUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.homegamemaster_createquest, container, false);

        // Pour recuperer la key d'un user (pour le lier a une quête)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
        mUserId = sharedPreferences.getString("mUserId", mUserId);
        Log.d("key", mUserId);
        /////////////////////////////////////////////////////////////////

        // Spinner
        spinner_quest = (Spinner) rootView.findViewById(R.id.spinner_challenge);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.life_duration, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_quest.setAdapter(adapter);

        // Go to Challenge Activity on click
        butAddNewChallenge = (Button) rootView.findViewById(R.id.butAddNewChallenge);
        butAddNewChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChallengesActivity.class);
                startActivity(intent);
            }
        });

        // Database
        name_quest = (EditText) rootView.findViewById(R.id.name_quest);
        description_quest = (EditText) rootView.findViewById(R.id.description_quest);
        button_create_quest = (Button) rootView.findViewById(R.id.button_create_quest);

        ref = FirebaseDatabase.getInstance();
        childRef = ref.getReference("Quest");

        button_create_quest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameContent = name_quest.getText().toString();
                String descriptionContent = description_quest.getText().toString();
                String spinnerContent = spinner_quest.getSelectedItem().toString();

                // childRef.push().getKey() is used to generate the different key
                final String questid = ref.getReference("Quest").push().getKey();
                //  DatabaseReference childRef = ref.getReference("form");
                // On recupere la quete crée par l'user actuel pour link challenge a la quête
                DatabaseReference refUser =
                        FirebaseDatabase.getInstance().getReference().child("User").child(mUserId);
                refUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                         mUserName = user.getUser_name();

                        childRef.child(questid).child("quest_creatorName").setValue(mUserName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                Quest quest = new Quest(nameContent, descriptionContent, spinnerContent);

                quest.setQuest_name(nameContent);
                quest.setQuest_description(descriptionContent);
                quest.setLife_duration(spinnerContent);

                childRef.child(questid).setValue(quest);
                childRef.child(questid).child("quest_creatorId").setValue(mUserId);


                name_quest.setText("");
                description_quest.setText("");

                //On lie la quête créee a un user
                ref.getReference("User").child(mUserId).child("user_createdquestID").setValue(questid);
                ref.getReference("User").child(mUserId).child("user_createdquestName").setValue(nameContent);

            }
        });

        return rootView;
    }
}
