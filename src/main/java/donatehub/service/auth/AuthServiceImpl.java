package donatehub.service.auth;

import donatehub.config.security.JwtProvider;
import donatehub.domain.constants.FileType;
import donatehub.domain.constants.UserRole;
import donatehub.domain.entities.UserEntity;
import donatehub.domain.exceptions.BaseException;
import donatehub.domain.request.LoginRequest;
import donatehub.domain.request.RefreshTokenRequest;
import donatehub.domain.request.RegisterRequest;
import donatehub.domain.response.LoginResponse;
import donatehub.service.cloud.CloudService;
import donatehub.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final CloudService cloudService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        UserEntity user = userService.findByEmail(loginRequest.getUsernameOrEmail()).orElseThrow(
                () -> new BaseException(
                        "Username, Email yoki parol noto'g'ri",
                        HttpStatus.NOT_FOUND
                )
        );

        String accessToken = jwtProvider.generateAccessToken(user.getId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        return new LoginResponse(
                accessToken,
                refreshToken
        );
    }

    @Override
    public void register(RegisterRequest registerRequest, MultipartFile profileImage, MultipartFile bannerImage) {
        userService.save(UserEntity.from(registerRequest, cloudService.uploadFile(profileImage, FileType.IMAGE), cloudService.uploadFile(bannerImage, FileType.IMAGE)));
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest refreshToken) {
        Long userId = jwtProvider.extractUserId(refreshToken.getRefreshToken());

        UserEntity user = userService.findById(userId);

        String accessToken = jwtProvider.generateAccessToken(userId);

        return new LoginResponse(
                accessToken,
                refreshToken.getRefreshToken()
        );
    }
}
