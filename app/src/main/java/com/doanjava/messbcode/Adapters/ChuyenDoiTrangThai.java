package com.doanjava.messbcode.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doanjava.messbcode.Activities.MainActivity;
import com.doanjava.messbcode.Activities.NhanTinDiaDiem;
import com.doanjava.messbcode.Models.CauChuyen;
import com.doanjava.messbcode.Models.NguoiDung;
import com.doanjava.messbcode.Models.TrangThaiNguoiDung;
import com.doanjava.messbcode.R;
import com.doanjava.messbcode.databinding.ItemStatusBinding;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class ChuyenDoiTrangThai extends RecyclerView.Adapter<ChuyenDoiTrangThai.CheDoXemTrangThai> {

    ArrayList<NguoiDung> nguoiDungs;
    NguoiDung nguoiDung;
    String ten = "";
    public void setNguoiDung(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public void setNguoiDungs(ArrayList<NguoiDung> nguoiDungs) {
        this.nguoiDungs = nguoiDungs;
    }

    Context context;
    ArrayList<TrangThaiNguoiDung> trangThaiNguoiDungs;
    public ChuyenDoiTrangThai(Context context, ArrayList<TrangThaiNguoiDung> trangThaiNguoiDungs) {
        this.context = context;
        this.trangThaiNguoiDungs = trangThaiNguoiDungs;
    }

    public ChuyenDoiTrangThai(){}
    @NonNull
    @Override
    public CheDoXemTrangThai onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new CheDoXemTrangThai(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull CheDoXemTrangThai holder, int position) {
            TrangThaiNguoiDung trangThaiNguoiDung = trangThaiNguoiDungs.get(position);
        try {
            CauChuyen cauChuyenCuoi = trangThaiNguoiDung.getCauChuyens().get(trangThaiNguoiDung.getCauChuyens().size() - 1);

            Glide.with(context).load(cauChuyenCuoi.getLinkAnh()).into(holder.binding.image);

            holder.binding.circularStatusView.setPortionsCount(trangThaiNguoiDung.getCauChuyens().size());
        }catch (Exception Ex){}
        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(CauChuyen cauChuyen : trangThaiNguoiDung.getCauChuyens()){
                    myStories.add(new MyStory(cauChuyen.getLinkAnh()));
                }

                if(trangThaiNguoiDung.getNguoiDang().equals(nguoiDung.getMaSo()))
                    ten = nguoiDung.getTen();
                else
                {
                    for (NguoiDung ng : nguoiDungs)
                    {
                        if(ng.getMaSo().equals(trangThaiNguoiDung.getNguoiDang()))
                            ten = ng.getTen();
                    }
                }


                try {
                    new StoryView.Builder(((MainActivity) context).getSupportFragmentManager())
                            .setStoriesList(myStories) // Required
                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                            .setTitleText(ten) // Default is Hidden
                            .setSubtitleText("") // Default is Hidden
                            .setTitleLogoUrl(trangThaiNguoiDung.getAnhDaiDien()) // Default is Hidden
                            .setStoryClickListeners(new StoryClickListeners() {
                                @Override
                                public void onDescriptionClickListener(int position) {
                                    //your action
                                }

                                @Override
                                public void onTitleIconClickListener(int position) {
                                    //your action
                                }
                            }) // Optional Listeners
                            .build() // Must be called before calling show method
                            .show();
                }
                catch (Exception ex){
                    new StoryView.Builder(((NhanTinDiaDiem) context).getSupportFragmentManager())
                            .setStoriesList(myStories) // Required
                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                            .setTitleText(ten) // Default is Hidden
                            .setSubtitleText("") // Default is Hidden
                            .setTitleLogoUrl(trangThaiNguoiDung.getAnhDaiDien()) // Default is Hidden
                            .setStoryClickListeners(new StoryClickListeners() {
                                @Override
                                public void onDescriptionClickListener(int position) {
                                    //your action
                                }

                                @Override
                                public void onTitleIconClickListener(int position) {
                                    //your action
                                }
                            }) // Optional Listeners
                            .build() // Must be called before calling show method
                            .show();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(trangThaiNguoiDungs == null)
            return 0;
        return trangThaiNguoiDungs.size();
    }

    public class CheDoXemTrangThai extends RecyclerView.ViewHolder {

        ItemStatusBinding binding;

        public CheDoXemTrangThai(@NonNull View itemView) {
            super(itemView);
            binding = ItemStatusBinding.bind(itemView);
        }
    }
}
