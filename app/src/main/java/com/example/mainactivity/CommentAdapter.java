package com.example.mainactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    ArrayList<CommentModel> items;
    Context context;
    RecyclerView recyclerView;

    public CommentAdapter(ArrayList<CommentModel> items, Context context, RecyclerView recyclerView) {
        this.items = items;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void addItem(CommentModel item) {
        items.add(item);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_recycler_comment, viewGroup, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int i) {
        final CommentModel model = items.get(i);
        holder.item_user.setText(model.getUserName());
        holder.item_comment.setText(model.getContent());
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView item_user;
        TextView item_comment;

        public CommentViewHolder(@NonNull final View itemView) {
            super(itemView);
            item_user = itemView.findViewById(R.id.item_user);
            item_comment = itemView.findViewById(R.id.item_comment);
        }
    }
}