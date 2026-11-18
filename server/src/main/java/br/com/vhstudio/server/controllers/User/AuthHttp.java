package br.com.vhstudio.server.controllers.User;

import br.com.vhstudio.server.services.User.Auth.ClientInfo;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;

final class AuthHttp {
    private AuthHttp() {}

    static ClientInfo client(HttpServletRequest request) {
        return new ClientInfo(request.getRemoteAddr(), request.getHeader(HttpHeaders.USER_AGENT));
    }
}
