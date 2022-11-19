package org.standardserve.driftflasche.dialog;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.standardserve.driftflasche.R;

public class BottleAdapter extends RecyclerView.Adapter<BottleAdapter.ViewHolder> {
    private final JSONArray bottles;

    BottleAdapter(JSONArray out_bottles){

        bottles = out_bottles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.bottles_table, null);
        return new BottleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.seqNum.setText(String.valueOf(position+1));
        try {
            holder.abstractContent.setText(bottles.getJSONObject(position).getString("content"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return bottles.length();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView seqNum;
        private final TextView abstractContent;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            seqNum = itemView.findViewById(R.id.seqNum);
            abstractContent = itemView.findViewById(R.id.abstract_content);
        }
    }
}
