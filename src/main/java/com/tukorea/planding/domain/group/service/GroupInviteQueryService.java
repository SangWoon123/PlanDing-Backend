package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.entity.GroupInvite;
import com.tukorea.planding.domain.group.entity.InviteStatus;
import com.tukorea.planding.domain.group.repository.GroupInviteRepository;
import com.tukorea.planding.domain.group.repository.GroupInviteRepositoryCustom;
import com.tukorea.planding.domain.user.entity.User;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupInviteQueryService {

    private final GroupInviteRepository groupInviteRepository;
    private final GroupInviteRepositoryCustom groupInviteRepositoryCustom;

    public GroupInvite getInvitationByInviteCode(String inviteCode) {
        return groupInviteRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTEXIST_INVITE));
    }

    public List<GroupInvite> getPendingInvitationForUser(User user) {
        return groupInviteRepositoryCustom.findPendingInvitationsForUser(user);
    }

    public Long countInvitation(String userCode, InviteStatus inviteStatus) {
        return groupInviteRepositoryCustom.countByInvitedUserCodeAndStatus(userCode, inviteStatus);
    }

}
