package com.doanjava.messbcode.Adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Activities.NhanTinDiaDiem;
import com.doanjava.messbcode.Models.DiaDiemYeuThich;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.RowConversationBinding;

import java.util.ArrayList;

public class ChuyenDoiDiaDiem extends RecyclerView.Adapter<ChuyenDoiDiaDiem.CheDoXemDiaDiem> {

    Context context;
    ArrayList<DiaDiemYeuThich> diaDiemYeuThichs;
    String maNguoiGui;
    public ChuyenDoiDiaDiem(Context context, String maNguoiGui, ArrayList<DiaDiemYeuThich> diaDiemYeuThichs) {
        this.context = context;
        this.diaDiemYeuThichs = diaDiemYeuThichs;
        this.maNguoiGui = maNguoiGui;
    }

    @NonNull
    @Override
    public CheDoXemDiaDiem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation,parent, false );

        return new CheDoXemDiaDiem(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull CheDoXemDiaDiem holder, int position) {
        DiaDiemYeuThich ddyt = diaDiemYeuThichs.get(position);

        holder.binding.username.setText(ddyt.getTenDiaDiem().toUpperCase());
        ArrayList<Address> addressList = new ArrayList<>();
        Geocoder geocoder = new Geocoder(context);
        try {
            addressList = (ArrayList<Address>) geocoder.getFromLocation(ddyt.getLatitude(), ddyt.getLongitude(), 1);
            Address address = addressList.get(0);
            if (address != null)
                holder.binding.lastMsg.setText(address.getAddressLine(0));
        }catch (Exception ex){}
        holder.binding.msgTime.setText("");
        Glide.with(context).load(ddyt.getAnhDiaDiem())
                .placeholder(R.drawable.anhdd)
                .into(holder.binding.profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NhanTinDiaDiem.class);
                intent.putExtra("maNguoiGui",maNguoiGui);
                intent.putExtra("chatDiaDiem",ddyt);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return diaDiemYeuThichs.size();
    }

    public  class CheDoXemDiaDiem extends RecyclerView.ViewHolder {

        RowConversationBinding binding;

        public CheDoXemDiaDiem(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
