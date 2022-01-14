package com.doanjava.messbcode.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Activities.NhanTinPhongChat;
import com.doanjava.messbcode.GuiThongBao;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.PhongChat;
import com.doanjava.messbcode.Models.TinNhan;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.DeleteDialogBinding;
import com.doanjava.messbcode.databinding.ItemReceiceGroupBinding;
import com.doanjava.messbcode.databinding.ItemReceiveBinding;
import com.doanjava.messbcode.databinding.ItemSentBinding;
import com.doanjava.messbcode.databinding.ItemSentGroupBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChuyenDoiTinNhanPhong extends RecyclerView.Adapter {

    Context context;
    ArrayList<TinNhan> tinNhans;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;
    String maSoNguoiGui;
    ArrayList<NguoiDung> nguoiDungs;
    PhongChat phongChat;

    public void setPhongChat(PhongChat phongChat) {
        this.phongChat = phongChat;
    }
    String tennggui ;
    String maPhong ;
    public void setNguoiDungs(ArrayList<NguoiDung> nguoiDungs) {
        this.nguoiDungs = nguoiDungs;
    }


    public ChuyenDoiTinNhanPhong(Context context, String maSoNguoiGui,PhongChat phongChat, ArrayList<TinNhan> tinNhans) {
        this.context = context;
        this.tinNhans = tinNhans;
        this.maSoNguoiGui = maSoNguoiGui;
        this.phongChat = phongChat;
        this.maPhong = phongChat.getMaPhong();
    }

    public ChuyenDoiTinNhanPhong(){}



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent_group, parent, false );
            return new CheDoGuiTinNhan(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_receice_group, parent, false );
            return new CheDoNhanTinNhan(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        TinNhan tinNhan = tinNhans.get(position);
        if(maSoNguoiGui.equals(tinNhan.getMaNguoiGui())) {
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
                if (holder.getClass() == CheDoGuiTinNhan.class) {
                    CheDoGuiTinNhan cheDoTinNhan = (CheDoGuiTinNhan) holder;
                    cheDoTinNhan.binding.feeling.setImageResource(reactions[pos]);
                    cheDoTinNhan.binding.feeling.setVisibility(View.VISIBLE);
                } else {
                    CheDoNhanTinNhan cheDoTinNhan = (CheDoNhanTinNhan) holder;
                    cheDoTinNhan.binding.feeling.setImageResource(reactions[pos]);
                    cheDoTinNhan.binding.feeling.setVisibility(View.VISIBLE);


                }
                tinNhan.setCamXuc(pos);
                FirebaseDatabase.getInstance().getReference()
                        .child("PhongChat")
                        .child(maPhong)
                        .child("tinNhan").child(tinNhan.getMaTin()).setValue(tinNhan);

            }
            return true;
        });

        if(holder.getClass() == CheDoGuiTinNhan.class) {
            CheDoGuiTinNhan cheDoTinNhan = (CheDoGuiTinNhan)holder;
            cheDoTinNhan.binding.image.setVisibility(View.GONE);
            cheDoTinNhan.binding.anhgrgui.setVisibility(View.GONE);
            /*for(NguoiDung ng : nguoiDungs)
                if(ng.getMaSo().equals(tinNhan.getMaNguoiGui()))
                {
                    Glide.with(context)
                            .load(ng.getAnhDaiDien())
                            .placeholder(R.drawable.avatar)
                            .into(cheDoTinNhan.binding.anhgrgui);
                }*/


            if(tinNhan.getTinNhan().equals("HinhAnh")) {
                cheDoTinNhan.binding.image.setVisibility(View.VISIBLE);
                cheDoTinNhan.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(tinNhan.getLinkAnhGui())
                        .placeholder(R.drawable.placeholder)
                        .into(cheDoTinNhan.binding.image);
            }

            FirebaseDatabase.getInstance()
                    .getReference().child("NguoiDung")
                    .child(tinNhan.getMaNguoiGui())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                NguoiDung nguoiDung = snapshot.getValue(NguoiDung.class);
                                cheDoTinNhan.binding.name.setText("@" + nguoiDung.getTen());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

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
                            FirebaseDatabase.getInstance().getReference()
                                    .child("PhongChat")
                                    .child(maPhong)
                                    .child("tinNhan").child(tinNhan.getMaTin()).setValue(tinNhan).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    for(NguoiDung ng : nguoiDungs)
                                        if(ng.getMaSo().equals(maSoNguoiGui))
                                            tennggui = ng.getTen();

                                    for(NguoiDung ng : nguoiDungs)
                                    {
                                        if(!ng.getMaSo().equals(maSoNguoiGui))
                                        {
                                            if(phongChat.getQuanTri() != null)
                                                if(phongChat.getQuanTri().contains(ng.getMaSo()))
                                                    GuiThongBao.GuiThongBao(tennggui,"Đã Thu Hồi Tin Nhắn",ng.getToken(), context);

                                            if(phongChat.getThanhVien() != null)
                                                if(phongChat.getThanhVien().contains(ng.getMaSo()))
                                                    GuiThongBao.GuiThongBao(tennggui," Đã Thu Hồi Tin Nhắn",ng.getToken(),context);
                                        }
                                    }
                                }
                            });

                            dialog.dismiss();
                        }
                    });

                          binding.delete.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  FirebaseDatabase.getInstance().getReference()
                                          .child("Nhom")
                                          .child(tinNhan.getMaTin()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void unused) {
                                          for(NguoiDung ng : nguoiDungs)
                                              if(ng.getMaSo().equals(maSoNguoiGui))
                                                  tennggui = ng.getTen();

                                          for(NguoiDung ng : nguoiDungs)
                                          {
                                              if(!ng.getMaSo().equals(maSoNguoiGui))
                                              {
                                                  if(phongChat.getQuanTri() != null)
                                                      if(phongChat.getQuanTri().contains(ng.getMaSo()))
                                                          GuiThongBao.GuiThongBao(tennggui,"Đã Xoá Tin Nhắn",ng.getToken(), context);

                                                  if(phongChat.getThanhVien() != null)
                                                      if(phongChat.getThanhVien().contains(ng.getMaSo()))
                                                          GuiThongBao.GuiThongBao(tennggui," Đã Xoá Tin Nhắn",ng.getToken(),context);
                                              }
                                          }
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

            for(NguoiDung ng : nguoiDungs)
                if(ng.getMaSo().equals(tinNhan.getMaNguoiGui()))
                {
                    Glide.with(context)
                            .load(ng.getAnhDaiDien())
                            .placeholder(R.drawable.avatar)
                            .into(cheDoTinNhan.binding.anhgrnhan);
                }

            if(tinNhan.getTinNhan().equals("HinhAnh")) {
                cheDoTinNhan.binding.image.setVisibility(View.VISIBLE);
                cheDoTinNhan.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(tinNhan.getLinkAnhGui())
                        .placeholder(R.drawable.placeholder)
                        .into(cheDoTinNhan.binding.image);

            }
            FirebaseDatabase.getInstance()
                    .getReference().child("NguoiDung")
                    .child(tinNhan.getMaNguoiGui())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                NguoiDung nguoiDung = snapshot.getValue(NguoiDung.class);
                                cheDoTinNhan.binding.name.setText("@" + nguoiDung.getTen());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

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

            //    cheDoTinNhan.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            //        @Override
            //        public boolean onLongClick(View v) {
            //            View view = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null);
            //            DeleteDialogBinding binding = DeleteDialogBinding.bind(view);
            //            AlertDialog dialog = new AlertDialog.Builder(context)
            //                    .setTitle("Thu Hồi Tin Nhắn")
            //                    .setView(binding.getRoot())
            //                    .create();
//
            //            binding.delete.setVisibility(View.GONE);
            //            binding.everyone.setOnClickListener(new View.OnClickListener() {
            //                @Override
            //                public void onClick(View v) {
            //                    tinNhan.setTinNhan("Tin nhắn này đã bị xóa.");
            //                    tinNhan.setCamXuc(-1);
            //                    FirebaseDatabase.getInstance().getReference()
            //                            .child("Nhom")
            //                            .child(tinNhan.getMaTin()).setValue(tinNhan);
//
            //                    dialog.dismiss();
            //                }
            //            });
            //
//
            //            binding.cancel.setOnClickListener(new View.OnClickListener() {
            //                @Override
            //                public void onClick(View v) {
            //                    dialog.dismiss();
            //                }
            //            });
//
            //            dialog.show();
//
            //            return false;
            //        }
            //    });
        }
    }

    @Override
    public int getItemCount() {
        return tinNhans.size();
    }

    public class CheDoGuiTinNhan extends RecyclerView.ViewHolder {


        ItemSentGroupBinding binding;

        public CheDoGuiTinNhan(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentGroupBinding.bind(itemView);
        }

    }

    public class CheDoNhanTinNhan extends RecyclerView.ViewHolder {

        ItemReceiceGroupBinding binding;
        public CheDoNhanTinNhan(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiceGroupBinding.bind(itemView);
        }
    }

}
