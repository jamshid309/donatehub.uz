package donatehub.service.auth;

import donatehub.domain.request.LoginRequest;
import donatehub.domain.request.RegisterRequest;
import donatehub.domain.request.RefreshTokenRequest;
import donatehub.domain.response.LoginResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    LoginResponse login(LoginRequest LoginRequest);

    void register(RegisterRequest registerRequest, MultipartFile profileImage, MultipartFile bannerImage);

    LoginResponse refreshToken(RefreshTokenRequest refreshToken);
}
