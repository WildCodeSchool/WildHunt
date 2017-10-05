package fr.indianacroft.wildhunt;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class HomeGameMaster_CreateQuest extends Fragment {

    Button butNewChallenge;
    Button butNewChallenge2;
    Button butAddNewChallenge;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.homegamemaster_createquest, container, false);

        // Spinner
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner_challenge);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.life_duration, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Go to Challenge Activity on click
        butNewChallenge = (Button) rootView.findViewById(R.id.butNewChallenge);
        butNewChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChallengesActivity.class);
                startActivity(intent);
            }
        });
        butNewChallenge2 = (Button) rootView.findViewById(R.id.butNewChallenge2);
        butNewChallenge2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChallengesActivity.class);
                startActivity(intent);
            }
        });
        butAddNewChallenge = (Button) rootView.findViewById(R.id.butAddNewChallenge);
        butAddNewChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChallengesActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
