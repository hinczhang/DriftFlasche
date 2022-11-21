package org.standardserve.driftflasche.dialog;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import org.standardserve.driftflasche.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final JSONArray comments;

    CommentAdapter(JSONArray out_comments){
        comments = out_comments;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.commet_table, null);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            String username = comments.getJSONObject(position).getString("username");
            String content = comments.getJSONObject(position).getString("content");
            holder.content.setText(username + ": " + content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.length();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView content;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.commet_content);
        }
    }
}
