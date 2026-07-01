package com.example.community.user.controller;

import com.example.community.global.ApiResponse;
import com.example.community.user.dto.*;
import com.example.community.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        LoginResponseDTO responseDTO = userService.login(requestDTO);
        return ResponseEntity.ok(new ApiResponse<>("user_login_success", responseDTO));
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader(value = "Authorization") String authorizationHeader) {
        userService.logout(authorizationHeader);
        return ResponseEntity.ok(new ApiResponse<>("logout_success", null));
    }
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponseDTO>> signUp(@Valid @RequestBody SignUpRequestDTO requestDTO) {
        SignUpResponseDTO responseDTO = userService.signUp(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("user_register_success", responseDTO));
    }
    @PatchMapping("/{userId}/info")
    public ResponseEntity<ApiResponse<ModifyInfoResponseDTO>> modifyInfo(@PathVariable Long userId, @RequestHeader(value = "Authorization") String authorizationHeader,@Valid @RequestBody ModifyInfoRequestDTO requestDTO){
        ModifyInfoResponseDTO responseDTO = userService.modifyInfo(userId, authorizationHeader, requestDTO);
        return ResponseEntity.ok().body(new ApiResponse<>("user_modify_success", responseDTO));
    }
    @PatchMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<String>> modifyPassword(@PathVariable Long userId, @RequestHeader(value = "Authorization") String authorizationHeader, @Valid @RequestBody ModifyPasswordRequestDTO requestDTO){
        userService.modifyPassword(userId, authorizationHeader, requestDTO);
        return ResponseEntity.ok().body(new ApiResponse<>("password_modify_success", null));
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<WithdrawResponseDTO>>  withdraw(@PathVariable Long userId, @RequestHeader(value = "Authorization") String authorizationHeader){
        WithdrawResponseDTO responseDTO = userService.withdraw(userId, authorizationHeader);
        return ResponseEntity.ok().body(new ApiResponse<>("user_withdraw_success", responseDTO));
    }
}
