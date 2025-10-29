package ru.mentor.entity;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.mentor.constant.Role;

/**
 * Сущность — пользователь, на которой завязаны процессы
 * регистрации и авторизации
 */
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
    @Column("id_user")
    private Long id;

    /**
     * Имя пользователя (логин).
     */
    @Column("username")
    private String username;

    /**
     * Пароль пользователя.
     */
    @Column("password")
    private String password;

    /**
     * Роль пользователя в системе.
     * Определяет уровень доступа пользователя.
     */
    @Column("user_role")
    private Role role;

    /**
     * Имя пользователя.
     */
    @Column("firstname")
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    @Column("lastname")
    private String lastName;

    /**
     * Telegram никнейм пользователя.
     */
    @Column("tg_nickname")
    private String tgNickname;

    /**
     * Идентификатор чата Telegram пользователя.
     */
    @Column("telegram_chat_id")
    private Long tgChatId;

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