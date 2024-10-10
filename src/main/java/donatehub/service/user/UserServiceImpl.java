package donatehub.service.user;

import donatehub.domain.projections.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import donatehub.domain.entities.UserEntity;
import donatehub.domain.constants.FileType;
import donatehub.domain.exceptions.BaseException;
import donatehub.domain.request.UserUpdateRequest;
import donatehub.repo.UserRepository;
import donatehub.service.cloud.CloudService;

import static donatehub.domain.constants.UserRole.STREAMER;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Logger log;
    private final UserRepository repo;
    private final CloudService cloudService;

    @Override
    public UserInfoForDonate findByChannelName(String channelName) {
        log.info("Kanaldan foydalanuvchi qidirilmoqda: {}", channelName);

        UserInfoForDonate info = repo.findByChannelNameIgnoreCase(channelName).orElseThrow(
                () -> new BaseException(
                        "Kanal topilmadi " + channelName,
                        HttpStatus.NOT_FOUND
                )
        );

        UserEntity user = findById(info.getId());

        if (!user.getEnable()) {
            throw new BaseException(
                    "Streamer topilmadi: " + user.getId(),
                    HttpStatus.NOT_FOUND
            );
        }

        return info;
    }

    @Override
    public Page<UserInfoForView> getUsersByEnableState(Boolean getEnables, int page, int size) {
        log.info("Foydalanuvchilarni faollik holati bo'yicha qidirish: faol - {}, sahifa - {}, o'lcham - {}", getEnables, page, size);
        return repo.getAllByEnableAndRoleOrderByFullRegistered(getEnables, PageRequest.of(page, size), STREAMER);
    }

    @Override
    public Page<UserInfoForView> searchUsers(String text, Boolean action, int page, int size) {
        log.info("Foydalanuvchilarni qidirish: matn - {}, sahifa - {}, o'lcham - {}", text, page, size);
        return repo.findAllByFirstNameOrUsernameAndEnable(text, text, action, PageRequest.of(page, size));
    }

    @Override
    public UserInfo getById(Long id) {
        log.info("ID bo'yicha foydalanuvchi olinmoqda: {}", id);

        return repo.getByIdOrderByCreatedAt(id).orElseThrow(
                () -> new BaseException(
                        "Foydalanuvchi topilmadi: " + id,
                        HttpStatus.NOT_FOUND
                )
        );
    }

    @Override
    public UserEntity save(UserEntity user) {
        log.info("Foydalanuvchi saqlanmoqda: {}", user);

        try {
            return repo.save(user);
        } catch (Exception e) {
            log.error("Foydalanuvchi saqlanmadi: {}", user);
            throw new BaseException("Email yoki username avval foydalanilgan", HttpStatus.CONFLICT);
        }
    }

    @Override
    public UserEntity findById(Long id) {
        log.info("ID bo'yicha foydalanuvchi olinmoqda: {}", id);

        return repo.findById(id).orElseThrow(
                () -> new BaseException(
                        "Foydalanuvchi topilmadi: " + id,
                        HttpStatus.NOT_FOUND
                )
        );
    }

    @Override
    public void update(Long id, UserUpdateRequest updateReq, MultipartFile profileImg, MultipartFile bannerImg) {
        log.info("Foydalanuvchi ma'lumotlarini yangilash: ID - {}", id);

        UserEntity user = findById(id);

        user.setDescription(updateReq.getDescription());
        user.setChannelUrl(updateReq.getChannelUrl());

        if (profileImg != null && !profileImg.isEmpty()) {
            String profileImgUrl = cloudService.uploadFile(profileImg, FileType.IMAGE);
            user.setProfileImgUrl(profileImgUrl);
        }

        if (bannerImg != null && !bannerImg.isEmpty()) {
            String bannerImgUrl = cloudService.uploadFile(bannerImg, FileType.IMAGE);
            user.setBannerImgUrl(bannerImgUrl);
        }

        repo.save(user);

        log.info("Foydalanuvchi ma'lumotlari yangilandi: ID - {}", id);
    }

    @Override
    public void recalculateStreamerBalance(Long streamerId, Float amount) {
        log.info("Streamer balansini qayta hisoblash: ID - {}, miqdor - {}", streamerId, amount);

        UserEntity user = findById(streamerId);
        user.setBalance(user.getBalance() + amount);
        repo.save(user);

        log.info("Streamer balansiga qo'shildi: ID - {}, yangi balans - {}", streamerId, user.getBalance());
    }

    @Override
    public void setEnable(Long streamerId, boolean action) {
        log.info("Streamer enable o'zgartirilmoqda: ID - {}, action - {}", streamerId, action);

        UserEntity user = findById(streamerId);
        user.setEnable(action);
        repo.save(user);

        log.info("Streamer enable o'zgartirildi: ID - {}, action - {}", streamerId, action);
    }

    @Override
    public void setOnline(Long streamerId, boolean action) {
        log.info("Stremar online o'zgartirilmoqda: ID - {}, action - {}", streamerId, action);

        UserEntity user = findById(streamerId);
        user.setOnline(action);
        repo.save(user);

        log.info("Stremar online o'zgartirildi: ID - {}, action - {}", streamerId, action);
    }

    @Override
    public void fullRegister(UserEntity user, UserUpdateRequest updateReq, MultipartFile profileImg, MultipartFile bannerImg) {
        log.info("Foydalanuvchi to'liq registratsiyadan o'tmoqda: ID - {}", user.getId());

        user.setRole(STREAMER);
        user.setFullRegistered(true);

        this.update(
                user.getId(),
                updateReq,
                profileImg,
                bannerImg
        );

        log.info("Foydalanuvchi ma'lumotlari yangilandi: ID - {}", user.getId());
    }

    @Override
    public UserStatistic getUsersStatistic() {
        return repo.getUsersStatistic();
    }

    @Override
    public ProfitStatistic getProfitStatistic() {
        return repo.getProfitStatistic();
    }

    @Override
    public List<UserEntity> getAllEnabledUsers() {
        return repo.getAllByEnableTrue();
    }

    @Override
    public List<AdminStatisticByGraphic> adminStatisticByGraphic(int days) {
        return repo.getStatistic(days);
    }

    @Override
    public List<UserStatisticByGraphic> userStatisticByGraphic(int days, Long streamerId) {
        findById(streamerId);

        return repo.getStatisticOfStreamer(days, streamerId);
    }

    @Override
    public Optional<UserEntity> findByEmail(String usernameOrEmail) {
        return repo.findByEmailOrUsername(usernameOrEmail, usernameOrEmail);
    }
}