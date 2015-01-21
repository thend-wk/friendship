package com.thend.friendship.cache;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractGenericPointcutAdvisor;

public class GeneralRedisCacheAdvisor extends AbstractGenericPointcutAdvisor {
	
	private static final Log logger = LogFactory.getLog(GeneralRedisCacheAdvisor.class);
	
	private static final long serialVersionUID = 25808348042290413L;

	private Set<String> filteredClass;

	public Set<String> getFilteredClass() {
		return filteredClass;
	}

	public void setFilteredClass(Set<String> filteredClass) {
		this.filteredClass = filteredClass;
	}

	private MethodMatcher methodMatcher = new MethodMatcher() {
		
		public boolean matches(Method method, Class<?> targetClass,
				Object[] args) {
			return matches(method, targetClass);
		}

		public boolean matches(Method method, Class<?> targetClass) {
			boolean matched = method.getAnnotation(GeneralCache.class) != null;
			if(matched) {
				logger.info("match method : " + method.getName());
			}
			return matched;
		}

		public boolean isRuntime() {
			return false;
		}
	};

	private ClassFilter classFilter = new ClassFilter() {
		
		public boolean matches(Class<?> clazz) {
			logger.info("to be filtered class : " + clazz.getName());
			for (String item : filteredClass) {
				item.replace(".", "\\.");
				Pattern p = Pattern.compile(item);
				Matcher m = p.matcher(clazz.getName());
				if (m.matches()) {
					return true;
				}
			}
			return false;
		}
	};

	private Pointcut pointcut = new Pointcut() {

		public MethodMatcher getMethodMatcher() {
			return methodMatcher;
		}

		public ClassFilter getClassFilter() {
			return classFilter;
		}
	};

	public Pointcut getPointcut() {
		return this.pointcut;
	}
}
