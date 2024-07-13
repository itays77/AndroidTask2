package com.example.task1.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.task1.Model.GameScore;
import com.example.task1.R;
import com.google.android.material.color.utilities.Score;
import java.util.ArrayList;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private ArrayList<GameScore> scores;
    private ScoreClickListener listener;

    public ScoreAdapter(ArrayList<GameScore> scores, ScoreClickListener listener) {
        this.scores = scores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        GameScore score = scores.get(position);
        holder.bind(score);
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        TextView scoreTextView;

        ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.item_LBL_playerName);
            scoreTextView = itemView.findViewById(R.id.item_LBL_score);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    GameScore score = scores.get(position);
                    listener.onScoreClick(score.getLatitude(), score.getLongitude());
                }
            });
        }

        void bind(GameScore score) {
            if (timeTextView != null) {
                timeTextView.setText(score.getFormattedTime());
            }
            if (scoreTextView != null) {
                scoreTextView.setText(String.valueOf(score.getScore()));
            }
        }
    }

    public interface ScoreClickListener {
        void onScoreClick(double lat, double lon);
    }
}