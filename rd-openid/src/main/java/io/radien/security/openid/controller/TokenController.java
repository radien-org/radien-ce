package io.radien.security.openid.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TokenController {

	@PostMapping(value = "/oauth/token/revoke")
	public @ResponseBody void create(@RequestParam("token") String value) {
		throw new UnsupportedOperationException();
	}
}