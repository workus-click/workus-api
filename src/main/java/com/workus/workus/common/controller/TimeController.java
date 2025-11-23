package com.workus.workus.common.controller;

import java.time.ZonedDateTime;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class TimeController {

	@GetMapping("/time")
	public Map<String, String> getServerTime() {
		ZonedDateTime now = ZonedDateTime.now();
        log.info(now.toString());
        log.info(now.toString());
        log.info(now.toString());
        log.info(now.toString());
        log.info(now.toString());
		return Map.of("serverTime", now.toString());
	}
}
