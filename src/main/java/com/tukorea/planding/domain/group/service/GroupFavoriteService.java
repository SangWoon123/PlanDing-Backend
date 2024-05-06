package com.tukorea.planding.domain.group.service;

import com.tukorea.planding.domain.group.repository.GroupFavoriteRepository;
import com.tukorea.planding.domain.user.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupFavoriteService {

    private final GroupFavoriteRepository groupFavoriteRepository;

    public void addFavorite(UserInfo userInfo, Long groupId){

    }
}
