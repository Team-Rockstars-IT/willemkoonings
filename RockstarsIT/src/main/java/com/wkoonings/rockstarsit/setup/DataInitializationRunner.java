package com.wkoonings.rockstarsit.setup;

import com.wkoonings.rockstarsit.service.DataInitializationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.data.init.enabled", havingValue = "true", matchIfMissing = true)
@Order(2)
public class DataInitializationRunner implements ApplicationRunner {

  private final DataInitializationService dataInitializationService;

  @Override
  public void run(final ApplicationArguments args) {
    log.info("Starting application data initialization...");

    try {
      //      dataInitializationService.initializeData();
      log.info("Application data initialization completed successfully!");
    } catch (Exception e) {
      log.error("Failed to initialize application data", e);
    }
  }
}
