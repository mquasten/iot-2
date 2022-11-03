package de.mq.iot2.main;

import java.io.Serializable;
import java.lang.reflect.Method;

public class ReflectionCommandLineRunnerArguments implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Class<?> executedBean;
	
	private String methodName;
	
	private Class<?>[] parameterTypes; 
	
	private Object[] parameterValues;
	
	public ReflectionCommandLineRunnerArguments(){
		
	}
	
	public ReflectionCommandLineRunnerArguments(final Method method, final Object[] parameterValues ){
		executedBean=method.getDeclaringClass();
		methodName=method.getName();
		parameterTypes=method.getParameterTypes();
		this.parameterValues=parameterValues;
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
