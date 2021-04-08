package io.radien.ms.openid.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AuthenticationFilterTest {

    private final String accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MTQ2ODAwOTMsImlhdCI6MTYxNDY3OTc5MywianRpIjoiODEyYzU2NzAtNzM4Ni00MDY1LTkxZmQtZGRiNzQ2OTZmYTU3IiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZWM0NDE2YjMtOWY3YS00YWUxLTg5ZjUtZGZkMDlhNDRlZThkIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImQ3Y2EzN2Y1LTM3YTgtNDQ1My05MzdkLTkzMmE1MDE2MzI3MyIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiTnVubyAgU2FudGFuYSIsInByZWZlcnJlZF91c2VybmFtZSI6Im4uc2FudGFuYS11c2VybmFtZSIsImdpdmVuX25hbWUiOiJOdW5vICIsImZhbWlseV9uYW1lIjoiU2FudGFuYSIsImVtYWlsIjoibi5zYW50YW5hQHJhZGllbi5pbyJ9.x37IRsQWE-NjIwcK-wzZc9rj_nGDBo7FWt7Kj_agzhpNx0lbMy6LcrklZJ6oIBb-rW6YpfIbqBP17hrK27ZNyAB2Tb3-jADSg8WIkTpbjQ16h3Zn4_Hn2GQIHrEOryAz7DZhkGj6MFZEVuebwbHmPh5MqX8pwNl8bpwS63zxa6YK9q1ny_hLrzgrsJWERgo6TsOKFle3asP8LzlGt3YI4_ePd3MSXDv-Z627bD-9PrAdpJqDujcDca58DXukoVE-zigD1-AAI2E85Ala-7kCDdR_KlBKk6frATfqpgZ5QrzHir0h_qRs9UGwDPOF1iv9fCyvVyBy-_VbzMWxqZCksQ";

    @InjectMocks
    AuthenticationFilter target;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testDoFilter() throws ServletException, IOException {
        target.init(null);
        ServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletRequest req = (HttpServletRequest) request;
        ServletResponse response = Mockito.mock(HttpServletResponse.class);
        HttpServletResponse resp = (HttpServletResponse) response;
        FilterChain chain = Mockito.mock(FilterChain.class);
        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer "+accessToken);
        when(req.getRequestURI()).thenReturn("a");
        PrintWriter printWriter = Mockito.mock(PrintWriter.class);
        when(resp.getWriter()).thenReturn(printWriter);
        target.doFilter(request,response,chain);
        //Mockito.verify(chain).doFilter(request, response);
        Mockito.verify(printWriter).write("Failed authentication..");
        target.destroy();
    }

}