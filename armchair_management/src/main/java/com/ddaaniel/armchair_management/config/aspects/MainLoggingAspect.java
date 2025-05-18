package com.ddaaniel.armchair_management.config.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class MainLoggingAspect {

    private static final Logger mainLogger = LoggerFactory.getLogger(MainLoggingAspect.class);

    @Pointcut("execution(* com.ddaaniel.armchair_management.controller.service.*.*(..))")
    public void serviceMethods(){}

    @Around("execution(* com.ddaaniel.armchair_management.controller.SeatController.*(..))")
    public Object logAuditController(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        mainLogger.info("- Request: {}, Args: {}",
                proceedingJoinPoint.getSignature().toString(), Arrays.toString(proceedingJoinPoint.getArgs()));

        Object action = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis() - startTime;

        mainLogger.info("- Response from {} completed in {}ms",
                proceedingJoinPoint.getSignature().toString(), endTime);

        return action;
    }

    @Around("serviceMethods()")
    public Object logAuditService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        mainLogger.info("- Service method started: {}, Args: {}",
                proceedingJoinPoint.getSignature().toString(), Arrays.toString(proceedingJoinPoint.getArgs()));

        Object action;
        try {
            action = proceedingJoinPoint.proceed();
        } catch (Exception ex){
            mainLogger.error("- Exception in Service method: {} - Error: {}",
                    proceedingJoinPoint.getSignature().toString(), ex.getMessage(), ex);
            throw ex;
        }

        long endTime = System.currentTimeMillis() - startTime;
        mainLogger.info("- Service method completed: {} in {}ms.",
                proceedingJoinPoint.getSignature().toString(), endTime);
        return action;
    }

    @Around("execution(* com.ddaaniel.armchair_management.model.repository.*.*(..))")
    public Object logAuditRepository(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        mainLogger.info("- Query started: {} with args: {}",
                proceedingJoinPoint.getSignature().toString(), Arrays.toString(proceedingJoinPoint.getArgs()));

        Object action = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis() - startTime;

        mainLogger.info("- Query completed: {}, Execution time: {}ms.",
                proceedingJoinPoint.getSignature().toString(), endTime);
        return action;
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logAuditServiceExceptions(JoinPoint joinPoint, Exception ex) {
        mainLogger.error("- Exception in method {} - Error: {}",
                joinPoint.getSignature().toString(), ex.getMessage(), ex);
    }
}
