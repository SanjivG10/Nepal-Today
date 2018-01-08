package com.techniary.nepaltoday;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditorChoiceFragment extends Fragment {

    private FloatingActionButton mFloatingActionButton;
    private CardView moviesCardView;
    private CardView newsCardView;

    public EditorChoiceFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_editor_choice,container,false);
        mFloatingActionButton = (FloatingActionButton) v.findViewById(R.id.fabForPost);
        moviesCardView = (CardView)v.findViewById(R.id.movies_card_view);
        newsCardView = (CardView)v.findViewById(R.id.BBC_news_card_view);

        moviesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MoviesActivity.class);
                startActivity(intent);

            }
        });

        newsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),NewsActivity.class);
                startActivity(intent);
            }
        });

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),UsersPostActivity.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return v;
    }



}
