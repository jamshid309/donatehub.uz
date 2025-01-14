package donatehub.controller;

import donatehub.domain.request.LoginRequest;
import donatehub.domain.request.RefreshTokenRequest;
import donatehub.domain.request.RegisterRequest;
import donatehub.domain.request.TelegramAuthRequest;
import donatehub.service.oauth.TelegramAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import donatehub.domain.response.ExceptionResponse;
import donatehub.domain.response.LoginResponse;
import donatehub.service.auth.AuthService;
import org.springframework.web.multipart.MultipartFile;

/**
 * AuthController - Telegram orqali foydalanuvchilarni autentifikatsiya qilish uchun REST API.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Foydalanuvchilarni Telegram orqali autentifikatsiya qilish uchun API metodlari")
public class AuthController {
    private final AuthService authService;
    private final TelegramAuthService telegramAuthService;

    @PostMapping("/register")
    public void register(@RequestPart @Valid RegisterRequest registerRequest,@RequestPart(required = false) MultipartFile profileImage, @RequestPart(required = false) MultipartFile bannerImage) {
        authService.register(registerRequest, profileImage, bannerImage);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/oauth/telegram")
    @Operation(
            summary = "Foydalanuvchini Telegram orqali autentifikatsiya qilish",
            description = "Telegramdan kelgan autentifikatsiya so'rovlarini qabul qiladi va foydalanuvchining tizimga kirishini ta'minlaydi.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Telegram orqali autentifikatsiya uchun foydalanuvchi ma'lumotlari",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TelegramAuthRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Muvaffaqiyatli autentifikatsiya", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Noto'g'ri so'rov ma'lumotlari yoki autentifikatsiya muvaffaqiyatsiz", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Server xatosi yoki tizim ichki muammosi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
            }
    )
    public LoginResponse auth(
            @RequestBody @Valid @Parameter(description = "Foydalanuvchining autentifikatsiya uchun kerakli ma'lumotlar") TelegramAuthRequest telegramAuthRequest
    ) {
        return telegramAuthService.login(telegramAuthRequest);
    }

    @PostMapping("/refresh-token")
    @Operation(
            summary = "Tokenni yangilash",
            description = "Tokenni yangilash uchun kerakli ma'lumotlarni jo'nating",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RefreshTokenRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tokenni yangilandi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User topilmadi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Token eskirgan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Server xatosi yoki tizim ichki muammosi", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
            }
    )
    public LoginResponse refreshToken(@RequestBody @Valid RefreshTokenRequest refreshToken) {
        return authService.refreshToken(refreshToken);
    }
}