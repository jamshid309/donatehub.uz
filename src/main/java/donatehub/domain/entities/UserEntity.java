package donatehub.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import donatehub.domain.request.RegisterRequest;
import donatehub.domain.request.TelegramAuthRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import donatehub.domain.constants.UserRole;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity(name = "users_table")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(unique = true)
    private String username;

    private String email;

    private String description;

    @Column(name = "channel_url")
    private String channelUrl;

    @Column(unique = true, name = "channel_name")
    private String channelName;

    @Column(name = "profile_img_url")
    private String profileImgUrl;

    @Column(name = "banner_img_url")
    private String bannerImgUrl;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String token;

    private String password;

    private Boolean online;

    private Boolean enable;

    @Column(name = "last_online_at")
    @JsonFormat(pattern = "dd:mm:yyyy hh:MM")
    private LocalDateTime lastOnlineAt;

    @Column(name = "min_donation_amount")
    private Float minDonationAmount;

    @Column(name = "full_registered")
    private Boolean fullRegistered;

    private Float balance;

    public static UserEntity from(TelegramAuthRequest telegramAuthRequest) {
        return UserEntity.builder()
                .id(telegramAuthRequest.getId())
                .username(telegramAuthRequest.getFirstName())
                .role(UserRole.STREAMER)
                .token(UUID.randomUUID().toString())
                .online(false)
                .enable(false)
                .minDonationAmount(1000f)
                .balance(0f)
                .lastOnlineAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .fullRegistered(false)
                .updateAt(LocalDateTime.now())
                .build();
    }

    public static UserEntity from(RegisterRequest telegramAuthRequest, String profileImgUrl, String bannerImgUrl) {
        return UserEntity.builder()
                .username(telegramAuthRequest.getUsername())
                .email(telegramAuthRequest.getEmail())
                .password(telegramAuthRequest.getPassword())
                .description(telegramAuthRequest.getDescription())
                .channelUrl(telegramAuthRequest.getChannelUrl())
                .channelName(telegramAuthRequest.getChannelName())
                .profileImgUrl(profileImgUrl)
                .bannerImgUrl(bannerImgUrl)
                .role(UserRole.STREAMER)
                .token(UUID.randomUUID().toString())
                .online(false)
                .enable(false)
                .minDonationAmount(1000f)
                .balance(0f)
                .lastOnlineAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .fullRegistered(false)
                .updateAt(LocalDateTime.now())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
