package com.ddaaniel.armchair_management.config.filters.wrapper;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream cachedBody;
    private final ServletOutputStream outputStream;
    private final PrintWriter writer;

    public CachedBodyHttpServletResponse(HttpServletResponse response) {
        super(response);
        this.cachedBody = new ByteArrayOutputStream();

        this.outputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener listener) {
                // Implementação vazia, pois não estamos utilizando async I/O
            }

            @Override
            public void write(int b) throws IOException {
                cachedBody.write(b);
            }
        };

        this.writer = new PrintWriter(cachedBody, true, StandardCharsets.UTF_8);
    }

    public String getBody() {
        return cachedBody.toString(StandardCharsets.UTF_8);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

//    @Override
//    public void flushBuffer() throws IOException {
//        writer.flush();
//        outputStream.flush();
//
//        // Escrever o conteúdo capturado no response original
//        byte[] responseBody = cachedBody.toByteArray();
//        super.getResponse().getOutputStream().write(responseBody);
//        super.getResponse().getOutputStream().flush();
//
//        super.flushBuffer();
//    }

    /**
     * Este método escreve o conteúdo capturado de volta na resposta real.
     * Chame no `finally` do filtro para garantir que a resposta seja enviada.
     */
    public void copyBodyToResponse() throws IOException {
        HttpServletResponse response = (HttpServletResponse) getResponse();

        // Escreve o conteúdo armazenado no stream real
        ServletOutputStream out = response.getOutputStream();
        out.write(cachedBody.toByteArray());
        out.flush();
    }
}