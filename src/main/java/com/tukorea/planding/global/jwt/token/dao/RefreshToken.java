package com.tukorea.planding.global.jwt.token.dao;

import com.tukorea.planding.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;


    public void update(String refreshToken){
        this.refreshToken=refreshToken;
    }
}
