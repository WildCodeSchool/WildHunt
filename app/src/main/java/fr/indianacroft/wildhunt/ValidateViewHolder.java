package fr.indianacroft.wildhunt;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by wilder on 16/10/17.
 */

public class ValidateViewHolder extends RecyclerView.ViewHolder{

    TextView challengeToValidate = (TextView) itemView.findViewById(R.id.challengeToValidate);
    TextView userNametoValidate = (TextView) itemView.findViewById(R.id.userToValidate);

    public ValidateViewHolder(View itemView) {
        super(itemView);}



    ///////// POUR RENTRER LES DONNEES DANS LE RECYCLER VIEW \\\\\\\\\\\\\\\
    //______________________________________________________\\

    // Pour mettre le nom de la party / quête dans le reclycer view
    public void setChallenge_name(String challenge_name) {
        challengeToValidate.setText(challenge_name);
    }
    // pour mettre la description de la party / quête dans le recycler view
    public void setUser_name(String user_name) {
        userNametoValidate.setText(user_name);
    }

}
