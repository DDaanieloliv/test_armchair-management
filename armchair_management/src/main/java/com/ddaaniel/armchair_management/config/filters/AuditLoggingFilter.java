package com.ddaaniel.armchair_management.config.filters;

import com.ddaaniel.armchair_management.config.filters.wrapper.CachedBodyHttpServletRequest;
import com.ddaaniel.armchair_management.config.filters.wrapper.CachedBodyHttpServletResponse;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Order(2)
@Component
public class AuditLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger("AUDIT");

    private final LogSanitizer logSanitizer = new LogSanitizer();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest httpRequest) || !(response instanceof HttpServletResponse httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        // Gerando um ID único para rastreamento da auditoria
        //String auditId = UUID.randomUUID().toString();
        //MDC.put("auditId", auditId);

        // Capturando informações da requisição
        String clientIp = httpRequest.getRemoteAddr();
        String method = httpRequest.getMethod();
        String requestUri = httpRequest.getRequestURI();
        Map<String, String> headers = getHeaders(httpRequest);

        // Criar um wrapper da requisição para armazenar o corpo
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpRequest);
        String requestBody = wrappedRequest.getBody();
        String maskedRequestBody = logSanitizer.sanitizeJson(requestBody);

        // Criar um wrapper da resposta para capturar o corpo
        CachedBodyHttpServletResponse wrappedResponse = new CachedBodyHttpServletResponse(httpResponse);

        try {
            // Log da requisição
            logger.info("AUDIT - Request - IP: {}, Method: {}, URI: {}, Headers: {}, Body: {}",
                     clientIp, method, requestUri, headers, maskedRequestBody);

            // Executa a requisição com os wrappers
            chain.doFilter(wrappedRequest, wrappedResponse);

            // Captura o payload da resposta
            String responseBody = wrappedResponse.getBody();
            int status = wrappedResponse.getStatus();
            String maskedResponseBody = logSanitizer.sanitizeJson(responseBody);

            // Log da resposta
            logger.info("AUDIT - Response - Status: {}, Body: {}",  status, maskedResponseBody);

            // Reescreve a resposta original com o conteúdo do wrapper
            wrappedResponse.copyBodyToResponse();

        } finally {
            //MDC.clear(); // Remove o contexto da thread ao finalizar a requisição
        }
    }

    // Método para capturar todos os headers da requisição
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }

        return headers;
    }
}
