package com.doanjava.messbcode.Adapters;

import static com.doanjava.messbcode.GuiThongBao.GuiThongBao;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Activities.KichHoatOTP;
import com.doanjava.messbcode.Activities.KichHoatSoDienThoai;
import com.doanjava.messbcode.Activities.NhanTin;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.TinNhan;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.DeleteDialogBinding;
import com.doanjava.messbcode.databinding.ItemReceiveBinding;
import com.doanjava.messbcode.databinding.ItemSentBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ChuyenDoiTinNhan extends RecyclerView.Adapter {

    Context context;
    ArrayList<TinNhan> tinNhans;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;


    NguoiDung NguoiNhan;
    NguoiDung NguoiGui;
    String ten,token;
    public ChuyenDoiTinNhan(Context context, ArrayList<TinNhan> tinNhans,NguoiDung NguoiGui, NguoiDung NguoiNhan,String ten,String token) {
        this.context = context;
        this.tinNhans = tinNhans;
        this.NguoiGui = NguoiGui;
        this.NguoiNhan = NguoiNhan;
        this.ten = ten;
        this.token = token;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false );
            return new CheDoGuiTinNhan(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false );
            return new CheDoNhanTinNhan(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        TinNhan tinNhan = tinNhans.get(position);
        if(NguoiGui.getMaSo().equals(tinNhan.getMaNguoiGui())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        TinNhan tinNhan = tinNhans.get(position);

        int reactions[] = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(pos != -1) {
            if(holder.getClass() == CheDoGuiTinNhan.class) {
                CheDoGuiTinNhan cheDoTinNhan = (CheDoGuiTinNhan)holder;
                cheDoTinNhan.binding.feeling.setImageResource(reactions[pos]);
                cheDoTinNhan.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                CheDoNhanTinNhan cheDoTinNhan = (CheDoNhanTinNhan)holder;
                cheDoTinNhan.binding.feeling.setImageResource(reactions[pos]);
                cheDoTinNhan.binding.feeling.setVisibility(View.VISIBLE);


            }
                tinNhan.setCamXuc(pos);
                FirebaseDatabase.getInstance().getReference()
                        .child("TroChuyen")
                        .child(NguoiGui.getMaSo())
                        .child(NguoiNhan.getMaSo())
                        .child("tinNhan")
                        .child(tinNhan.getMaTin()).setValue(tinNhan);

                FirebaseDatabase.getInstance().getReference()
                        .child("TroChuyen")
                        .child(NguoiNhan.getMaSo())
                        .child(NguoiGui.getMaSo())
                        .child("tinNhan")
                        .child(tinNhan.getMaTin()).setValue(tinNhan);
            }

            return true;
        });

        if(holder.getClass() == CheDoGuiTinNhan.class) {
            CheDoGuiTinNhan cheDoTinNhan = (CheDoGuiTinNhan)holder;
            cheDoTinNhan.binding.image.setVisibility(View.GONE);
            cheDoTinNhan.binding.anhgui.setVisibility(View.GONE);
            /*Glide.with(context)
                    .load(NguoiGui.getAnhDaiDien())
                    .placeholder(R.drawable.placeholder)
                    .into(cheDoTinNhan.binding.anhgui);*/

            if(tinNhan.getTinNhan().equals("HinhAnh")) {
                cheDoTinNhan.binding.image.setVisibility(View.VISIBLE);
                cheDoTinNhan.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(tinNhan.getLinkAnhGui())
                        .placeholder(R.drawable.placeholder)
                        .into(cheDoTinNhan.binding.image);
            }

            cheDoTinNhan.binding.message.setText(tinNhan.getTinNhan());

            if(tinNhan.getCamXuc() >= 0) {
                cheDoTinNhan.binding.feeling.setImageResource(reactions[tinNhan.getCamXuc()]);
                cheDoTinNhan.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                cheDoTinNhan.binding.feeling.setVisibility(View.GONE);
            }

            cheDoTinNhan.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });

            cheDoTinNhan.binding.image.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });

            cheDoTinNhan.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View view = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null);
                    DeleteDialogBinding binding = DeleteDialogBinding.bind(view);
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Thu Hồi Tin Nhắn")
                            .setView(binding.getRoot())
                            .create();

                    binding.everyone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tinNhan.setTinNhan("Tin nhắn này đã bị xóa.");
                            tinNhan.setCamXuc(-1);
                            if(tinNhan.getMaTin().equals(tinNhans.get(tinNhans.size()-1).getMaTin()))
                            {
                                HashMap<String, Object> tinCuoiObj = new HashMap<>();
                                tinCuoiObj.put("tinCuoi",tinNhan.getTinNhan());
                                tinCuoiObj.put("thoiGianTinCuoi",tinNhan.getThoiGianTin());
                                FirebaseDatabase.getInstance().getReference().child("TroChuyen").child(NguoiGui.getMaSo()).child(NguoiNhan.getMaSo()).updateChildren(tinCuoiObj);
                                FirebaseDatabase.getInstance().getReference().child("TroChuyen").child(NguoiNhan.getMaSo()).child(NguoiGui.getMaSo()).updateChildren(tinCuoiObj);
                            }
                            FirebaseDatabase.getInstance().getReference()
                                    .child("TroChuyen")
                                    .child(NguoiGui.getMaSo())
                                    .child(NguoiNhan.getMaSo())
                                    .child("tinNhan")
                                    .child(tinNhan.getMaTin()).setValue(tinNhan);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("TroChuyen")
                                    .child(NguoiNhan.getMaSo())
                                    .child(NguoiGui.getMaSo())
                                    .child("tinNhan")
                                    .child(tinNhan.getMaTin()).setValue(tinNhan).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    GuiThongBao(ten,ten + " đã thu hồi 1 tin nhắn",token,context);
                                }
                            });
                            dialog.dismiss();
                        }
                    });

                    binding.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("TroChuyen")
                                    .child(NguoiGui.getMaSo())
                                    .child(NguoiNhan.getMaSo())
                                    .child("tinNhan")
                                    .child(tinNhan.getMaTin()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    GuiThongBao(ten,ten + " đã xoá 1 tin nhắn",token,context);
                                }
                            });

                            dialog.dismiss();
                        }
                    });

                    binding.cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    return false;
                }
            });
        } else {
            CheDoNhanTinNhan cheDoTinNhan = (CheDoNhanTinNhan)holder;
            cheDoTinNhan.binding.image.setVisibility(View.GONE);

            Glide.with(context)
                    .load(NguoiNhan.getAnhDaiDien())
                    .placeholder(R.drawable.avatar)
                    .into(cheDoTinNhan.binding.anhnhan);

            if(tinNhan.getTinNhan().equals("HinhAnh")) {
                cheDoTinNhan.binding.image.setVisibility(View.VISIBLE);
                cheDoTinNhan.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(tinNhan.getLinkAnhGui())
                        .placeholder(R.drawable.avatar)
                        .into(cheDoTinNhan.binding.image);
            }

            cheDoTinNhan.binding.message.setText(tinNhan.getTinNhan());

            if(tinNhan.getCamXuc() >= 0) {
                cheDoTinNhan.binding.feeling.setImageResource(reactions[tinNhan.getCamXuc()]);
                cheDoTinNhan.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                cheDoTinNhan.binding.feeling.setVisibility(View.GONE);
            }

            cheDoTinNhan.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });

            cheDoTinNhan.binding.image.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });

            cheDoTinNhan.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View view = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null);
                    DeleteDialogBinding binding = DeleteDialogBinding.bind(view);
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Thu Hồi Tin Nhắn")
                            .setView(binding.getRoot())
                            .create();

                    binding.everyone.setVisibility(View.GONE);
                   // binding.everyone.setOnClickListener(new View.OnClickListener() {
                   //     @Override
                   //     public void onClick(View v) {
                   //         tinNhan.setTinNhan("Tin nhắn này đã bị xóa.");
                   //         tinNhan.setCamXuc(-1);
                   //         FirebaseDatabase.getInstance().getReference()
                   //                 .child("TroChuyen")
                   //                 .child(noiNguoiGui)
                   //                 .child("tinNhan")
                   //                 .child(tinNhan.getMaTin()).setValue(tinNhan);
//
                   //         FirebaseDatabase.getInstance().getReference()
                   //                 .child("TroChuyen")
                   //                 .child(noiNguoiNhan)
                   //                 .child("tinNhan")
                   //                 .child(tinNhan.getMaTin()).setValue(tinNhan);
                   //         dialog.dismiss();
                   //     }
                   // });

                    binding.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference()
                                    .child("TroChuyen")
                                    .child(NguoiGui.getMaSo())
                                    .child(NguoiNhan.getMaSo())
                                    .child("tinNhan")
                                    .child(tinNhan.getMaTin()).setValue(null);
                            dialog.dismiss();
                        }
                    });

                    binding.cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return tinNhans.size();
    }

    public class CheDoGuiTinNhan extends RecyclerView.ViewHolder {


        ItemSentBinding binding;

        public CheDoGuiTinNhan(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);
        }

    }

    public class CheDoNhanTinNhan extends RecyclerView.ViewHolder {

        ItemReceiveBinding binding;
        public CheDoNhanTinNhan(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }

}
