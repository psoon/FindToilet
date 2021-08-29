package com.example.mainactivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    ArrayList<CommentModel> items;
    Context context;
    RecyclerView recyclerView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy.MM.dd HH:mm");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
        holder.item_time.setText(getDateTime(i));
        if(MainActivity.getUser()!=null){
            if(model.uid.equals(MainActivity.getUser().getUid())){
                holder.item_btn_delete.setVisibility(View.VISIBLE);
                holder.item_btn_modify.setVisibility(View.VISIBLE);
            }
        }
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView item_user;
        TextView item_comment;
        TextView item_time;
        Button item_btn_report, item_btn_modify, item_btn_delete;
        private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        private DatabaseReference databaseReference = mDatabase.getReference();
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();
        public CommentViewHolder(@NonNull final View itemView) {
            super(itemView);
            item_user = itemView.findViewById(R.id.item_user);
            item_comment = itemView.findViewById(R.id.item_comment);
            item_time = itemView.findViewById(R.id.item_time);
            item_btn_modify = itemView.findViewById(R.id.btn_modify);
            item_btn_report = itemView.findViewById(R.id.btn_report);
            item_btn_delete = itemView.findViewById(R.id.btn_delete);

            item_btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("삭제하시겠습니까?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int position = getAdapterPosition();
                            CommentModel model = items.get(position);
                            databaseReference.child("Toilet_Comment").child(MainActivity.dataArr[Integer.parseInt(model.toiletNum)][1]).child(model.content).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(view.getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.show();
                }
            });
            item_btn_modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final LinearLayout linearLayout = (LinearLayout)View.inflate(view.getContext(), R.layout.comment_modify, null);
                    new AlertDialog.Builder(view.getContext())
                            .setView(linearLayout)
                            .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    EditText et_modify = linearLayout.findViewById(R.id.et_modify);
                                    int position = getAdapterPosition();
                                    CommentModel model = items.get(position);
                                    try{
                                        String str = et_modify.getText().toString();
                                        if(str.length()<=0) {
                                            Toast.makeText(view.getContext(), "빈 내용입니다.", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        items.remove(position);
                                        databaseReference.child("Toilet_Comment").child(MainActivity.dataArr[Integer.parseInt(model.toiletNum)][1]).child(model.content).setValue(null);

                                        model.createAt = ServerValue.TIMESTAMP;
                                        model.content = str;
                                        databaseReference.child("Toilet_Comment").child(MainActivity.dataArr[Integer.parseInt(model.toiletNum)][1]).child(model.content).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(view.getContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show();
                                                try{
                                                    Thread.sleep(500);
                                                }catch(Exception e){ }
                                                notifyDataSetChanged();
                                            }
                                        });
                                    }catch(Exception e){
                                        Toast.makeText(view.getContext(), "공백 혹은 잘못된 입력입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) { }
                            })
                            .show();
                }
            });
            item_btn_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    CommentModel reportModel = items.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("신고하시겠습니까?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final LinearLayout linearLayout = (LinearLayout)View.inflate(view.getContext(), R.layout.comment_report, null);
                            new AlertDialog.Builder(view.getContext())
                                    .setView(linearLayout)
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            EditText et_report = linearLayout.findViewById(R.id.et_report);
                                            CommentModel model = new CommentModel();
                                            try{
                                                model.content = et_report.getText().toString();
                                            }catch(Exception e){
                                                Toast.makeText(view.getContext(), "공백 혹은 잘못된 입력입니다.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            model.createAt = ServerValue.TIMESTAMP;

                                            if(mAuth.getCurrentUser()!= null){
                                                model.uid = mAuth.getCurrentUser().getUid();
                                                databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("nickName").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                        model.userName = task.getResult().getValue(String.class);
                                                        model.toiletNum = reportModel.getToiletNum();
                                                        databaseReference.child("Report").child(MainActivity.dataArr[Integer.parseInt(model.toiletNum)][1]).child(model.content).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(view.getContext(), "신고되었습니다.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                            else{
                                                model.toiletNum = reportModel.getToiletNum();
                                                databaseReference.child("Report").child(MainActivity.dataArr[Integer.parseInt(model.toiletNum)][1]).child(model.content).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(view.getContext(), "신고되었습니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                        }
                                    })
                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) { }
                                    })
                                    .show();
                            //신고할 경우 구현
                        }
                    }).show();
                }
            });
        }
    }
    public String getDateTime(int position){
        long unixTime = (long) items.get(position).createAt;
        Date date = new Date(unixTime);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String time = simpleDateFormat.format(date);
        return time;
    }
}