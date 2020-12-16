/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.radien.ms.usermanagement.legacy;

import java.security.Identity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.bind.ValidationException;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.User;

import org.openappframe.api.module.ticket.model.SystemTicket;
import org.openappframe.persistence.jpa.EntityManagerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.exception.UserNotFoundException;

/**
 * The default implementation of the {@link UserDataAccessLayer} interface for
 * oaf applications
 *
 * @author Marco Weiland
 * @author José Rodrigues
 * @author Rafael Fernandes
 * @author João Alves Coelho
 */
@RequestScoped
public class UserDataLayer implements UserDataAccessLayer {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(UserContextDataLayer.class);

	@Inject
	private EntityManager em;



	/**
	 * {@inheritDoc}
	 */
	@Override
	// TODO: this method has to be revised. we need single private methods for
	// each possible login (main: logon and email, secondary: linked accounts)
	public SystemUser getUserByLoginId(String loginId) throws UserNotFoundException {
		//TODO: make logon methods configurable
		Optional<SystemUser> user = Optional.empty();
		//1st step, check logon
		try {
			user = getUserByLogon(loginId);
			log.info("User found by logon:  {}", loginId);
		} catch (Exception e) {
			log.error("User not found using method logon: {}",loginId);
		}

		if ( !user.isPresent()) {
			//2nd step, check email
			try {
				user = getUserByEmail(loginId);
				log.info("User found by email:  {}", loginId);
			} catch (Exception e) {
				log.error("User not found using method email: {}",loginId);
			}
			if ( !user.isPresent()) {
				throw new UserNotFoundException("The user with loginId " + loginId + " cannot be found");
			}
		}
		return user.get();
	}

	/**
	 * {@inheritDoc}
	 */
	// TODO: this method has to be revised. we need single private methods for
	// each possible login (main: logon and email, secondary: linked accounts)
	// we need a generic userquery method here
	public Optional<SystemUser> getUserByLogon(String loginId){
		log.debug("Get user with logon:{}", loginId);
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);

