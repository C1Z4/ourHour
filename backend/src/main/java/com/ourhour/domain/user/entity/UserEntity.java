package com.ourhour.domain.user.entity;

import com.ourhour.domain.user.enums.Platform;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="tbl_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private List<UserEntity> userEntityList = new ArrayList<>();

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Platform platform;
}
