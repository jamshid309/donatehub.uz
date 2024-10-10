package donatehub.service.oauth;

import donatehub.domain.request.TelegramAuthRequest;
import donatehub.domain.request.WidgetCreateRequest;
import donatehub.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import donatehub.domain.entities.UserEntity;
import donatehub.domain.response.LoginResponse;
import donatehub.repo.UserRepository;
import donatehub.config.security.JwtProvider;
import donatehub.service.widget.WidgetService;
import donatehub.utils.TelegramUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Service
@RequiredArgsConstructor
public class TelegramAuthService {
    private final Logger log;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final WidgetService widgetService;

    @Value("${bot.token}")
    private String botToken;

    public LoginResponse login(TelegramAuthRequest telegramAuthRequest) {
        log.info("Tizimga kirish jarayoni boshlandi: {}", telegramAuthRequest);

        String dataCheckString = TelegramUtils.getDataCheckString(
                telegramAuthRequest.getId(),
                telegramAuthRequest.getFirstName(),
                telegramAuthRequest.getUsername(),
                telegramAuthRequest.getAuthDate()
        );

//        if (!TelegramUtils.verifyAuth(dataCheckString, botToken, telegramAuthRequest.getHash())) {
//            log.error("Ushbu so'rov Telegramdan emas: {}", telegramAuthRequest);
//
//            throw new BaseException("Ushbu so'rov Telegramdan emas", HttpStatus.BAD_REQUEST);
//        }

        UserEntity user = userRepository.findById(telegramAuthRequest.getId())
                .orElseGet(() -> createNewUser(telegramAuthRequest));

        updateUser(user, telegramAuthRequest);

        log.info("Foydalanuvchi muvaffaqiyatli tizimga kirdi: {}", user.getId());

        return new LoginResponse(
                jwtProvider.generateAccessToken(user.getId()),
                jwtProvider.generateRefreshToken(user.getId())
        );
    }

    private void updateUser(UserEntity user, TelegramAuthRequest telegramAuthRequest) {
        boolean updated = false;
        user.setLastOnlineAt(LocalDateTime.now());

        if (!Objects.equals(user.getUsername(), telegramAuthRequest.getUsername())) {
            user.setUsername(telegramAuthRequest.getUsername());
            updated = true;
        }

        if (updated) {
            userRepository.save(user);
            log.info("Foydalanuvchi ma'lumotlari yangilandi: {}", user);
        }
    }

    private UserEntity createNewUser(TelegramAuthRequest telegramAuthRequest) {
        log.warn("Foydalanuvchi topilmadi, yangi foydalanuvchi yaratilmoqda: {}", telegramAuthRequest);

        UserEntity newUser = UserEntity.from(telegramAuthRequest);

        userRepository.save(newUser);
        widgetService.create(telegramAuthRequest.getId(), new WidgetCreateRequest(1000f, 5), null, null);

        log.info("Yangi foydalanuvchi yaratildi va widget qo'shildi: {}", newUser);

        return newUser;
    }
}