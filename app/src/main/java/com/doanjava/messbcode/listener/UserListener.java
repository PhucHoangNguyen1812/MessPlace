package com.doanjava.messbcode.listener;

import com.doanjava.messbcode.Models.NguoiDung;

public interface UserListener {
    void initiateVideoMeeting(NguoiDung user);
    void initiateAudioMeeting(NguoiDung user);
}
