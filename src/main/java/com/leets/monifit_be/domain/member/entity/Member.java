package com.leets.monifit_be.domain.member.entity;

import com.leets.monifit_be.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kakao_id", nullable = false, unique = true)
    private Long kakaoId;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Builder
    private Member(Long kakaoId, String email, String name) {
        this.kakaoId = kakaoId;
        this.email = email;
        this.name = name;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
