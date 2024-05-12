package com.tukorea.planding.domain.group.service.query;

import com.tukorea.planding.domain.group.entity.GroupInvite;
import com.tukorea.planding.domain.group.entity.InviteStatus;
import com.tukorea.planding.domain.group.repository.invite.GroupInviteRepository;
import com.tukorea.planding.domain.group.repository.invite.GroupInviteRepositoryCustom;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupInviteQueryService {

    private final GroupInviteRepository groupInviteRepository;

    public GroupInvite getInvitationByInviteCode(String inviteCode) {
        return groupInviteRepository.findByGroupInviteCode(inviteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTEXIST_INVITE));
    }

    public List<GroupInvite> getPendingInvitationForUser(Long userId) {
        return groupInviteRepository.findPendingInvitationsForUser(userId);
    }

    public Long countInvitation(String userCode, InviteStatus inviteStatus) {
        return groupInviteRepository.countByInvitedUserCodeAndStatus(userCode, inviteStatus);
    }

    public void delete(Long inviteId) {
        groupInviteRepository.deleteById(inviteId);
    }
}
