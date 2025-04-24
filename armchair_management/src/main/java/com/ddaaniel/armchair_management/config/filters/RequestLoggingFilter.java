package com.ddaaniel.armchair_management.config.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Order(1)
@Component
public class RequestLoggingFilter implements Filter {


    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    // Lista de headers sensíveis que NÃO devem ser logados
    private static final Set<String> SENSITIVE_HEADERS = Set.of("Authorization", "Cookie", "Set-Cookie",
            "X-Api-Key", "X-Forwarded-For",
            "Proxy-Authorization", "Proxy-Authenticate");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Gerando um ID único para rastreamento da requisição

        // Tenta pegar o traceId vindo do header
        String traceId = httpRequest.getHeader("X-Trace-Id");

        // Se não veio, gera um novo
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }

        // Coloca no MDC
        MDC.put("traceId", traceId);

        // Assim, quem chamou sua API também pode pegar esse ID e usar pra debug ou exibir em UI.
        httpResponse.setHeader("X-Trace-Id", traceId);


        // Obtendo informações da requisição
        String clientIp = maskIp(httpRequest.getRemoteAddr());
        String method = httpRequest.getMethod();
        String requestUriSanitized = httpRequest.getRequestURI()
                .replaceAll("/\\d+", "/{id}")
                .split("\\?")[0]; // Remove query parameters

        // Captura os headers, omitindo os sensíveis
        Map<String, String> headers = getFilteredHeaders(httpRequest);

        // Limita a quantidade de headers logados (máximo de 5)
        if (headers.size() > 5) {
            headers = headers.entrySet().stream()
                    .limit(5)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        try {
            logger.info("Request - IP: {}, Method: {}, URI: {}, Headers: {}", clientIp, method, requestUriSanitized, headers);

            // Executa a requisição
            chain.doFilter(request, response);

            // Log de resposta
            logger.info("Response - Status: {}", httpResponse.getStatus());

        } finally {
            MDC.clear(); // Remove o contexto da thread após a requisição
        }
    }

    // Filtra headers sensíveis antes de logar
    private Map<String, String> getFilteredHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!SENSITIVE_HEADERS.contains(headerName) && !"host".equalsIgnoreCase(headerName)) {
                headers.put(headerName, request.getHeader(headerName));
            }
        }

        // Máscara no X-Forwarded-For, se presente
        if (headers.containsKey("X-Forwarded-For")) {
            headers.put("X-Forwarded-For", maskIp(headers.get("X-Forwarded-For")));
        }

        return headers;
    }

    // Método para mascarar parcialmente o IP (IPv4 e IPv6)
    private String maskIp(String ip) {
        if (ip == null || ip.isBlank()) return "UNKNOWN";

        if (ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) { // IPv4
            return ip.replaceAll("(\\d+\\.\\d+)\\.\\d+\\.\\d+", "$1.***.***");
        } else if (ip.contains(":")) { // IPv6
            return ip.replaceAll("([a-fA-F0-9]{4}):([a-fA-F0-9]{4}):.*", "$1:$2:****:****");
        }

        return ip;
    }
}