		select.where(builder.equal(root.get("logon"), loginId));

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return Optional.of(typedQuery.getSingleResult());
		} catch (NonUniqueResultException e) {
			log.error("Duplicate user record with logon {}", loginId);
			throw new NonUniqueResultException("There is more than one user with the same logon");
		} catch (NoResultException e) {
			return Optional.empty();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public Optional<SystemUser> getUserByEmail(String email) {
		log.debug("Get user with email:{}", email);
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);

		select.where(builder.equal(root.get("userEmail"), email));

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return Optional.of(typedQuery.getSingleResult());
		} catch (NonUniqueResultException e) {
			log.error("Duplicate user record with Email {}", email);
			throw new NonUniqueResultException("There is more than one user with the same Email");
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	@Override
	@Deprecated
	public SystemUser getUserByGUID(String guid) throws UserNotFoundException {
		log.debug("Get user with logon and guid: {}", guid);
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);

		select.where(builder.equal(root.get("globalUserId"), guid));

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return typedQuery.getSingleResult();
		} catch (NonUniqueResultException e) {
			log.error("Duplicate user record with GUID {}", guid);
			throw new NonUniqueResultException("There is more than one user with the same GUID");
		} catch (NoResultException e) {
			throw new UserNotFoundException("There is no user with the GUID " + guid);
		}
	}

	@Override
	@Deprecated
	public SystemUser getUserByGID(String gid) throws UserNotFoundException {
		log.debug("Get user with GID :{}", gid);
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);

		Join<Organization, Identity> identityJoin = root.join("identityId", JoinType.INNER);
		select.select(root).where(identityJoin.get("globalId").in(gid));

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return typedQuery.getSingleResult();
		} catch (NonUniqueResultException e) {
			log.error("Duplicate user record with GID: {}", gid);
			throw new NonUniqueResultException("There is more than one user with the same logon");
		} catch (NoResultException e) {
			throw new UserNotFoundException("There is no user with the logon " + gid);
		}
	}

	@Override
	@Deprecated
	public SystemUser getUserByLogonOrGUID(String logonOrGUID) throws UserNotFoundException {
		log.debug("Get user with logon or guid: {}", logonOrGUID);
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);

		Predicate logon = builder.equal(root.get("logon"), logonOrGUID);
		// Predicate guid = builder.equal(root.get("globalUserId"),
		// logonOrGUID);
		Predicate email = builder.equal(root.get("userEmail"), logonOrGUID);

		Predicate user = builder.or(logon, email);

		select.where(user);

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return typedQuery.getSingleResult();
		} catch (NonUniqueResultException e) {
			log.error("Duplicate user record with Logon or GUID {}", logonOrGUID);
			throw new NonUniqueResultException("There is more than one user with the same Logon or GUID");
		} catch (NoResultException e) {
			throw new UserNotFoundException("There is no user with the Logon or GUID " + logonOrGUID);
		}
	}

	@Override
	public List<SystemUser> getUsers(boolean registered, Long days) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);
		CriteriaQuery<User> select = criteriaQuery.select(root);
		Predicate p = builder.equal(root.get("enabled"),registered);
		if(days != null){
			LocalDate daysAgo = LocalDate.now().minusDays(days);
			Join<User, Identity> identityJoin = root.join("identityId", JoinType.INNER);
			Predicate datePredicate = builder.or(
					builder.lessThanOrEqualTo(root.get("registerDate"),java.sql.Date.valueOf(daysAgo)),
					builder.lessThanOrEqualTo(identityJoin.get("createDate"),java.sql.Date.valueOf(daysAgo))
			);
			p = builder.and(p,datePredicate);
		}

		TypedQuery<User> typedQuery = em.createQuery(select.where(p));

		return new ArrayList<>(typedQuery.getResultList());
	}

	@Override
	public List<SystemUser> getUsersWithoutContext(boolean registered, Long days) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);
		CriteriaQuery<User> select = criteriaQuery.select(root);

		Join<User, Context> contextJoin = root.join("contexts", JoinType.LEFT);
		Predicate p = builder.and(
				builder.equal(root.get("enabled"),registered),
				builder.isNull(contextJoin.get("id")));
		if(days != null){
			LocalDate daysAgo = LocalDate.now().minusDays(days);
			Join<User, Identity> identityJoin = root.join("identityId", JoinType.INNER);
			Predicate datePredicate = builder.or(builder.lessThanOrEqualTo(root.get("registerDate"),java.sql.Date.valueOf(daysAgo)),
					builder.lessThanOrEqualTo(identityJoin.get("createDate"),java.sql.Date.valueOf(daysAgo)));
			p = builder.and(p,datePredicate);
		}

		TypedQuery<User> typedQuery = em.createQuery(select.distinct(true).where(p));

		return new ArrayList<>(typedQuery.getResultList());
	}

	@Override
	public List<Long> getUserIdsWithContext() {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
		Root<User> root = criteriaQuery.from(User.class);
		CriteriaQuery<Long> select = criteriaQuery.select(root.get("id"));

		Join<User, Context> contextJoin = root.join("contexts", JoinType.INNER);

		TypedQuery<Long> typedQuery = em.createQuery(select.distinct(true));

		return new ArrayList<>(typedQuery.getResultList());
	}

	@Override
	public List<Long> getUserIdsWithoutContext(Long forDays) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
		Root<User> root = criteriaQuery.from(User.class);
		CriteriaQuery<Long> select = criteriaQuery.select(root.get("id"));

		Join<User, Context> contextJoin = root.join("contexts", JoinType.LEFT);
		Predicate p= builder.isNull(contextJoin.get("id"));
		if(forDays != null){
			LocalDate daysAgo = LocalDate.now().minusDays(forDays);
			p = builder.and(p,builder.lessThanOrEqualTo(root.get("registerDate"),java.sql.Date.valueOf(daysAgo)));
		}
		TypedQuery<Long> typedQuery = em.createQuery(select.distinct(true).where(p));

		return new ArrayList<>(typedQuery.getResultList());
	}

	@Override
	public List<SystemUser> getUsersInactiveForDays(long inactiveDays) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);
		CriteriaQuery<User> select = criteriaQuery.select(root);
		LocalDate daysAgo = LocalDate.now().minusDays(inactiveDays);
		Predicate datePredicate = builder.lessThanOrEqualTo(root.get("lastLogin"),java.sql.Date.valueOf(daysAgo));
		TypedQuery<User> typedQuery = em.createQuery(select.where(datePredicate));

		return new ArrayList<>(typedQuery.getResultList());
	}

	@Deprecated
	public SystemUser getUserByLogonOrGUIDOrEmail(String logonOrGUIDOrEmail) throws UserNotFoundException {
		log.debug("Get user with logon or guid: or email {}", logonOrGUIDOrEmail);
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);

		Predicate logon = builder.equal(root.get("logon"), logonOrGUIDOrEmail);
		Predicate guid = builder.equal(root.get("globalUserId"), logonOrGUIDOrEmail);
		Predicate email = builder.equal(root.get("userEmail"), logonOrGUIDOrEmail);

		Predicate user = builder.or(logon, guid, email);

		select.where(user);

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return typedQuery.getSingleResult();
		} catch (NonUniqueResultException e) {
			log.error("Duplicate user record with Logon or GUID or Email {}", logonOrGUIDOrEmail);
			throw new NonUniqueResultException("There is more than one user with the same Logon or GUID or Email");
		} catch (NoResultException e) {
			throw new UserNotFoundException("There is no user with the Logon or GUID or Email " + logonOrGUIDOrEmail);
		}
	}

	@Override
	@Deprecated
	public SystemUser getUserByLogonOrEmail(String logonOrEmail) throws UserNotFoundException {
		log.debug("Get user with logon or email: {}", logonOrEmail);
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);

		Predicate logon = builder.equal(root.get("logon"), logonOrEmail);
		Predicate email = builder.equal(root.get("userEmail"), logonOrEmail);

		Predicate user = builder.or(logon, email);

		select.where(user);

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return typedQuery.getSingleResult();
		} catch (NonUniqueResultException e) {
			log.error("Duplicate user record with Logon or Email {}", logonOrEmail);
			throw new NonUniqueResultException("There is more than one user with the same Logon or Email");
		} catch (NoResultException e) {
			throw new UserNotFoundException("There is no user with the Logon or Email " + logonOrEmail);
		}
	}

	@Override
	@Deprecated
	public SystemUser getUserByEmailAndEnabled(String email, boolean enabled) throws UserNotFoundException {
		log.debug("Get user with email:{} and enabled:{}", email, enabled);
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);
		Predicate emailPredicate = builder.equal(root.get("userEmail"), email);
		Predicate enabledPredicate = builder.equal(root.get("enabled"), enabled);
		select.where(builder.and(emailPredicate, enabledPredicate));

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return typedQuery.getSingleResult();
		} catch (NonUniqueResultException e) {
			log.error("Duplicate user record with Email {} and Enabled {}", email, enabled);
			throw new NonUniqueResultException("There is more than one user with the same Email");
		} catch (NoResultException e) {
			throw new UserNotFoundException("There is no user with the Email " + email + " and Enabled " + enabled);
		}
	}

	@Override
	@Deprecated
	public SystemUser getUserByIdentityKey(String identityKey) throws UserNotFoundException {
		log.debug("Get user with identityKey :{}", identityKey);
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);

		Join<User, Identity> identityJoin = root.join("identityId", JoinType.INNER);
		select.select(root).where(identityJoin.get("identityKey").in(identityKey));

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return typedQuery.getSingleResult();
		} catch (NonUniqueResultException e) {
			log.error("Duplicate user record with Identity Key {}", identityKey);
			throw new NonUniqueResultException("There is more than one user with the same Identity Key");
		} catch (NoResultException e) {
			throw new UserNotFoundException("There is no user with the Identity Key " + identityKey);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override 
	public void save(SystemUser obj) {
		log.debug("Saving User {}", obj);
		User user = userFactory.convert(obj);
		user.setLastUpdate(new Date());
		EntityManagerUtil.saveOrUpdate(user, em);

		log.debug("User saved.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(SystemUser obj) {
		log.debug("Deleting User {}", obj);
		User user = userFactory.convert(obj);
		EntityManagerUtil.delete(user, em);
		log.debug("User {} deleted with success", user.getLogon());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Collection<SystemUser> obj) {
		log.debug("Deleting Users");
		EntityTransaction transaction = em.getTransaction();
		if(!transaction.isActive()) {
			transaction.begin();
		}
		try {
			for(SystemUser usr:obj) {
				User entity =userFactory.convert(usr);

				if (entity != null && entity.getId() == null) {
					log.debug("Requested delete on Null entity");
					continue;
				}
				if (!em.contains(entity)) {
					log.debug("Detached entity delete requested, merging");
					entity = em.merge(entity);
				}
				em.remove(entity);
			}
			//transaction.commit();
			log.info("An list of Users was deleted from the DB but not commited");
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			log.error("Error deleting Users: {}", e.getMessage());
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SystemUser getUserFromToken(String token) throws ValidationException {
		SystemTicket ticket;
		SystemUser user;
		log.debug("Get user with token:{}", token);
		try {
			ticket = ticketDAL.getByTypeAndKey(SystemUser.class.getName(), token);
		} catch (NoResultException e) {
			log.error("Error validating token: {} ,Reason: Invalid Token", token);
			throw new ValidationException("Invalid Token");
		}

		if (ticket.getExpiryDate() == null || ticket.getExpiryDate().before(new Date())) {
			log.error("Error validating token: {} ,Reason: Token expired", ticket.getToken());
			throw new ValidationException("Token Expired");
		} else {
			user = ticket.getUser();
			log.debug("USer {} found for ticket with token: {}", user.getLogon(), ticket.getToken());
		}
		return user;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SystemUser get(Long id) {
		log.debug("Get user with ID:{}", id);
		return em.find(User.class, id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SystemUser> get(List<Long> ids) {
		ArrayList<SystemUser> results = new ArrayList<>();
		if(ids == null || ids.isEmpty()){
			return results;
		}
		log.debug("Get user with IDs:{}", ids.stream().map(l->Long.toString(l)).collect(Collectors.joining(",")));
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> userRoot = criteriaQuery.from(User.class);
		criteriaQuery.select(userRoot);
		criteriaQuery.where(userRoot.get("id").in(ids));

		TypedQuery<User> q=em.createQuery(criteriaQuery);
		return new ArrayList<>(q.getResultList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SystemUser> getAll() {
		log.debug("Get All users");
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> userRoot = criteriaQuery.from(User.class);
		criteriaQuery.select(userRoot);

		TypedQuery<User> q=em.createQuery(criteriaQuery);
		return new ArrayList<>(q.getResultList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SystemUser> get(int pageSize, int pageNumber, String query) {
		if (pageNumber == 0) {
			pageNumber = 1;
		}
		if (pageSize == 0) {
			pageSize = 10;
		}
		log.debug("Get user with pageSize:{}, pagenNumber:{} and query:{}", pageSize, pageNumber, query);
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> from = criteriaQuery.from(User.class);
		CriteriaQuery<User> select = criteriaQuery.select(from);

		TypedQuery<User> typedQuery = em.createQuery(select);
		typedQuery.setFirstResult((pageNumber - 1) * pageSize);
		typedQuery.setMaxResults(pageSize);

		return new ArrayList<>(typedQuery.getResultList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SystemUser> getAllUsersByLanguage(String language) {
		log.debug("Get user with language{}", language);
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);

		select.where(builder.equal(root.get("language"), language));

		TypedQuery<User> typedQuery = em.createQuery(select);
		return new ArrayList<>(typedQuery.getResultList());
	}
}
