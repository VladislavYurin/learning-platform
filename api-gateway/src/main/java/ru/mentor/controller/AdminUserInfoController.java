package ru.mentor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.admin.UsersProto;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.grpc.UsersServiceGrpcClient;
import ru.mentor.mapper.UserInfoMapper;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserInfoController {

    private final UsersServiceGrpcClient usersServiceGrpcClient;
    private final UserInfoMapper userInfoMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserInfoDto> getUserById(@PathVariable Long id) {
        UsersProto.UserInfoDto protoResponse = usersServiceGrpcClient.getUserById(id);
        UserInfoDto dto = userInfoMapper.fromProtoUserInfo(protoResponse);

        return ResponseEntity.ok().body(dto);
    }
}