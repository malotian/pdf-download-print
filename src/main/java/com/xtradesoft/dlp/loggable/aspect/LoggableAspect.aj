/**
 * Copyright (c) 2014 xTradesoft Gmbh. All rights reserved.
 */

package com.xtradesoft.dlp.loggable.aspect;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xtradesoft.dlp.loggable.annotation.Loggable;
import com.xtradesoft.dlp.loggable.logger.LevelLoggerFactory;
import com.xtradesoft.dlp.loggable.logger.LoggableLogger;

/**
 * The Class LoggableAspect.
 */
@Aspect
public class LoggableAspect {

  /**
   * The Class MethodParameter.
   */
  public class MethodParameter {

    /** The name. */
    private final String _name;

    /** The object. */
    private final Object _object;

    /** The type. */
    private final Class<?> _type;

    /**
     * Instantiates a new MethodParameter.
     *
     * @param object
     *            the object
     * @param type
     *            the type
     * @param name
     *            the name
     */
    public MethodParameter(Object object, Class<?> type, String name) {

      super();
      _object = object;
      _type = type;
      _name = name;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {

      return _name;
    }

    /**
     * Gets the object.
     *
     * @return the object
     */
    public Object getObject() {

      return _object;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public Class<?> getType() {

      return _type;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

      return _name + "=" + _object;
    }

  }

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(LoggableAspect.class);

  /**
   * Any method.
   */
  @Pointcut("execution(* *(..))")
  public void anyMethod() {

  }

  /**
   * Creates the parameter.
   *
   * @param signature
   *            the signature
   * @param objects
   *            the objects
   * @return the list
   */
  private List<MethodParameter> createParameter(Signature signature, Object[] objects) {

    final List<MethodParameter> params = new ArrayList<MethodParameter>();
    if (!(signature instanceof CodeSignature)) {
      return params;
    }

    final CodeSignature codeSignature = (CodeSignature) signature;
    for (int i = 0; i < objects.length; i++) {
      params.add(new MethodParameter(objects[i], codeSignature.getParameterTypes()[i], codeSignature
              .getParameterNames()[i]));
    }

    return params;

  }

  /**
   * Donot log annotation.
   */
  @Pointcut("!@annotation(com.xtradesoft.dlp.loggable.annotation.Loggable)")
  public void donotLogAnnotation() {

  }

  /**
   * Checks if is void.
   *
   * @param signature
   *            the signature
   * @return true, if is void
   */
  private boolean isVoid(Signature signature) {

    if (!(signature instanceof MethodSignature)) {
      return false;
    }

    final MethodSignature methodSignature = (MethodSignature) signature;
    return "void".equals(methodSignature.getReturnType().toString());
  }

  /**
   * Log.
   *
   * @param joinPoint
   *            the join point
   * @param loggable
   *            the loggable
   * @return the object
   * @throws Throwable
   *             the throwable
   */
  @Around("anyMethod() && @within(loggable) && donotLogAnnotation()")
  public Object log(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {

    return logMethod(joinPoint, loggable);
  }

  /**
   * Log join point.
   *
   * @param joinPoint
   *            the join point
   * @param loggable
   *            the loggable
   * @return the object
   * @throws Throwable
   *             the throwable
   */
  private Object logJoinPoint(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {

    final Signature signature = joinPoint.getSignature();
    final String methodName = signature.getName();
    if (logger.isTraceEnabled()) {
      logger.trace("pre-entry, Method [{}]", methodName);
    }
    final Class<?> type = joinPoint.getTarget().getClass();
    final LoggableLogger context = LevelLoggerFactory.create(loggable.level(), type);
    Object returnValue = null;
    boolean exceptionThrown = false;
    try {
      if (loggable.logEntryExit()) {
        final List<MethodParameter> methodParameter = createParameter(signature, joinPoint.getArgs());
        if (!loggable.skipArguments() && !methodParameter.isEmpty()) {
          context.log("entry, Method [{}] with Parameter={}", methodName, methodParameter);
        } else {
          context.log("entry, [{}]", methodName);
        }
      }
      returnValue = joinPoint.proceed();
      return returnValue;
    } catch (final Throwable t) {
      exceptionThrown = true;
      if (logger.isTraceEnabled()) {
        logger.trace("Method [{}] throws Exception [{}]", methodName, t);
      }
      if (loggable.logError()) {
        final LoggableLogger errorLogger = LevelLoggerFactory.create(loggable.errorLevel(), type);
        errorLogger.log("Method [{}] throws unexpected error", methodName, t);
      }
      throw t;
    } finally {
      if (loggable.logEntryExit()) {
        if (!isVoid(signature) && !exceptionThrown && !loggable.skipResults()) {
          context.log("exit, Method [{}] return=[{}]", methodName, returnValue);
        } else {
          context.log("exit, [{}]", methodName);
        }
      }
      if (logger.isTraceEnabled()) {
        logger.trace("post-exit, Method [{}]", methodName);
      }
    }
  }

  /**
   * Log method.
   *
   * @param joinPoint
   *            the join point
   * @param loggable
   *            the loggable
   * @return the object
   * @throws Throwable
   *             the throwable
   */
  @Around("anyMethod() && @annotation(loggable)")
  public Object logMethod(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {

    return logJoinPoint(joinPoint, loggable);
  }

}
