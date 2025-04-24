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
public class AuditLoggingAspect {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    @Pointcut("execution(* com.ddaaniel.armchair_management.controller.service.*.*(..))")
    public void serviceMethods(){}

    @Around("execution(* com.ddaaniel.armchair_management.controller.SeatController.*(..))")
    public Object logAuditController(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        auditLogger.info("AUDIT - Request: {}, Args: {}",
                proceedingJoinPoint.getSignature().toString(), Arrays.toString(proceedingJoinPoint.getArgs()));

        Object action = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis() - startTime;

        auditLogger.info("AUDIT - Response from {} completed in {}ms",
                proceedingJoinPoint.getSignature().toString(), endTime);

        return action;
    }

    @Around("serviceMethods()")
    public Object logAuditService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        auditLogger.info("AUDIT - Service method started: {}, Args: {}",
                proceedingJoinPoint.getSignature().toString(), Arrays.toString(proceedingJoinPoint.getArgs()));

        Object action;
        try {
            action = proceedingJoinPoint.proceed();
        } catch (Exception ex){
            auditLogger.error("AUDIT - Exception in Service method: {} - Error: {}",
                    proceedingJoinPoint.getSignature().toString(), ex.getMessage(), ex);
            throw ex;
        }

        long endTime = System.currentTimeMillis() - startTime;
        auditLogger.info("AUDIT - Service method completed: {} in {}ms.",
                proceedingJoinPoint.getSignature().toString(), endTime);
        return action;
    }

    @Around("execution(* com.ddaaniel.armchair_management.model.repository.*.*(..))")
    public Object logAuditRepository(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        auditLogger.info("AUDIT - Query started: {} with args: {}",
                proceedingJoinPoint.getSignature().toString(), Arrays.toString(proceedingJoinPoint.getArgs()));

        Object action = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis() - startTime;

        auditLogger.info("AUDIT - Query completed: {}, Execution time: {}ms.",
                proceedingJoinPoint.getSignature().toString(), endTime);
        return action;
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logAuditServiceExceptions(JoinPoint joinPoint, Exception ex) {
        auditLogger.error("AUDIT - Exception in method {} - Error: {}",
                joinPoint.getSignature().toString(), ex.getMessage(), ex);
    }
}
