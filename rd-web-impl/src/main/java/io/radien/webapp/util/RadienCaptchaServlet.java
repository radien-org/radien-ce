package io.radien.webapp.util;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.GradiatedBackgroundProducer;
import cn.apiclub.captcha.servlet.CaptchaServletUtil;
import cn.apiclub.captcha.servlet.SimpleCaptchaServlet;
import cn.apiclub.captcha.text.renderer.ColoredEdgesWordRenderer;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RadienCaptchaServlet extends SimpleCaptchaServlet {
    private static final long serialVersionUID = 1L;
    private static int _width = 200;
    private static int _height = 50;
    private static final List<Color> COLORS = new ArrayList(2);
    private static final List<Font> FONTS = new ArrayList(3);

    static {
        COLORS.add(Color.BLACK);
        COLORS.add(Color.BLUE);
        FONTS.add(new Font("Geneva", 2, 48));
        FONTS.add(new Font("Courier", 1, 48));
        FONTS.add(new Font("Arial", 1, 48));
    }

    @Inject
    private CaptchaValidationHolder holder;

    public RadienCaptchaServlet() {
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (this.getInitParameter("captcha-height") != null) {
            _height = Integer.valueOf(this.getInitParameter("captcha-height"));
        }

        if (this.getInitParameter("captcha-width") != null) {
            _width = Integer.valueOf(this.getInitParameter("captcha-width"));
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ColoredEdgesWordRenderer wordRenderer = new ColoredEdgesWordRenderer(COLORS, FONTS);
        Captcha captcha = (new Captcha.Builder(_width, _height)).addText(wordRenderer).gimp().addNoise().addBackground(new GradiatedBackgroundProducer()).build();
        CaptchaServletUtil.writeImage(resp, captcha.getImage());
        req.getSession().setAttribute("simpleCaptcha", captcha);
        holder.addCaptcha(req.getParameter("uuid"), captcha);
    }
}
