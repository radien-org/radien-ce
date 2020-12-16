package io.radien.ms.usermanagement.legacy;

import java.util.Collection;
import java.util.List;

/**
 * @author Bruno Gama
 */
public interface UserServiceAccess extends ServiceAccess {

    SystemUser get(Long userId);

    List<SystemUser> get(List<Long> userId);

    List<SystemUser> getAll();

    void save(SystemUser user);

    void delete(SystemUser systemUser);

    void delete(Collection<SystemUser> systemUser);

    SystemUser getUserByLoginId(String loginId) throws UserNotFoundException;

    @Deprecated
    SystemUser getUserByEmailAndEnabled(String email, boolean enabled) throws UserNotFoundException;

    List<SystemUser> getUsers(boolean registered, Long days);

    List<SystemUser> getUsersById(Collection<Long> ids);
}
