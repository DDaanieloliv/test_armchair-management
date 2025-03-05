package com.ddaaniel.armchair_management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class Config implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Garantindo que sempre que executarmos esta solução seja inserido os 15 registro
    // caso não existam.
    @Override
    public void run(String... args) throws Exception {
        for (int position = 1; position <= 15; position++) {
            int count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM tb_seats WHERE position = ?", Integer.class, position
            );

            if (count == 0) {
                jdbcTemplate.update("INSERT INTO tb_seats (position, free) VALUES (?, true);", position);
            }
        }
    }
}
