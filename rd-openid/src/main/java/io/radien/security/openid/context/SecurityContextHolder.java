package io.radien.security.openid.context;

public class SecurityContextHolder {
    private static final ThreadLocal<SecurityContext> contextHolder = new ThreadLocal<>();

    public static void clearContext() {
        contextHolder.remove();
    }

    public static SecurityContext getContext() {
        SecurityContext ctx = contextHolder.get();
        if (ctx == null) {
            ctx = createEmptyContext();
            contextHolder.set(ctx);
        }
        return ctx;
    }

    protected static SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }

}
