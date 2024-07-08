package com.example.task1.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.task1.Adapters.ScoreAdapter;
import com.example.task1.Interfaces.Callback_ListItemClicked;
import com.example.task1.Model.GameScore;
import java.util.ArrayList;
import java.util.Collections;
import com.example.task1.R;
import com.example.task1.Utilities.SharePreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;


public class ListFragment extends Fragment {

    private RecyclerView list_RCV_scores;
    private MaterialTextView list_LBL_title;
    private ScoreAdapter scoreAdapter;
    private Callback_ListItemClicked callbackListItemClicked;

    public ListFragment() {
        // Required empty public constructor
    }

    public void setCallbackListItemClicked(Callback_ListItemClicked callbackListItemClicked) {
        this.callbackListItemClicked = callbackListItemClicked;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        findViews(view);
        initViews();
        return view;
    }

    private void findViews(View view) {
        list_RCV_scores = view.findViewById(R.id.list_RCV_scores);
    }

    private void initViews() {
        ArrayList<GameScore> scores = getScores();

        // Sort scores in descending order
        Collections.sort(scores, (s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()));

        scoreAdapter = new ScoreAdapter(scores, new ScoreAdapter.ScoreClickListener() {
            @Override
            public void onScoreClick(double lat, double lon) {
                if (callbackListItemClicked != null) {
                    callbackListItemClicked.listItemClicked(lat, lon);
                }
            }
        });
        list_RCV_scores.setLayoutManager(new LinearLayoutManager(getContext()));
        list_RCV_scores.setAdapter(scoreAdapter);
    }

    private ArrayList<GameScore> getScores() {
        SharePreferencesManager spm = SharePreferencesManager.getInstance();
        return spm.getScores();
    }
}