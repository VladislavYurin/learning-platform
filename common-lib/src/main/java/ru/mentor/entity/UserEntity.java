package ru.mentor.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.mentor.constant.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сущность — пользователь, на которой завязаны процессы
 * регистрации и авторизации
 */
@Entity
@Getter
@Setter
@Builder
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity implements UserDetails {

    /**
     * id пользователя
     * генерируется с помощью SEQUENCE
     */
    @Id
    @Column(name = "id_user")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя (логин).
     */
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    /**
     * Пароль пользователя.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Роль пользователя в системе.
     * Определяет уровень доступа пользователя.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private Role role;

    /**
     * Имя пользователя.
     */
    @Column(name = "firstname", nullable = false)
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    @Column(name = "lastname", nullable = false)
    private String lastName;

    /**
     * Telegram никнейм пользователя.
     */
    @Column(name = "tg_nickname", nullable = false)
    private String tgNickname;

    /**
     * Идентификатор чата Telegram пользователя.
     */
    @Column(name = "telegram_chat_id")
    private Long tgChatId;

    /**
     * Ключ аватара в MinIO.
     */
    @Column(name = "user_avatar_key")
    private String userAvatarKey;

    /**
     * Список доступов пользователя к курсам.
     * Содержит все записи о курсах, к которым предоставлен доступ данному пользователю.
     */
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCourseAccessEntity> courseAccesses = new ArrayList<>();

    /**
     * Список доступов пользователя к модулям.
     * Содержит все записи о модулях, к которым предоставлен доступ данному пользователю.
     */
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserModuleAccessEntity> moduleAccesses = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "meetingParticipants")
    private Set<MentorTimeSlotEntity> timeSlots = new HashSet<>();

    /**
     * Список тегов ментора. ТОЛЬКО ДЛЯ МЕНТОРОВ.
     */
    @Builder.Default
    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<MentorTagLinkEntity> mentorTags = new ArrayList<>();

    /**
     * Возвращает список ролей пользователя.
     * Преобразует роль пользователя в формат, понятный Spring Security.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    /**
     * Проверяет, не истек ли срок действия учетной записи.
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * Проверяет, не заблокирована ли учетная запись.
     */
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    /**
     * Проверяет, не истек ли срок действия учетных данных (пароля).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * Проверяет, включена ли учетная запись.
     */
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

}