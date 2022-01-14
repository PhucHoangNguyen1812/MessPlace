package com.doanjava.messbcode.utilities;

import java.util.HashMap;

public class Constants {



    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String KEY_NAME = "ten";
    public static final String KEY_ANH = "anh";
    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";
    public static final String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";

    public static  final String REMOTE_MSG_CALL_ROOM = "meetingRoom";

    public static HashMap<String,String> getRemoteMessageHeaders(){
        HashMap<String,String> headers = new HashMap<>();
        headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                "Key=AAAA8NUrIDc:APA91bHNbOtDbeO-Sq6_qQYVA7gTWy5RmrAVI7CAxHejA2unCSypQFywQRiZmrUrvMGP2zjTi9jEX3XuBjGdD4VJULkKg3YoaexdEoIrYliV6_5hy7c0XMfLvkCVjV0zc4sWZZMwbmKX"
        );
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return headers;
    }

}
