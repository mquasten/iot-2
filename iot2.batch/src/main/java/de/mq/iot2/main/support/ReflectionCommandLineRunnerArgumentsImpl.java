package de.mq.iot2.main.support;

import java.io.Serializable;
import java.lang.reflect.Method;

public class ReflectionCommandLineRunnerArgumentsImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private Class<?> executedBean;

	private String methodName;

	private Class<?>[] parameterTypes;

	private Object[] parameterValues;

	public ReflectionCommandLineRunnerArgumentsImpl() {

	}

	public ReflectionCommandLineRunnerArgumentsImpl(final Method method, final Object[] parameterValues) {
		parameterLengthGuard(method, parameterValues);
		executedBean = method.getDeclaringClass();
		methodName = method.getName();
		parameterTypes = method.getParameterTypes();
		this.parameterValues = parameterValues;

	}

	private void parameterLengthGuard(final Method method, final Object[] parameterValues) {
		if (method.getParameterCount() != parameterValues.length) {
			throw new IllegalStateException(String.format("Number of Parameters in %s and number of parameters are different.", method.getName()));
		}
	}

	public final Class<?> getExecutedBean() {
		return executedBean;
	}

	public final void setExecutedBean(Class<?> executedBean) {
		this.executedBean = executedBean;
	}

	public final String getMethodName() {
		return methodName;
	}

	public final void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public final Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public final void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public final Object[] getParameterValues() {
		return parameterValues;
	}

	public final void setParameterValues(Object[] parameterValues) {
		this.parameterValues = parameterValues;
	}

}
