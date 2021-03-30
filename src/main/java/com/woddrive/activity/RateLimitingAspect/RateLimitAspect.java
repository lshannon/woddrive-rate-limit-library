package com.woddrive.activity.RateLimitingAspect;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
public class RateLimitAspect {

	private final RateService rateService;
	private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";

	public RateLimitAspect(RateService rateService) {
		this.rateService = rateService;
	}

	@Around("within(@org.springframework.web.bind.annotation.RestController *)")
	public Object checkAuthorization(ProceedingJoinPoint pjp) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		// if this is a Swagger user, no need to manage activity
		if (request.getRequestURI().equals("/") || request.getRequestURI().equals("/swagger-ui.html")) {
			return pjp.proceed();
		}
		if (isBanned(request)) {
			return ResponseEntity.ok(rateService.getBannedResponse());
		}
		return pjp.proceed();
	}

	private Boolean isBanned(HttpServletRequest request) {
		String ipAddress = getIpFromRequest(request);
		if (ipAddress == null) {
			log.error("Unable to get the IP for {} banning this request just in case", request.toString());
			return Boolean.TRUE;
		}
		return rateService.isBanned(ipAddress);
	}
	
	private String getIpFromRequest(HttpServletRequest request) {
		String ipAddress = request.getHeader(X_FORWARDED_FOR);
		// is client behind something?
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}

}
