package io.radien.webapp.util;

import cn.apiclub.captcha.Captcha;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CaptchaValidationHolder {
    private Map<String, Captcha> captchaMap;

    @PostConstruct
    public void init() {
        captchaMap = new HashMap<>();
    }

    public Captcha getCaptcha(String uuid) {
        return captchaMap.remove(uuid);
    }

    public void addCaptcha(String uuid, Captcha captcha) {
        captchaMap.put(uuid, captcha);
    }
}
