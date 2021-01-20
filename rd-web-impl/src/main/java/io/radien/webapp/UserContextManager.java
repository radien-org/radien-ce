package io.radien.webapp;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.webapp.security.UserSession;

/**
 * Class responsible for managing the current user context and all other
 * available contexts in this session.
 *
 * @author Rafael Fernandes
 * @author Marco Weiland
 * @author Nuno Santana
 * @author Mario Ribiero
 */
public @Model @SessionScoped class UserContextManager implements Serializable {

	private static final long serialVersionUID = -2201323833442517194L;
	private static final Logger log = LoggerFactory.getLogger(UserContextManager.class);
//	@Inject
//	protected ContextServiceAccess contextService;
//	@Inject
//	private ApplicationServiceAccess applicationService;
	@Inject
	private UserSession userSession;

//	private UserContext userContext;
//
//	private SystemContext activeContext = null;

	private boolean initialized;

	@PostConstruct
	public void init() {
		initialized = false;
		log.info("End of UserContextManager Init");
	}

	private void lazyInit(){
//		try {
//			log.info("UserContextManager lazy Init");
//			SystemUser user = userSession.getUser();
//			if (user != null) {
//				SystemContext userContext = userSession.getCurrentContext();
//				if (userContext == null) {
//					log.info("Initializing working context for {}", user.getLogon());
//					loadContext(null);
//				} else {
//					log.info("Loading working context {} for {}", userContext.getContract(), user.getLogon());
//					loadContext(userSession.getCurrentContext().getContract().getKey());
//				}
//			}
//		} catch (Exception e) {
//			log.error("Error on initialization", e);
//		}
//		initialized = true;
	}

	public void reload() {
		lazyInit();
	}

	public void loadContext(String key) {
//		try {
//			log.info("start init context {}", key);
//
//			initUserContext(key);
//
//			String msg="No Active Context";
//			if(activeContext!=null){
//				msg = "No Active Contract";
//				if(activeContext.getContract()!=null){
//					msg = activeContext.getContract().getKey();
//				}
//			}
//			log.info("context initialized {}", msg);
//		} catch (Exception e) {
//			log.error("problem initializing context {}", key);
//			log.error("problem initializing context", e);
//		}
//
//		try {
//			log.info("start setting sessioncontext {}", key);
//			userSession.setCurrentContext(activeContext);
//			log.info("set up sessioncontext {}", key);
//		} catch (Exception e) {
//			log.error("problem setting context {}", key);
//			log.error("problem setting context", e);
//		}
	}

	//TODO: this has to be totally refactored, not save of active context anymore, maintain context ONLY in session
	private void initUserContext(String key) {
//		userContext = null;
//		if ( activeContext!= null && !activeContext.getContract().getKey().equalsIgnoreCase(key) ) {
//			activeContext = null;
//		}
//		List<SystemContext> actualUserContexts = new ArrayList<>();
//		List<SystemContext> newUserContexts = new ArrayList<>();
//		try {
//			actualUserContexts = contextService.getSystemContextsByUserIdAndAccepted(userSession.getUser().getId(), true);
//
//			if (key != null && !key.isEmpty()) {
//				for (SystemContext context : actualUserContexts) {
//					if (context.getContract().getKey() != null && context.getContract().getKey().equals(key)) {
//						activeContext = context;
//			            log.info("set active context to {}", context);
//					} else if (context.isCurrent()) {
//						context.setCurrent(false);
//			            log.info("start init+save context {}", context);
//					}
//					newUserContexts.add(context);
//				}
//			} else {
//				for (SystemContext context : actualUserContexts) {
//					if (context.isDefaultContext()) {
//						activeContext = context;
//			            log.info("context is default context {}", context);
//					} else {
//						context.setCurrent(false);
//					}
//					newUserContexts.add(context);
//				}
//			}
//
//			if (!actualUserContexts.isEmpty() && activeContext == null) {
//                log.info("active context is still empty, setting to contexts(0)");
//				activeContext = actualUserContexts.get(0);
//                log.info("active context is now {}", activeContext);
//			}
//
//			if (activeContext != null) {
//				if (!activeContext.isCurrent()) {
//					activeContext.setCurrent(true);
//				}
//			}
//			userContext = new UserContext(newUserContexts);
//			loadAvailableApplicationsForCurrentContext();
//		} catch (Exception e) {
//			log.error("error on initialization", e);
//		}
	}

//	private void loadCurrentContext() {
//		final List<SystemContext> contexts = contextService.getSystemContextsByUserIdAndAccepted(userSession.getUser().getId(), true);
//		List<SystemContext> newUserContexts = new ArrayList<>();
//		for (SystemContext context : contexts) {
//			if (activeContext!= null && context.getContract().getKey().equalsIgnoreCase(activeContext.getContract().getKey())) {
//				context.setCurrent(true);
//			}else {
//				context.setCurrent(false);
//			}
//			newUserContexts.add(context);
//		}
//		userContext = new UserContext(newUserContexts);
//		loadAvailableApplicationsForCurrentContext();
//	}
//
//	private void loadAvailableApplicationsForCurrentContext() {
//		if (activeContext != null) {
//			log.info("Loading available applications");
//	 			List<SystemApplication> applications = applicationService.getAvailableApplicationsByContext(activeContext);
//			userContext.setAvailableApplications(applications);
//			log.info("Loaded available applications");
//		}
//	}
//
//	public UserContext getUserContext() {
//		if ( !initialized ) {
//			lazyInit();
//		}
//		loadCurrentContext();
//		return userContext;
//	}
//
//	public UserContext getCachedUserContext() {
//		if ( !initialized ) {
//			lazyInit();
//		}
//		return userContext;
//	}

}
