package com.tukorea.planding.domain.group.dto;

import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class GroupInviteEvent extends ApplicationEvent {

    private final String userCode;
    private final String groupName;

    public GroupInviteEvent(Object source, String userCode, String groupName) {
        super(source);
        this.userCode = userCode;
        this.groupName = groupName;
    }

    public String getUserCode() {
        return userCode;
    }

    public String getGroupName() {
        return groupName;
    }
}
