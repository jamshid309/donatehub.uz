package donatehub.service.user;

import donatehub.domain.projections.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import donatehub.domain.entities.UserEntity;
import donatehub.domain.request.UserUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserInfoForDonate findByChannelName(String channelName);

    Page<UserInfoForView> getUsersByEnableState(Boolean getEnables, int page, int size);

    Page<UserInfoForView> searchUsers(String text, Boolean action, int page, int size);

    UserInfo getById(Long id);

    UserEntity save(UserEntity user);

    UserEntity findById(Long id);

    void recalculateStreamerBalance(Long streamerId, Float amount);

    void update(Long userId, UserUpdateRequest updateReq, MultipartFile profileImg, MultipartFile bannerImg);

    void setEnable(Long streamerId, boolean action);

    void setOnline(Long streamerId, boolean action);

    void fullRegister(UserEntity user, UserUpdateRequest updateReq, MultipartFile profileImg, MultipartFile bannerImg);

    UserStatistic getUsersStatistic();

    ProfitStatistic getProfitStatistic();

    List<UserEntity> getAllEnabledUsers();

    List<AdminStatisticByGraphic> adminStatisticByGraphic(int days);

    List<UserStatisticByGraphic> userStatisticByGraphic(int days, Long streamerId);

    Optional<UserEntity> findByEmail(String usernameOrEmail);
}
