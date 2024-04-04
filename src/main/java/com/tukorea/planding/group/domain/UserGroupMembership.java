package com.tukorea.planding.group.domain;

import com.tukorea.planding.group.domain.GroupRoom;
import com.tukorea.planding.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGroupMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_room_id")
    private GroupRoom groupRoom;

    @Builder
    public UserGroupMembership(User user, GroupRoom groupRoom) {
        this.user = user;
        this.groupRoom = groupRoom;
    }
}