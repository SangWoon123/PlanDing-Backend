package com.tukorea.planding.domain.group.service.query;

import com.tukorea.planding.domain.group.entity.UserGroup;
import com.tukorea.planding.domain.group.repository.usergroup.UserGroupRepository;
import com.tukorea.planding.global.error.BusinessException;
import com.tukorea.planding.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGroupQueryService {

    private final UserGroupRepository userGroupRepository;

    public void save(UserGroup userGroup) {
        userGroupRepository.save(userGroup);
    }

    public void checkUserAccessToGroupRoom(Long groupRoomId, Long userId) {
        boolean exists = userGroupRepository.existsByGroupRoomIdAndUserId(groupRoomId, userId);
        if (!exists) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }
}
