package io.radien.api.service.user;

import java.util.Collection;
import java.util.List;

import io.radien.api.model.user.SystemUser;
import io.radien.api.service.ServiceAccess;

/**
 * @author Bruno Gama
 * @author Marco Weiland <m.weiland@radien.io>
 *
 */
public interface UserServiceAccess extends ServiceAccess {

    public SystemUser get(Long userId);

    public List<SystemUser> get(List<Long> userId);

    public List<SystemUser> getAll();

    public void save(SystemUser user);

    public void delete(SystemUser systemUser);

    public void delete(Collection<SystemUser> systemUser);

}
