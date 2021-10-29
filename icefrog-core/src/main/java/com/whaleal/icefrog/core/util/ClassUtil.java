package com.whaleal.icefrog.core.util;

import com.whaleal.icefrog.core.bean.NullWrapperBean;
import com.whaleal.icefrog.core.convert.BasicType;
import com.whaleal.icefrog.core.exceptions.UtilException;
import com.whaleal.icefrog.core.io.FileUtil;
import com.whaleal.icefrog.core.io.IORuntimeException;
import com.whaleal.icefrog.core.io.resource.ResourceUtil;
import com.whaleal.icefrog.core.lang.Preconditions;
import com.whaleal.icefrog.core.lang.ClassScanner;
import com.whaleal.icefrog.core.lang.Predicate;
import com.whaleal.icefrog.core.lang.Singleton;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URI;
import java.net.URL;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import static com.whaleal.icefrog.core.collection.CollUtil.createHashMap;
import static com.whaleal.icefrog.core.collection.CollUtil.createHashSet;
import static com.whaleal.icefrog.core.util.ArrayUtil.*;

/**
 * 类工具类 <br>
 *
 * @author Looly
 * @author wh
 */
public class ClassUtil {

	/**
	 * {@code null}安全的获取对象类型
	 *
	 * @param <T> 对象类型
	 * @param obj 对象，如果为{@code null} 返回{@code null}
	 * @return 对象类型，提供对象如果为{@code null} 返回{@code null}
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClass(T obj) {
		return ((null == obj) ? null : (Class<T>) obj.getClass());
	}

	/**
	 * 获得外围类<br>
	 * 返回定义此类或匿名类所在的类，如果类本身是在包中定义的，返回{@code null}
	 *
	 * @param clazz 类
	 * @return 外围类
	 * @since 1.0.0
	 */
	public static Class<?> getEnclosingClass(Class<?> clazz) {
		return null == clazz ? null : clazz.getEnclosingClass();
	}

	/**
	 * 是否为顶层类，即定义在包中的类，而非定义在类中的内部类
	 *
	 * @param clazz 类
	 * @return 是否为顶层类
	 * @since 1.0.0
	 */
	public static boolean isTopLevelClass(Class<?> clazz) {
		if (null == clazz) {
			return false;
		}
		return null == getEnclosingClass(clazz);
	}

	/**
	 * 获取类名
	 *
	 * @param obj      获取类名对象
	 * @param isSimple 是否简单类名，如果为true，返回不带包名的类名
	 * @return 类名
	 * @since 1.0.0
	 */
	public static String getClassName(Object obj, boolean isSimple) {
		if (null == obj) {
			return null;
		}
		final Class<?> clazz = obj.getClass();
		return getClassName(clazz, isSimple);
	}

	/**
	 * 获取类名<br>
	 * 类名并不包含“.class”这个扩展名<br>
	 * 例如：ClassUtil这个类<br>
	 *
	
	 * isSimple为false: "com.xiaoleilu.icefrog.util.ClassUtil"
	 * isSimple为true: "ClassUtil"
	
	 *
	 * @param clazz    类
	 * @param isSimple 是否简单类名，如果为true，返回不带包名的类名
	 * @return 类名
	 * @since 1.0.0
	 */
	public static String getClassName(Class<?> clazz, boolean isSimple) {
		if (null == clazz) {
			return null;
		}
		return isSimple ? clazz.getSimpleName() : clazz.getName();
	}

	/**
	 * 获取完整类名的短格式如：<br>
	 * StrUtil -》c.h.c.u.StrUtil
	 *
	 * @param className 类名
	 * @return 短格式类名
	 * @since 1.0.0
	 */
	public static String getShortClassName(String className) {
		final List<String> packages = StrUtil.split(className, CharUtil.DOT);
		if (null == packages || packages.size() < 2) {
			return className;
		}

		final int size = packages.size();
		final StringBuilder result = StrUtil.builder();
		result.append(packages.get(0).charAt(0));
		for (int i = 1; i < size - 1; i++) {
			result.append(CharUtil.DOT).append(packages.get(i).charAt(0));
		}
		result.append(CharUtil.DOT).append(packages.get(size - 1));
		return result.toString();
	}

	/**
	 * 获得对象数组的类数组
	 *
	 * @param objects 对象数组，如果数组中存在{@code null}元素，则此元素被认为是Object类型
	 * @return 类数组
	 */
	public static Class<?>[] getClasses(Object... objects) {
		Class<?>[] classes = new Class<?>[objects.length];
		Object obj;
		for (int i = 0; i < objects.length; i++) {
			obj = objects[i];
			if (obj instanceof NullWrapperBean) {
				// 自定义null值的参数类型
				classes[i] = ((NullWrapperBean<?>) obj).getWrappedClass();
			} else if (null == obj) {
				classes[i] = Object.class;
			} else {
				classes[i] = obj.getClass();
			}
		}
		return classes;
	}

	/**
	 * 指定类是否与给定的类名相同
	 *
	 * @param clazz      类
	 * @param className  类名，可以是全类名（包含包名），也可以是简单类名（不包含包名）
	 * @param ignoreCase 是否忽略大小写
	 * @return 指定类是否与给定的类名相同
	 * @since 1.0.0
	 */
	public static boolean equals(Class<?> clazz, String className, boolean ignoreCase) {
		if (null == clazz || StrUtil.isBlank(className)) {
			return false;
		}
		if (ignoreCase) {
			return className.equalsIgnoreCase(clazz.getName()) || className.equalsIgnoreCase(clazz.getSimpleName());
		} else {
			return className.equals(clazz.getName()) || className.equals(clazz.getSimpleName());
		}
	}

	// ----------------------------------------------------------------------------------------- Scan classes

	/**
	 * 扫描指定包路径下所有包含指定注解的类
	 *
	 * @param packageName     包路径
	 * @param annotationClass 注解类
	 * @return 类集合
	 * @see ClassScanner#scanPackageByAnnotation(String, Class)
	 */
	public static Set<Class<?>> scanPackageByAnnotation(String packageName, final Class<? extends Annotation> annotationClass) {
		return ClassScanner.scanPackageByAnnotation(packageName, annotationClass);
	}

	/**
	 * 扫描指定包路径下所有指定类或接口的子类或实现类
	 *
	 * @param packageName 包路径
	 * @param superClass  父类或接口
	 * @return 类集合
	 * @see ClassScanner#scanPackageBySuper(String, Class)
	 */
	public static Set<Class<?>> scanPackageBySuper(String packageName, final Class<?> superClass) {
		return ClassScanner.scanPackageBySuper(packageName, superClass);
	}

	/**
	 * 扫面该包路径下所有class文件
	 *
	 * @return 类集合
	 * @see ClassScanner#scanPackage()
	 */
	public static Set<Class<?>> scanPackage() {
		return ClassScanner.scanPackage();
	}

	/**
	 * 扫面该包路径下所有class文件
	 *
	 * @param packageName 包路径 com | com. | com.abs | com.abs.
	 * @return 类集合
	 * @see ClassScanner#scanPackage(String)
	 */
	public static Set<Class<?>> scanPackage(String packageName) {
		return ClassScanner.scanPackage(packageName);
	}

	/**
	 * 扫面包路径下满足class过滤器条件的所有class文件，<br>
	 * 如果包路径为 com.abs + A.class 但是输入 abs会产生classNotFoundException<br>
	 * 因为className 应该为 com.abs.A 现在却成为abs.A,此工具类对该异常进行忽略处理,有可能是一个不完善的地方，以后需要进行修改<br>
	 *
	 * @param packageName 包路径 com | com. | com.abs | com.abs.
	 * @param classPredicate class过滤器，过滤掉不需要的class
	 * @return 类集合
	 */
	public static Set<Class<?>> scanPackage(String packageName, Predicate<Class<?>> classPredicate) {
		return ClassScanner.scanPackage(packageName, classPredicate);
	}

	// ----------------------------------------------------------------------------------------- Method

	/**
	 * 获得指定类中的Public方法名<br>
	 * 去重重载的方法
	 *
	 * @param clazz 类
	 * @return 方法名Set
	 */
	public static Set<String> getPublicMethodNames(Class<?> clazz) {
		return ReflectUtil.getPublicMethodNames(clazz);
	}

	/**
	 * 获得本类及其父类所有Public方法
	 *
	 * @param clazz 查找方法的类
	 * @return 过滤后的方法列表
	 */
	public static Method[] getPublicMethods(Class<?> clazz) {
		return ReflectUtil.getPublicMethods(clazz);
	}

	/**
	 * 获得指定类过滤后的Public方法列表
	 *
	 * @param clazz  查找方法的类
	 * @param predicate 过滤器
	 * @return 过滤后的方法列表
	 */
	public static List<Method> getPublicMethods(Class<?> clazz, Predicate<Method> predicate) {
		return ReflectUtil.getPublicMethods(clazz, predicate);
	}

	/**
	 * 获得指定类过滤后的Public方法列表
	 *
	 * @param clazz          查找方法的类
	 * @param excludeMethods 不包括的方法
	 * @return 过滤后的方法列表
	 */
	public static List<Method> getPublicMethods(Class<?> clazz, Method... excludeMethods) {
		return ReflectUtil.getPublicMethods(clazz, excludeMethods);
	}

	/**
	 * 获得指定类过滤后的Public方法列表
	 *
	 * @param clazz              查找方法的类
	 * @param excludeMethodNames 不包括的方法名列表
	 * @return 过滤后的方法列表
	 */
	public static List<Method> getPublicMethods(Class<?> clazz, String... excludeMethodNames) {
		return ReflectUtil.getPublicMethods(clazz, excludeMethodNames);
	}

	/**
	 * 查找指定Public方法 如果找不到对应的方法或方法不为public的则返回{@code null}
	 *
	 * @param clazz      类
	 * @param methodName 方法名
	 * @param paramTypes 参数类型
	 * @return 方法
	 * @throws SecurityException 无权访问抛出异常
	 */
	public static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
		return ReflectUtil.getPublicMethod(clazz, methodName, paramTypes);
	}

	/**
	 * 获得指定类中的Public方法名<br>
	 * 去重重载的方法
	 *
	 * @param clazz 类
	 * @return 方法名Set
	 */
	public static Set<String> getDeclaredMethodNames(Class<?> clazz) {
		return ReflectUtil.getMethodNames(clazz);
	}

	/**
	 * 获得声明的所有方法，包括本类及其父类和接口的所有方法和Object类的方法
	 *
	 * @param clazz 类
	 * @return 方法数组
	 */
	public static Method[] getDeclaredMethods(Class<?> clazz) {
		return ReflectUtil.getMethods(clazz);
	}

	/**
	 * 查找指定对象中的所有方法（包括非public方法），也包括父对象和Object类的方法
	 *
	 * @param obj        被查找的对象
	 * @param methodName 方法名
	 * @param args       参数
	 * @return 方法
	 * @throws SecurityException 无访问权限抛出异常
	 */
	public static Method getDeclaredMethodOfObj(Object obj, String methodName, Object... args) throws SecurityException {
		return getDeclaredMethod(obj.getClass(), methodName, getClasses(args));
	}

	/**
	 * 查找指定类中的所有方法（包括非public方法），也包括父类和Object类的方法 找不到方法会返回{@code null}
	 *
	 * @param clazz          被查找的类
	 * @param methodName     方法名
	 * @param parameterTypes 参数类型
	 * @return 方法
	 * @throws SecurityException 无访问权限抛出异常
	 */
	public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws SecurityException {
		return ReflectUtil.getMethod(clazz, methodName, parameterTypes);
	}

	// ----------------------------------------------------------------------------------------- Field

	/**
	 * 查找指定类中的所有字段（包括非public字段）， 字段不存在则返回{@code null}
	 *
	 * @param clazz     被查找字段的类
	 * @param fieldName 字段名
	 * @return 字段
	 * @throws SecurityException 安全异常
	 */
	public static Field getDeclaredField(Class<?> clazz, String fieldName) throws SecurityException {
		if (null == clazz || StrUtil.isBlank(fieldName)) {
			return null;
		}
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			// e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查找指定类中的所有字段（包括非public字段)
	 *
	 * @param clazz 被查找字段的类
	 * @return 字段
	 * @throws SecurityException 安全异常
	 */
	public static Field[] getDeclaredFields(Class<?> clazz) throws SecurityException {
		if (null == clazz) {
			return null;
		}
		return clazz.getDeclaredFields();
	}

	// ----------------------------------------------------------------------------------------- Classpath

	/**
	 * 获得ClassPath，不解码路径中的特殊字符（例如空格和中文）
	 *
	 * @return ClassPath集合
	 */
	public static Set<String> getClassPathResources() {
		return getClassPathResources(false);
	}

	/**
	 * 获得ClassPath
	 *
	 * @param isDecode 是否解码路径中的特殊字符（例如空格和中文）
	 * @return ClassPath集合
	 * @since 1.0.0
	 */
	public static Set<String> getClassPathResources(boolean isDecode) {
		return getClassPaths(StrUtil.EMPTY, isDecode);
	}

	/**
	 * 获得ClassPath，不解码路径中的特殊字符（例如空格和中文）
	 *
	 * @param packageName 包名称
	 * @return ClassPath路径字符串集合
	 */
	public static Set<String> getClassPaths(String packageName) {
		return getClassPaths(packageName, false);
	}

	/**
	 * 获得ClassPath
	 *
	 * @param packageName 包名称
	 * @param isDecode    是否解码路径中的特殊字符（例如空格和中文）
	 * @return ClassPath路径字符串集合
	 * @since 1.0.0
	 */
	public static Set<String> getClassPaths(String packageName, boolean isDecode) {
		String packagePath = packageName.replace(StrUtil.DOT, StrUtil.SLASH);
		Enumeration<URL> resources;
		try {
			resources = getClassLoader().getResources(packagePath);
		} catch (IOException e) {
			throw new UtilException(e, "Loading classPath [{}] error!", packagePath);
		}
		final Set<String> paths = new HashSet<>();
		String path;
		while (resources.hasMoreElements()) {
			path = resources.nextElement().getPath();
			paths.add(isDecode ? URLUtil.decode(path, CharsetUtil.systemCharsetName()) : path);
		}
		return paths;
	}

	/**
	 * 获得ClassPath，将编码后的中文路径解码为原字符<br>
	 * 这个ClassPath路径会文件路径被标准化处理
	 *
	 * @return ClassPath
	 */
	public static String getClassPath() {
		return getClassPath(false);
	}

	/**
	 * 获得ClassPath，这个ClassPath路径会文件路径被标准化处理
	 *
	 * @param isEncoded 是否编码路径中的中文
	 * @return ClassPath
	 * @since 1.0.0
	 */
	public static String getClassPath(boolean isEncoded) {
		final URL classPathURL = getClassPathURL();
		String url = isEncoded ? classPathURL.getPath() : URLUtil.getDecodedPath(classPathURL);
		return FileUtil.normalize(url);
	}

	/**
	 * 获得ClassPath URL
	 *
	 * @return ClassPath URL
	 */
	public static URL getClassPathURL() {
		return getResourceURL(StrUtil.EMPTY);
	}

	/**
	 * 获得资源的URL<br>
	 * 路径用/分隔，例如:
	 *
	
	 * config/a/db.config
	 * spring/xml/test.xml
	
	 *
	 * @param resource 资源（相对Classpath的路径）
	 * @return 资源URL
	 * @see ResourceUtil#getResource(String)
	 */
	public static URL getResourceURL(String resource) throws IORuntimeException {
		return ResourceUtil.getResource(resource);
	}

	/**
	 * 获取指定路径下的资源列表<br>
	 * 路径格式必须为目录格式,用/分隔，例如:
	 *
	
	 * config/a
	 * spring/xml
	
	 *
	 * @param resource 资源路径
	 * @return 资源列表
	 * @see ResourceUtil#getResources(String)
	 */
	public static List<URL> getResources(String resource) {
		return ResourceUtil.getResources(resource);
	}

	/**
	 * 获得资源相对路径对应的URL
	 *
	 * @param resource  资源相对路径
	 * @param baseClass 基准Class，获得的相对路径相对于此Class所在路径，如果为{@code null}则相对ClassPath
	 * @return {@link URL}
	 * @see ResourceUtil#getResource(String, Class)
	 */
	public static URL getResourceUrl(String resource, Class<?> baseClass) {
		return ResourceUtil.getResource(resource, baseClass);
	}

	/**
	 * @return 获得Java ClassPath路径，不包括 jre
	 */
	public static String[] getJavaClassPaths() {
		return System.getProperty("java.class.path").split(System.getProperty("path.separator"));
	}

	/**
	 * 获取当前线程的{@link ClassLoader}
	 *
	 * @return 当前线程的class loader
	 * @see ClassLoaderUtil#getClassLoader()
	 */
	public static ClassLoader getContextClassLoader() {
		return ClassLoaderUtil.getContextClassLoader();
	}

	/**
	 * 获取{@link ClassLoader}<br>
	 * 获取顺序如下：<br>
	 *
	
	 * 1、获取当前线程的ContextClassLoader
	 * 2、获取{@link ClassLoaderUtil}类对应的ClassLoader
	 * 3、获取系统ClassLoader（{@link ClassLoader#getSystemClassLoader()}）
	
	 *
	 * @return 类加载器
	 */
	public static ClassLoader getClassLoader() {
		return ClassLoaderUtil.getClassLoader();
	}

	/**
	 * 比较判断types1和types2两组类，如果types1中所有的类都与types2对应位置的类相同，或者是其父类或接口，则返回{@code true}
	 *
	 * @param types1 类组1
	 * @param types2 类组2
	 * @return 是否相同、父类或接口
	 */
	public static boolean isAllAssignableFrom(Class<?>[] types1, Class<?>[] types2) {
		if (ArrayUtil.isEmpty(types1) && ArrayUtil.isEmpty(types2)) {
			return true;
		}
		if (null == types1 || null == types2) {
			// 任何一个为null不相等（之前已判断两个都为null的情况）
			return false;
		}
		if (types1.length != types2.length) {
			return false;
		}

		Class<?> type1;
		Class<?> type2;
		for (int i = 0; i < types1.length; i++) {
			type1 = types1[i];
			type2 = types2[i];
			if (isBasicType(type1) && isBasicType(type2)) {
				// 原始类型和包装类型存在不一致情况
				if (BasicType.unWrap(type1) != BasicType.unWrap(type2)) {
					return false;
				}
			} else if (false == type1.isAssignableFrom(type2)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 加载类
	 *
	 * @param <T>           对象类型
	 * @param className     类名
	 * @param isInitialized 是否初始化
	 * @return 类
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> loadClass(String className, boolean isInitialized) {
		return (Class<T>) ClassLoaderUtil.loadClass(className, isInitialized);
	}

	/**
	 * 加载类并初始化
	 *
	 * @param <T>       对象类型
	 * @param className 类名
	 * @return 类
	 */
	public static <T> Class<T> loadClass(String className) {
		return loadClass(className, true);
	}

	// ---------------------------------------------------------------------------------------------------- Invoke start

	/**
	 * 执行方法<br>
	 * 可执行Private方法，也可执行static方法<br>
	 * 执行非static方法时，必须满足对象有默认构造方法<br>
	 * 非单例模式，如果是非静态方法，每次创建一个新对象
	 *
	 * @param <T>                     对象类型
	 * @param classNameWithMethodName 类名和方法名表达式，类名与方法名用{@code .}或{@code #}连接 例如：com.xiaoleilu.icefrog.StrUtil.isEmpty 或 com.xiaoleilu.icefrog.StrUtil#isEmpty
	 * @param args                    参数，必须严格对应指定方法的参数类型和数量
	 * @return 返回结果
	 */
	public static <T> T invoke(String classNameWithMethodName, Object[] args) {
		return invoke(classNameWithMethodName, false, args);
	}

	/**
	 * 执行方法<br>
	 * 可执行Private方法，也可执行static方法<br>
	 * 执行非static方法时，必须满足对象有默认构造方法<br>
	 *
	 * @param <T>                     对象类型
	 * @param classNameWithMethodName 类名和方法名表达式，例如：com.xiaoleilu.icefrog.StrUtil#isEmpty或com.xiaoleilu.icefrog.StrUtil.isEmpty
	 * @param isSingleton             是否为单例对象，如果此参数为false，每次执行方法时创建一个新对象
	 * @param args                    参数，必须严格对应指定方法的参数类型和数量
	 * @return 返回结果
	 */
	public static <T> T invoke(String classNameWithMethodName, boolean isSingleton, Object... args) {
		if (StrUtil.isBlank(classNameWithMethodName)) {
			throw new UtilException("Blank classNameDotMethodName!");
		}

		int splitIndex = classNameWithMethodName.lastIndexOf('#');
		if (splitIndex <= 0) {
			splitIndex = classNameWithMethodName.lastIndexOf('.');
		}
		if (splitIndex <= 0) {
			throw new UtilException("Invalid classNameWithMethodName [{}]!", classNameWithMethodName);
		}

		final String className = classNameWithMethodName.substring(0, splitIndex);
		final String methodName = classNameWithMethodName.substring(splitIndex + 1);

		return invoke(className, methodName, isSingleton, args);
	}

	/**
	 * 执行方法<br>
	 * 可执行Private方法，也可执行static方法<br>
	 * 执行非static方法时，必须满足对象有默认构造方法<br>
	 * 非单例模式，如果是非静态方法，每次创建一个新对象
	 *
	 * @param <T>        对象类型
	 * @param className  类名，完整类路径
	 * @param methodName 方法名
	 * @param args       参数，必须严格对应指定方法的参数类型和数量
	 * @return 返回结果
	 */
	public static <T> T invoke(String className, String methodName, Object[] args) {
		return invoke(className, methodName, false, args);
	}

	/**
	 * 执行方法<br>
	 * 可执行Private方法，也可执行static方法<br>
	 * 执行非static方法时，必须满足对象有默认构造方法<br>
	 *
	 * @param <T>         对象类型
	 * @param className   类名，完整类路径
	 * @param methodName  方法名
	 * @param isSingleton 是否为单例对象，如果此参数为false，每次执行方法时创建一个新对象
	 * @param args        参数，必须严格对应指定方法的参数类型和数量
	 * @return 返回结果
	 */
	public static <T> T invoke(String className, String methodName, boolean isSingleton, Object... args) {
		Class<Object> clazz = loadClass(className);
		try {
			final Method method = getDeclaredMethod(clazz, methodName, getClasses(args));
			if (null == method) {
				throw new NoSuchMethodException(StrUtil.format("No such method: [{}]", methodName));
			}
			if (isStatic(method)) {
				return ReflectUtil.invoke(null, method, args);
			} else {
				return ReflectUtil.invoke(isSingleton ? Singleton.get(clazz) : clazz.newInstance(), method, args);
			}
		} catch (Exception e) {
			throw new UtilException(e);
		}
	}

	// ---------------------------------------------------------------------------------------------------- Invoke end

	/**
	 * 是否为包装类型
	 *
	 * @param clazz 类
	 * @return 是否为包装类型
	 */
	public static boolean isPrimitiveWrapper(Class<?> clazz) {
		if (null == clazz) {
			return false;
		}
		return BasicType.WRAPPER_PRIMITIVE_MAP.containsKey(clazz);
	}

	/**
	 * 是否为基本类型（包括包装类和原始类）
	 *
	 * @param clazz 类
	 * @return 是否为基本类型
	 */
	public static boolean isBasicType(Class<?> clazz) {
		if (null == clazz) {
			return false;
		}
		return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
	}

	/**
	 * 是否简单值类型或简单值类型的数组<br>
	 * 包括：原始类型,、String、other CharSequence, a Number, a Date, a URI, a URL, a Locale or a Class及其数组
	 *
	 * @param clazz 属性类
	 * @return 是否简单值类型或简单值类型的数组
	 */
	public static boolean isSimpleTypeOrArray(Class<?> clazz) {
		if (null == clazz) {
			return false;
		}
		return isSimpleValueType(clazz) || (clazz.isArray() && isSimpleValueType(clazz.getComponentType()));
	}

	/**
	 * 是否为简单值类型<br>
	 * 包括：
	
	 *     原始类型
	 *     String、other CharSequence
	 *     Number
	 *     Date
	 *     URI
	 *     URL
	 *     Locale
	 *     Class
	
	 *
	 * @param clazz 类
	 * @return 是否为简单值类型
	 */
	public static boolean isSimpleValueType(Class<?> clazz) {
		return isBasicType(clazz) //
				|| clazz.isEnum() //
				|| CharSequence.class.isAssignableFrom(clazz) //
				|| Number.class.isAssignableFrom(clazz) //
				|| Date.class.isAssignableFrom(clazz) //
				|| clazz.equals(URI.class) //
				|| clazz.equals(URL.class) //
				|| clazz.equals(Locale.class) //
				|| clazz.equals(Class.class)//
				// jdk8 date object
				|| TemporalAccessor.class.isAssignableFrom(clazz); //
	}

	/**
	 * 检查目标类是否可以从原类转化<br>
	 * 转化包括：<br>
	 * 1、原类是对象，目标类型是原类型实现的接口<br>
	 * 2、目标类型是原类型的父类<br>
	 * 3、两者是原始类型或者包装类型（相互转换）
	 *
	 * @param targetType 目标类型
	 * @param sourceType 原类型
	 * @return 是否可转化
	 */
	public static boolean isAssignable(Class<?> targetType, Class<?> sourceType) {
		if (null == targetType || null == sourceType) {
			return false;
		}

		// 对象类型
		if (targetType.isAssignableFrom(sourceType)) {
			return true;
		}

		// 基本类型
		if (targetType.isPrimitive()) {
			// 原始类型
			Class<?> resolvedPrimitive = BasicType.WRAPPER_PRIMITIVE_MAP.get(sourceType);
			return targetType.equals(resolvedPrimitive);
		} else {
			// 包装类型
			Class<?> resolvedWrapper = BasicType.PRIMITIVE_WRAPPER_MAP.get(sourceType);
			return resolvedWrapper != null && targetType.isAssignableFrom(resolvedWrapper);
		}
	}

	/**
	 * 指定类是否为Public
	 *
	 * @param clazz 类
	 * @return 是否为public
	 */
	public static boolean isPublic(Class<?> clazz) {
		if (null == clazz) {
			throw new NullPointerException("Class to provided is null.");
		}
		return Modifier.isPublic(clazz.getModifiers());
	}

	/**
	 * 指定方法是否为Public
	 *
	 * @param method 方法
	 * @return 是否为public
	 */
	public static boolean isPublic(Method method) {
		Preconditions.notNull(method, "Method to provided is null.");
		return Modifier.isPublic(method.getModifiers());
	}

	/**
	 * 指定类是否为非public
	 *
	 * @param clazz 类
	 * @return 是否为非public
	 */
	public static boolean isNotPublic(Class<?> clazz) {
		return false == isPublic(clazz);
	}

	/**
	 * 指定方法是否为非public
	 *
	 * @param method 方法
	 * @return 是否为非public
	 */
	public static boolean isNotPublic(Method method) {
		return false == isPublic(method);
	}

	/**
	 * 是否为静态方法
	 *
	 * @param method 方法
	 * @return 是否为静态方法
	 */
	public static boolean isStatic(Method method) {
		Preconditions.notNull(method, "Method to provided is null.");
		return Modifier.isStatic(method.getModifiers());
	}

	/**
	 * 设置方法为可访问
	 *
	 * @param method 方法
	 * @return 方法
	 */
	public static Method setAccessible(Method method) {
		if (null != method && false == method.isAccessible()) {
			method.setAccessible(true);
		}
		return method;
	}

	/**
	 * 是否为抽象类
	 *
	 * @param clazz 类
	 * @return 是否为抽象类
	 */
	public static boolean isAbstract(Class<?> clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}

	/**
	 * 是否为标准的类<br>
	 * 这个类必须：
	 *
	
	 * 1、非接口
	 * 2、非抽象类
	 * 3、非Enum枚举
	 * 4、非数组
	 * 5、非注解
	 * 6、非原始类型（int, long等）
	
	 *
	 * @param clazz 类
	 * @return 是否为标准类
	 */
	public static boolean isNormalClass(Class<?> clazz) {
		return null != clazz //
				&& false == clazz.isInterface() //
				&& false == isAbstract(clazz) //
				&& false == clazz.isEnum() //
				&& false == clazz.isArray() //
				&& false == clazz.isAnnotation() //
				&& false == clazz.isSynthetic() //
				&& false == clazz.isPrimitive();//
	}

	/**
	 * 判断类是否为枚举类型
	 *
	 * @param clazz 类
	 * @return 是否为枚举类型
	 * @since 1.0.0
	 */
	public static boolean isEnum(Class<?> clazz) {
		return null != clazz && clazz.isEnum();
	}

	/**
	 * 获得给定类的第一个泛型参数
	 *
	 * @param clazz 被检查的类，必须是已经确定泛型类型的类
	 * @return {@link Class}
	 */
	public static Class<?> getTypeArgument(Class<?> clazz) {
		return getTypeArgument(clazz, 0);
	}

	/**
	 * 获得给定类的泛型参数
	 *
	 * @param clazz 被检查的类，必须是已经确定泛型类型的类
	 * @param index 泛型类型的索引号，即第几个泛型类型
	 * @return {@link Class}
	 */
	public static Class<?> getTypeArgument(Class<?> clazz, int index) {
		final Type argumentType = TypeUtil.getTypeArgument(clazz, index);
		return TypeUtil.getClass(argumentType);
	}

	/**
	 * 获得给定类所在包的名称<br>
	 * 例如：<br>
	 * com.xiaoleilu.icefrog.util.ClassUtil =》 com.xiaoleilu.icefrog.util
	 *
	 * @param clazz 类
	 * @return 包名
	 */
	public static String getPackage(Class<?> clazz) {
		if (clazz == null) {
			return StrUtil.EMPTY;
		}
		final String className = clazz.getName();
		int packageEndIndex = className.lastIndexOf(StrUtil.DOT);
		if (packageEndIndex == -1) {
			return StrUtil.EMPTY;
		}
		return className.substring(0, packageEndIndex);
	}

	/**
	 * 获得给定类所在包的路径<br>
	 * 例如：<br>
	 * com.xiaoleilu.icefrog.util.ClassUtil =》 com/xiaoleilu/icefrog/util
	 *
	 * @param clazz 类
	 * @return 包名
	 */
	public static String getPackagePath(Class<?> clazz) {
		return getPackage(clazz).replace(StrUtil.C_DOT, StrUtil.C_SLASH);
	}

	/**
	 * 获取指定类型分的默认值<br>
	 * 默认值规则为：
	 *
	
	 * 1、如果为原始类型，返回0
	 * 2、非原始类型返回{@code null}
	
	 *
	 * @param clazz 类
	 * @return 默认值
	 * @since 1.0.0
	 */
	public static Object getDefaultValue(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			if (long.class == clazz) {
				return 0L;
			} else if (int.class == clazz) {
				return 0;
			} else if (short.class == clazz) {
				return (short) 0;
			} else if (char.class == clazz) {
				return (char) 0;
			} else if (byte.class == clazz) {
				return (byte) 0;
			} else if (double.class == clazz) {
				return 0D;
			} else if (float.class == clazz) {
				return 0f;
			} else if (boolean.class == clazz) {
				return false;
			}
		}

		return null;
	}

	/**
	 * 获得默认值列表
	 *
	 * @param classes 值类型
	 * @return 默认值列表
	 * @since 1.0.0
	 */
	public static Object[] getDefaultValues(Class<?>... classes) {
		final Object[] values = new Object[classes.length];
		for (int i = 0; i < classes.length; i++) {
			values[i] = getDefaultValue(classes[i]);
		}
		return values;
	}

	/**
	 * 是否为JDK中定义的类或接口，判断依据：
	 *
	
	 * 1、以java.、javax.开头的包名
	 * 2、ClassLoader为null
	
	 *
	 * @param clazz 被检查的类
	 * @return 是否为JDK中定义的类或接口
	 * @since 1.0.0
	 */
	public static boolean isJdkClass(Class<?> clazz) {
		final Package objectPackage = clazz.getPackage();
		if (null == objectPackage) {
			return false;
		}
		final String objectPackageName = objectPackage.getName();
		return objectPackageName.startsWith("java.") //
				|| objectPackageName.startsWith("javax.") //
				|| clazz.getClassLoader() == null;
	}

	/**
	 * 获取class类路径URL, 不管是否在jar包中都会返回文件夹的路径<br>
	 * class在jar包中返回jar所在文件夹,class不在jar中返回文件夹目录<br>
	 * jdk中的类不能使用此方法
	 *
	 * @param clazz 类
	 * @return URL
	 * @since 1.0.0
	 */
	public static URL getLocation(Class<?> clazz) {
		if (null == clazz) {
			return null;
		}
		return clazz.getProtectionDomain().getCodeSource().getLocation();
	}

	/**
	 * 获取class类路径, 不管是否在jar包中都会返回文件夹的路径<br>
	 * class在jar包中返回jar所在文件夹,class不在jar中返回文件夹目录<br>
	 * jdk中的类不能使用此方法
	 *
	 * @param clazz 类
	 * @return class路径
	 * @since 1.0.0
	 */
	public static String getLocationPath(Class<?> clazz) {
		final URL location = getLocation(clazz);
		if (null == location) {
			return null;
		}
		return location.getPath();
	}


	/**
	 * Suffix for array class names: "[]"
	 */
	public static final String ARRAY_SUFFIX = "[]";

	/**
	 * The package separator character '.'
	 */
	private static final char PACKAGE_SEPARATOR = '.';

	/**
	 * The inner class separator character '$'
	 */
	private static final char INNER_CLASS_SEPARATOR = '$';

	/**
	 * The CGLIB class separator character "$$"
	 */
	public static final String CGLIB_CLASS_SEPARATOR = "$$";

	/**
	 * The ".class" file suffix
	 */
	public static final String CLASS_FILE_SUFFIX = ".class";

	/**
	 * 从接口端计算深度
	 * @param pInterface  入参
	 * @param targetClass 入参
	 * @return 返回 int 值
	 */
	public static int checkInterfaceDeepth(Class<?> pInterface, Class<?> targetClass) {
		if (targetClass.isAssignableFrom(pInterface))
			return 0;
		int min = -1;
		for (Class<?> c : targetClass.getInterfaces()) {
			if (pInterface.isAssignableFrom(c)) {
				int i = checkInterfaceDeepth(pInterface, c) + 1;
				if (min == -1 || min > i) {
					min = i;
				}
			}
		}
		return min;
	}

	/**
	 * @param supperClass 入参
	 * @param targetClass 入参
	 * @return  返回值
	 */
	public static int checkSupperClassDeepth(Class<?> supperClass, Class<?> targetClass) {
		if (!supperClass.isAssignableFrom(targetClass)) {
			return -1;
		}
		if (targetClass.isAssignableFrom(supperClass))
			return 0;
		int supperDeepth = checkSupperClassDeepth(supperClass, targetClass.getSuperclass()) + 1;
		int interfaceDeepth = checkInterfaceDeepth(supperClass, targetClass.getSuperclass()) + 1;
		if (supperDeepth < interfaceDeepth)
			return supperDeepth;
		return interfaceDeepth;
	}

	/**
	 * 采取驼峰规则
	 * @param clazz 入参
	 * @param <T>  入参中的泛型
	 * @return 返回值
	 */
	public static <T> String humpString(Class<T> clazz) {
		String simpleName = clazz.getSimpleName();
		String first = simpleName.charAt(0) + "";
		return first.toLowerCase() + simpleName.substring(1);
	}

	// ==========================================================================
	// 取得友好类名和package名的方法。
	// ==========================================================================

	/**
	 * 取得对象所属的类的友好类名。
	 * <p>
	 * 类似<code>object.getClass().getName()</code>，但不同的是，该方法用更友好的方式显示数组类型。 例如：
	 * <p>
	 * 
	
	 *  int[].class.getName() = "[I"
	 *  ClassUtil.getFriendlyClassName(int[].class) = "int[]"
	 *
	 *  Integer[][].class.getName() = "[[Ljava.lang.Integer;"
	 *  ClassUtil.getFriendlyClassName(Integer[][].class) = "java.lang.Integer[][]"
	
	 * <p>
	 * 对于非数组的类型，该方法等效于 <code>Class.getName()</code> 方法。
	 * <p>
	 * 注意，该方法所返回的数组类名只能用于显示给人看，不能用于 <code>Class.forName</code> 操作。
	 * <p>
	 * @param object 要显示类名的对象
	 * @return 用于显示的友好类名，如果对象为空，则返回<code>null</code>
	 */
	public static String getFriendlyClassNameForObject(Object object) {
		if (object == null) {
			return null;
		}

		String javaClassName = object.getClass().getName();

		return toFriendlyClassName(javaClassName, true, javaClassName);
	}

	/**
	 * 取得友好的类名。
	 * <p>
	 * 类似<code>clazz.getName()</code>，但不同的是，该方法用更友好的方式显示数组类型。 例如：
	 * <p>
	 * 
	 *  int[].class.getName() = "[I"
	 *  ClassUtil.getFriendlyClassName(int[].class) = "int[]"
	 *
	 *  Integer[][].class.getName() = "[[Ljava.lang.Integer;"
	 *  ClassUtil.getFriendlyClassName(Integer[][].class) = "java.lang.Integer[][]"
	 * <p>
	 * 对于非数组的类型，该方法等效于 <code>Class.getName()</code> 方法。
	 * 注意，该方法所返回的数组类名只能用于显示给人看，不能用于 <code>Class.forName</code> 操作。
	 * @param clazz 要显示类名的对象
	 * @return 用于显示的友好类名，如果类对象为空，则返回<code>null</code>
	 */
	public static String getFriendlyClassName(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}

		String javaClassName = clazz.getName();

		return toFriendlyClassName(javaClassName, true, javaClassName);
	}

	/**
	 * 取得友好的类名。
	 * <p>
	 * <code>className</code> 必须是从 <code>clazz.getName()</code>
	 * 所返回的合法类名。该方法用更友好的方式显示数组类型。 例如：
	 * <p>
	 * 
	
	 *  int[].class.getName() = "[I"
	 *  ClassUtil.getFriendlyClassName(int[].class) = "int[]"
	 *
	 *  Integer[][].class.getName() = "[[Ljava.lang.Integer;"
	 *  ClassUtil.getFriendlyClassName(Integer[][].class) = "java.lang.Integer[][]"
	
	 * <p>
	 * 对于非数组的类型，该方法等效于 <code>Class.getName()</code> 方法。
	 * <p>
	 * 注意，该方法所返回的数组类名只能用于显示给人看，不能用于 <code>Class.forName</code> 操作。
	 * <p>
	 * @param javaClassName 要转换的类名
	 * @return 用于显示的友好类名，如果原类名为空，则返回 <code>null</code> ，如果原类名是非法的，则返回原类名
	 */
	public static String getFriendlyClassName(String javaClassName) {
		return toFriendlyClassName(javaClassName, true, javaClassName);
	}

	/**
	 * 将Java类名转换成友好类名。
	 * <p>
	 * @param javaClassName     Java类名
	 * @param processInnerClass 是否将内联类分隔符 <code>'$'</code> 转换成 <code>'.'</code>
	 * @return 友好的类名。如果参数非法或空，则返回<code>null</code>。
	 */
	private static String toFriendlyClassName(String javaClassName, boolean processInnerClass, String defaultIfInvalid) {
		String name = StrUtil.trimToNull(javaClassName);

		if (name == null) {
			return defaultIfInvalid;
		}

		if (processInnerClass) {
			name = name.replace('$', '.');
		}

		int length = name.length();
		int dimension = 0;

		// 取得数组的维数，如果不是数组，维数为0
		for (int i = 0; i < length; i++, dimension++) {
			if (name.charAt(i) != '[') {
				break;
			}
		}

		// 如果不是数组，则直接返回
		if (dimension == 0) {
			return name;
		}

		// 确保类名合法
		if (length <= dimension) {
			return defaultIfInvalid; // 非法类名
		}

		// 处理数组
		StringBuilder componentTypeName = new StringBuilder();

		switch (name.charAt(dimension)) {
			case 'Z':
				componentTypeName.append("boolean");
				break;

			case 'B':
				componentTypeName.append("byte");
				break;

			case 'C':
				componentTypeName.append("char");
				break;

			case 'D':
				componentTypeName.append("double");
				break;

			case 'F':
				componentTypeName.append("float");
				break;

			case 'I':
				componentTypeName.append("int");
				break;

			case 'J':
				componentTypeName.append("long");
				break;

			case 'S':
				componentTypeName.append("short");
				break;

			case 'L':
				if (name.charAt(length - 1) != ';' || length <= dimension + 2) {
					return defaultIfInvalid; // 非法类名
				}

				componentTypeName.append(name.substring(dimension + 1, length - 1));
				break;

			default:
				return defaultIfInvalid; // 非法类名
		}

		for (int i = 0; i < dimension; i++) {
			componentTypeName.append("[]");
		}

		return componentTypeName.toString();
	}

	/**
	 * 取得指定对象所属的类的简单类名，不包括package名。
	 * <p>
	 * 此方法可以正确显示数组和内联类的名称。 例如：
	 * 
	
	 *  ClassUtil.getSimpleClassNameForObject(Boolean.TRUE) = "Boolean"
	 *  ClassUtil.getSimpleClassNameForObject(new Boolean[10]) = "Boolean[]"
	 *  ClassUtil.getSimpleClassNameForObject(new int[1][2]) = "int[][]"
	
	 * <p>
	 * 本方法和<code>Class.getSimpleName()</code>的区别在于，本方法会保留inner类的外层类名称。
	 * <p>
	 * @param object 要查看的对象
	 * @return 简单类名，如果对象为 <code>null</code> ，则返回 <code>null</code>
	 */
	public static String getSimpleClassNameForObject(Object object) {
		if (object == null) {
			return null;
		}

		return getSimpleClassName(object.getClass().getName());
	}

	/**
	 * 取得指定对象所属的类的简单类名，不包括package名。
	 * <p>
	 * 此方法可以正确显示数组和内联类的名称。 例如：
	 * 
	
	 *  ClassUtil.getSimpleClassNameForObject(Boolean.TRUE) = "Boolean"
	 *  ClassUtil.getSimpleClassNameForObject(new Boolean[10]) = "Boolean[]"
	 *  ClassUtil.getSimpleClassNameForObject(new int[1][2]) = "int[][]"
	
	 * <p>
	 * 本方法和<code>Class.getSimpleName()</code>的区别在于，本方法会保留inner类的外层类名称。
	 * <p>
	 * @param object 要查看的对象
	 * @param processInnerClass  是否为内部类
	 * @return 简单类名，如果对象为 <code>null</code> ，则返回 <code>null</code>
	 */
	public static String getSimpleClassNameForObject(Object object, boolean processInnerClass) {
		if (object == null) {
			return null;
		}

		return getSimpleClassName(object.getClass().getName(), processInnerClass);
	}

	/**
	 * 取得简单类名，不包括package名。
	 * <p>
	 * 此方法可以正确显示数组和内联类的名称。 例如：
	 * 
	
	 *  ClassUtil.getSimpleClassName(Boolean.class) = "Boolean"
	 *  ClassUtil.getSimpleClassName(Boolean[].class) = "Boolean[]"
	 *  ClassUtil.getSimpleClassName(int[][].class) = "int[][]"
	 *  ClassUtil.getSimpleClassName(Map.Entry.class) = "Map.Entry"
	
	 * <p>
	 * 本方法和<code>Class.getSimpleName()</code>的区别在于，本方法会保留inner类的外层类名称。
	 * <p>
	 * @param clazz 要查看的类
	 * @return 简单类名，如果类为 <code>null</code> ，则返回 <code>null</code>
	 */
	public static String getSimpleClassName(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}

		return getSimpleClassName(clazz.getName());
	}

	/**
	 * 取得简单类名，不包括package名。
	 * <p>
	 * 此方法可以正确显示数组和内联类的名称。 例如：
	 * 
	
	 *  ClassUtil.getSimpleClassName(Boolean.class) = "Boolean"
	 *  ClassUtil.getSimpleClassName(Boolean[].class) = "Boolean[]"
	 *  ClassUtil.getSimpleClassName(int[][].class) = "int[][]"
	 *  ClassUtil.getSimpleClassName(Map.Entry.class) = "Map.Entry"
	
	 * <p>
	 * 本方法和<code>Class.getSimpleName()</code>的区别在于，本方法会保留inner类的外层类名称。
	 * <p>
	 * @param clazz 要查看的类
	 * @param proccessInnerClass 是否为内部类
	 * @return 简单类名，如果类为 <code>null</code> ，则返回 <code>null</code>
	 */
	public static String getSimpleClassName(Class<?> clazz, boolean proccessInnerClass) {
		if (clazz == null) {
			return null;
		}

		return getSimpleClassName(clazz.getName(), proccessInnerClass);
	}

	/**
	 * 取得类名，不包括package名。
	 * <p>
	 * 此方法可以正确显示数组和内联类的名称。 例如：
	 * 
	
	 *  ClassUtil.getSimpleClassName(Boolean.class.getName()) = "Boolean"
	 *  ClassUtil.getSimpleClassName(Boolean[].class.getName()) = "Boolean[]"
	 *  ClassUtil.getSimpleClassName(int[][].class.getName()) = "int[][]"
	 *  ClassUtil.getSimpleClassName(Map.Entry.class.getName()) = "Map.Entry"
	
	 * <p>
	 * 本方法和<code>Class.getSimpleName()</code>的区别在于，本方法会保留inner类的外层类名称。
	 * <p>
	 * @param javaClassName 要查看的类名
	 * @return 简单类名，如果类名为空，则返回 <code>null</code>
	 */
	public static String getSimpleClassName(String javaClassName) {
		return getSimpleClassName(javaClassName, true);
	}

	/**
	 * 取得类名，不包括package名。
	 * <p>
	 * 此方法可以正确显示数组和内联类的名称。 例如：
	 * 
	
	 *  ClassUtil.getSimpleClassName(Boolean.class.getName()) = "Boolean"
	 *  ClassUtil.getSimpleClassName(Boolean[].class.getName()) = "Boolean[]"
	 *  ClassUtil.getSimpleClassName(int[][].class.getName()) = "int[][]"
	 *  ClassUtil.getSimpleClassName(Map.Entry.class.getName()) = "Map.Entry"
	
	 * <p>
	 * 本方法和<code>Class.getSimpleName()</code>的区别在于，本方法会保留inner类的外层类名称。
	 * <p>
	 * @param javaClassName 要查看的类名
	 * @param proccesInnerClass 要处理的内部类
	 * @return 简单类名，如果类名为空，则返回 <code>null</code>
	 */
	public static String getSimpleClassName(String javaClassName, boolean proccesInnerClass) {
		String friendlyClassName = toFriendlyClassName(javaClassName, false, null);

		if (friendlyClassName == null) {
			return javaClassName;
		}

		if (proccesInnerClass) {
			char[] chars = friendlyClassName.toCharArray();
			int beginIndex = 0;

			for (int i = chars.length - 1; i >= 0; i--) {
				if (chars[i] == '.') {
					beginIndex = i + 1;
					break;
				} else if (chars[i] == '$') {
					chars[i] = '.';
				}
			}

			return new String(chars, beginIndex, chars.length - beginIndex);
		} else {
			return friendlyClassName.substring(friendlyClassName.lastIndexOf(".") + 1);
		}
	}

	/**
	 * 取得简洁的method描述。
	 * @param method  method
	 * @return  method描述。
	 */
	public static String getSimpleMethodSignature(Method method) {
		return getSimpleMethodSignature(method, false, false, false, false);
	}


	/**
	 * 取得简洁的method描述。
	 * @param method  方法
	 * @param withClassName 是否有className
	 * @return 获取的方法签名名称
	 */
	public static String getSimpleMethodSignature(Method method, boolean withClassName) {
		return getSimpleMethodSignature(method, false, false, withClassName, false);
	}

	/**
	 * 取得简洁的method描述。
	 * @param method 方法
	 * @param withModifiers 是否使用编辑器
	 * @param withReturnType 是否使用返回类型
	 * @param withExceptionType 是否使用异常类型
	 * @param withClassName 是否使用class名称
	 * @return  简洁的method描述
	 *
	 */
	public static String getSimpleMethodSignature(Method method, boolean withModifiers, boolean withReturnType, boolean withClassName, boolean withExceptionType) {
		if (method == null) {
			return null;
		}

		StringBuilder buf = new StringBuilder();

		if (withModifiers) {
			buf.append(Modifier.toString(method.getModifiers())).append(' ');
		}

		if (withReturnType) {
			buf.append(getSimpleClassName(method.getReturnType())).append(' ');
		}

		if (withClassName) {
			buf.append(getSimpleClassName(method.getDeclaringClass())).append('.');
		}

		buf.append(method.getName()).append('(');

		Class<?>[] paramTypes = method.getParameterTypes();

		for (int i = 0; i < paramTypes.length; i++) {
			Class<?> paramType = paramTypes[i];

			buf.append(getSimpleClassName(paramType));

			if (i < paramTypes.length - 1) {
				buf.append(", ");
			}
		}

		buf.append(')');

		if (withExceptionType) {
			Class<?>[] exceptionTypes = method.getExceptionTypes();

			if (!isEmptyArray(exceptionTypes)) {
				buf.append(" throws ");

				for (int i = 0; i < exceptionTypes.length; i++) {
					Class<?> exceptionType = exceptionTypes[i];

					buf.append(getSimpleClassName(exceptionType));

					if (i < exceptionTypes.length - 1) {
						buf.append(", ");
					}
				}
			}
		}

		return buf.toString();
	}

	/**
	 * 取得指定对象所属的类的package名。
	 * <p>
	 * 对于数组，此方法返回的是数组元素类型的package名。
	 * <p>
	 * @param object 要查看的对象
	 * @return package名，如果对象为 <code>null</code> ，则返回<code>""</code>
	 */
	public static String getPackageNameForObject(Object object) {
		if (object == null) {
			return StrUtil.EMPTY;
		}

		return getPackageName(object.getClass().getName());
	}

	/**
	 * 取得指定类的package名。
	 * <p>
	 * 对于数组，此方法返回的是数组元素类型的package名。
	 * <p>
	 * @param clazz 要查看的类
	 * @return package名，如果类为 <code>null</code> ，则返回<code>""</code>
	 */
	public static String getPackageName(Class<?> clazz) {
		if (clazz == null) {
			return StrUtil.EMPTY;
		}

		return getPackageName(clazz.getName());
	}

	/**
	 * 取得指定类名的package名。
	 * <p>
	 * 对于数组，此方法返回的是数组元素类型的package名。
	 * <p>
	 * @param javaClassName 要查看的类名
	 * @return package名，如果类名为空，则返回 <code>null</code>
	 */
	public static String getPackageName(String javaClassName) {
		String friendlyClassName = toFriendlyClassName(javaClassName, false, null);

		if (friendlyClassName == null) {
			return StrUtil.EMPTY;
		}

		int i = friendlyClassName.lastIndexOf('.');

		if (i == -1) {
			return StrUtil.EMPTY;
		}

		return friendlyClassName.substring(0, i);
	}

	// ==========================================================================
	// 取得类名和package名的resource名的方法。
	//
	// 和类名、package名不同的是，resource名符合文件名命名规范，例如：
	// java/lang/String.class
	// com/alibaba/commons/lang
	// etc.
	// ==========================================================================

	/**
	 * 取得对象所属的类的资源名。
	 * <p>
	 * 例如：
	 * <p>
	 * 
	
	 * ClassUtil.getResourceNameForObjectClass(&quot;This is a string&quot;) = &quot;java/lang/String.class&quot;
	
	 * <p>
	 * @param object 要显示类名的对象
	 * @return 指定对象所属类的资源名，如果对象为空，则返回<code>null</code>
	 */
	public static String getResourceNameForObjectClass(Object object) {
		if (object == null) {
			return null;
		}

		return object.getClass().getName().replace('.', '/') + ".class";
	}

	/**
	 * 取得指定类的资源名。
	 * <p>
	 * 例如：
	 * <p>
	 * 
	
	 * ClassUtil.getResourceNameForClass(String.class) = &quot;java/lang/String.class&quot;
	
	 * <p>
	 * @param clazz 要显示类名的类
	 * @return 指定类的资源名，如果指定类为空，则返回<code>null</code>
	 */
	public static String getResourceNameForClass(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}

		return clazz.getName().replace('.', '/') + ".class";
	}

	/**
	 * 取得指定类的资源名。
	 * <p>
	 * 例如：
	 * <p>
	 * 
	
	 * ClassUtil.getResourceNameForClass(&quot;java.lang.String&quot;) = &quot;java/lang/String.class&quot;
	
	 * <p>
	 * @param className 要显示的类名
	 * @return 指定类名对应的资源名，如果指定类名为空，则返回<code>null</code>
	 */
	public static String getResourceNameForClass(String className) {
		if (className == null) {
			return null;
		}

		return className.replace('.', '/') + ".class";
	}

	/**
	 * 取得指定对象所属的类的package名的资源名。
	 * <p>
	 * 对于数组，此方法返回的是数组元素类型的package名。
	 * <p>
	 * @param object 要查看的对象
	 * @return package名，如果对象为 <code>null</code> ，则返回 <code>null</code>
	 */
	public static String getResourceNameForObjectPackage(Object object) {
		if (object == null) {
			return null;
		}

		return getPackageNameForObject(object).replace('.', '/');
	}

	/**
	 * 取得指定类的package名的资源名。
	 * <p>
	 * 对于数组，此方法返回的是数组元素类型的package名。
	 * <p>
	 * @param clazz 要查看的类
	 * @return package名，如果类为 <code>null</code> ，则返回 <code>null</code>
	 */
	public static String getResourceNameForPackage(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}

		return getPackageName(clazz).replace('.', '/');
	}

	/**
	 * 取得指定类名的package名的资源名。
	 * <p>
	 * 对于数组，此方法返回的是数组元素类型的package名。
	 * <p>
	 * @param className 要查看的类名
	 * @return package名，如果类名为空，则返回 <code>null</code>
	 */
	public static String getResourceNameForPackage(String className) {
		if (className == null) {
			return null;
		}

		return getPackageName(className).replace('.', '/');
	}

	// ==========================================================================
	// 取得数组类。
	// ==========================================================================

	/**
	 * 取得指定一维数组类.
	 * <p>
	 * @param componentType 数组的基础类
	 * @return 数组类，如果数组的基类为 <code>null</code> ，则返回 <code>null</code>
	 */
	public static Class<?> getArrayClass(Class<?> componentType) {
		return getArrayClass(componentType, 1);
	}

	/**
	 * 取得指定维数的 <code>Array</code>类.
	 * <p>
	 * @param dimension     维数，如果小于 <code>0</code> 则看作 <code>0</code>
	 * @param componentClass  类名称
	 * @return 如果维数为0, 则返回基类本身, 否则返回数组类，如果数组的基类为 <code>null</code> ，则返回
	 * <code>null</code>
	 */
	public static Class<?> getArrayClass(Class<?> componentClass, int dimension) {
		if (componentClass == null) {
			return null;
		}

		switch (dimension) {
			case 1:
				return Array.newInstance(componentClass, 0).getClass();

			case 0:
				return componentClass;

			default:

				return Array.newInstance(componentClass, new int[dimension]).getClass();
		}
	}

	// ==========================================================================
	// 取得原子类型或者其wrapper类。
	// ==========================================================================

	/**
	 * 取得primitive类。
	 * <p>
	 * 例如：
	 * ClassUtil.getPrimitiveType(&quot;int&quot;) = int.class;
	 * ClassUtil.getPrimitiveType(&quot;long&quot;) = long.class;
	 * <p>
	 * @param name 传入的类
	 * @return 类
	 */
	public static Class<?> getPrimitiveType(String name) {
		PrimitiveInfo<?> info = PRIMITIVES.get(name);

		if (info != null) {
			return info.type;
		}

		return null;
	}

	/**
	 * 取得primitive类。
	 * <p>
	 * 例如：
	 * ClassUtil.getPrimitiveType(Integer.class) = int.class;
	 * ClassUtil.getPrimitiveType(Long.class) = long.class;
	 * <p>
	 *
	 * @param type 传入的类型
	 * @return  获取的原始类型
	 */
	public static Class<?> getPrimitiveType(Class<?> type) {
		return getPrimitiveType(type.getName());
	}

	/**
	 * 取得primitive类型的wrapper。如果不是primitive，则原样返回。
	 * <p>
	 * 例如：
	 * 
	 * ClassUtil.getPrimitiveWrapperType(int.class) = Integer.class;
	 * ClassUtil.getPrimitiveWrapperType(int[].class) = int[].class;
	 * ClassUtil.getPrimitiveWrapperType(int[][].class) = int[][].class;
	 * ClassUtil.getPrimitiveWrapperType(String[][].class) = String[][].class;
	 *
	 * <p>
	 * @param type 传入的基本类型
	 * @param <T>  基本类型的泛型
	 * @return   返回基本类型的包装类
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getWrapperTypeIfPrimitive(Class<T> type) {
		if (type.isPrimitive()) {
			return ((PrimitiveInfo<T>) PRIMITIVES.get(type.getName())).wrapperType;
		}

		return type;
	}

	/**
	 * 取得primitive类型的默认值。如果不是primitive，则返回<code>null</code>。
	 * <p>
	 * 例如：
	 * ClassUtil.getPrimitiveDefaultValue(int.class) = 0;
	 * ClassUtil.getPrimitiveDefaultValue(boolean.class) = false;
	 * ClassUtil.getPrimitiveDefaultValue(char.class) = '\0';
	 * <p>
	 * @param type  传入的基本类型
	 * @param <T>  泛型
	 * @return 各基本类型的默认值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getPrimitiveDefaultValue(Class<T> type) {
		PrimitiveInfo<T> info = (PrimitiveInfo<T>) PRIMITIVES.get(type.getName());

		if (info != null) {
			return info.defaultValue;
		}

		return null;
	}

	private static final Map<String, PrimitiveInfo<?>> PRIMITIVES = createHashMap();

	static {
		addPrimitive(boolean.class, "Z", Boolean.class, "booleanValue", false);
		addPrimitive(short.class, "S", Short.class, "shortValue", (short) 0);
		addPrimitive(int.class, "I", Integer.class, "intValue", 0);
		addPrimitive(long.class, "J", Long.class, "longValue", 0L);
		addPrimitive(float.class, "F", Float.class, "floatValue", 0F);
		addPrimitive(double.class, "D", Double.class, "doubleValue", 0D);
		addPrimitive(char.class, "C", Character.class, "charValue", '\0');
		addPrimitive(byte.class, "B", Byte.class, "byteValue", (byte) 0);
		addPrimitive(void.class, "V", Void.class, null, null);
	}

	private static <T> void addPrimitive(Class<T> type, String typeCode, Class<T> wrapperType, String unwrapMethod, T defaultValue) {
		PrimitiveInfo<T> info = new PrimitiveInfo<T>(type, typeCode, wrapperType, unwrapMethod, defaultValue);

		PRIMITIVES.put(type.getName(), info);
		PRIMITIVES.put(wrapperType.getName(), info);
	}

	/**
	 * 代表一个primitive类型的信息。
	 */
	@SuppressWarnings("unused")
	private static class PrimitiveInfo<T> {
		final Class<T> type;
		final String typeCode;
		final Class<T> wrapperType;
		final String unwrapMethod;
		final T defaultValue;

		public PrimitiveInfo(Class<T> type, String typeCode, Class<T> wrapperType, String unwrapMethod, T defaultValue) {
			this.type = type;
			this.typeCode = typeCode;
			this.wrapperType = wrapperType;
			this.unwrapMethod = unwrapMethod;
			this.defaultValue = defaultValue;
		}
	}

	// ==========================================================================
	// 类型匹配。
	// ==========================================================================

	/**
	 * 检查一组指定类型 <code>fromClasses</code> 的对象是否可以赋值给另一组类型 <code>classes</code>。
	 * <p>
	 * 此方法可以用来确定指定类型的参数 <code>object1, object2, ...</code> 是否可以用来调用确定参数类型为
	 * <code>class1, class2,
	 * ...</code> 的方法。
	 * <p>
	 * 对于 <code>fromClasses</code> 的每个元素 <code>fromClass</code> 和
	 * <code>classes</code> 的每个元素 <code>clazz</code>， 按照如下规则：
	 * <ol>
	 * <li>如果目标类 <code>clazz</code> 为 <code>null</code> ，总是返回 <code>false</code>
	 * 。</li>
	 * <li>如果参数类型 <code>fromClass</code> 为 <code>null</code> ，并且目标类型
	 * <code>clazz</code> 为非原子类型，则返回 <code>true</code>。 因为 <code>null</code>
	 * 可以被赋给任何引用类型。</li>
	 * <li>调用 <code>Class.isAssignableFrom</code> 方法来确定目标类 <code>clazz</code>
	 * 是否和参数类 <code>fromClass</code> 相同或是其父类、接口，如果是，则返回 <code>true</code>。</li>
	 * <li>如果目标类型 <code>clazz</code> 为原子类型，那么根据 <a
	 * href="http://java.sun.com/docs/books/jls/">The Java Language
	 * Specification</a> ，sections 5.1.1, 5.1.2, 5.1.4定义的Widening Primitive
	 * Conversion规则，参数类型 <code>fromClass</code> 可以是任何能扩展成该目标类型的原子类型及其包装类。 例如，
	 * <code>clazz</code> 为 <code>long</code> ，那么参数类型可以是 <code>byte</code>、
	 * <code>short</code>、<code>int</code>、<code>long</code>、<code>char</code>
	 * 及其包装类 <code>java.lang.Byte</code>、<code>java.lang.Short</code>、
	 * <code>java.lang.Integer</code>、 <code>java.lang.Long</code> 和
	 * <code>java.lang.Character</code> 。如果满足这个条件，则返回 <code>true</code>。</li>
	 * <li>不满足上述所有条件，则返回 <code>false</code>。</li>
	 * </ol>
	 * <p>
	 * @param classes     目标类型列表，如果是 <code>null</code> 总是返回 <code>false</code>
	 * @param fromClasses 参数类型列表， <code>null</code> 表示可赋值给任意非原子类型
	 * @return 如果可以被赋值，则返回 <code>true</code>
	 */
	public static boolean isAssignable(Class<?>[] classes, Class<?>[] fromClasses) {
		if (!isArraySameLength(fromClasses, classes)) {
			return false;
		}

		if (fromClasses == null) {
			fromClasses = EMPTY_CLASS_ARRAY;
		}

		if (classes == null) {
			classes = EMPTY_CLASS_ARRAY;
		}

		for (int i = 0; i < fromClasses.length; i++) {
			if (isAssignable(classes[i], fromClasses[i]) == false) {
				return false;
			}
		}

		return true;
	}



	private final static Map<Class<?>, Set<Class<?>>> assignmentTable = createHashMap();

	static {
		// boolean可以接受：boolean
		assignmentTable.put(boolean.class, assignableSet(boolean.class));

		// byte可以接受：byte
		assignmentTable.put(byte.class, assignableSet(byte.class));

		// char可以接受：char
		assignmentTable.put(char.class, assignableSet(char.class));

		// short可以接受：short, byte
		assignmentTable.put(short.class, assignableSet(short.class, byte.class));

		// int可以接受：int、byte、short、char
		assignmentTable.put(int.class, assignableSet(int.class, byte.class, short.class, char.class));

		// long可以接受：long、int、byte、short、char
		assignmentTable.put(long.class, assignableSet(long.class, int.class, byte.class, short.class, char.class));

		// float可以接受：float, long, int, byte, short, char
		assignmentTable.put(float.class, assignableSet(float.class, long.class, int.class, byte.class, short.class, char.class));

		// double可以接受：double, float, long, int, byte, short, char
		assignmentTable.put(double.class, assignableSet(double.class, float.class, long.class, int.class, byte.class, short.class, char.class));

	}

	/**
	 *
	 * @param types 入参
	 * @return 返回的set
	 */
	private static Set<Class<?>> assignableSet(Class<?>... types) {
		Set<Class<?>> assignableSet = createHashSet();

		for (Class<?> type : types) {
			assignableSet.add(getPrimitiveType(type));
			assignableSet.add(getWrapperTypeIfPrimitive(type));
		}

		return assignableSet;
	}

	// ==========================================================================
	// 定位class的位置。
	// ==========================================================================

	/**
	 * 在class loader中查找class的位置。
	 * @param clazz 传入的class
	 * @return class 的包名
	 */
	public static String locateClass(Class<?> clazz) {
		return locateClass(clazz.getName(), clazz.getClassLoader());
	}

	/**
	 * 在class loader中查找class的位置。
	 * @param className  传入的class 字符串名称
	 * @return  class的位置。
	 */
	public static String locateClass(String className) {
		return locateClass(className, null);
	}

	/**
	 * 在class loader中查找class的位置。
	 * @param className 类名称
	 * @param loader 类加载器
	 * @return class的位置。
	 */
	public static String locateClass(String className, ClassLoader loader) {
		if (loader == null) {
			loader = Thread.currentThread().getContextClassLoader();
		}

		String classFile = className.replace('.', '/') + ".class";
		URL locationURL = loader.getResource(classFile);
		String location = null;

		if (locationURL != null) {
			location = locationURL.toExternalForm();

			if (location.endsWith(classFile)) {
				location = location.substring(0, location.length() - classFile.length());
			}

			location = location.replaceAll("^(jar|zip):|!/$", StrUtil.EMPTY);
		}

		return location;
	}

	/**
	 * property format. Strips the outer class name in case of an inner class.
	 * @param clazz the class
	 * @return the short name rendered in a standard JavaBeans property format,the short string name of a Java class in uncapitalized JavaBeans
	 *  property format. Strips the outer class name in case of an inner class.
	 * see java.beans.Introspector#decapitalize(String)
	 */
	public static String getShortNameAsProperty(Class<?> clazz) {
		String shortName = ClassUtil.getShortName(clazz);
		int dotIndex = shortName.lastIndexOf('.');
		shortName = (dotIndex != -1 ? shortName.substring(dotIndex + 1) : shortName);
		return Introspector.decapitalize(shortName);
	}

	/**
	 * Get the class name without the qualified package name.
	 * @param className the className to get the short name for
	 * @return the class name of the class without the package name
	 * throws IllegalArgumentException if the className is empty
	 */
	public static String getShortName(String className) {
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		int nameEndIndex = className.indexOf(CGLIB_CLASS_SEPARATOR);
		if (nameEndIndex == -1) {
			nameEndIndex = className.length();
		}
		String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
		shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
		return shortName;
	}

	/**
	 * Get the class name without the qualified package name.
	 * @param clazz the class to get the short name for
	 * @return the class name of the class without the package name
	 */
	public static String getShortName(Class<?> clazz) {
		return getShortName(getQualifiedName(clazz));
	}

	/**
	 * @return the qualified name of the given class: usually simply
	 * the class name, but component type class name + "[]" for arrays.
	 * @param clazz the class
	 * @return the qualified name of the class
	 */
	public static String getQualifiedName(Class<?> clazz) {
		if (clazz.isArray()) {
			return getQualifiedNameForArray(clazz);
		} else {
			return clazz.getName();
		}
	}

	/**
	 * Build a nice qualified name for an array:
	 * component type class name + "[]".
	 * @param clazz the array class
	 * @return a qualified name for the array class
	 */
	private static String getQualifiedNameForArray(Class<?> clazz) {
		StringBuilder result = new StringBuilder();
		while (clazz.isArray()) {
			clazz = clazz.getComponentType();
			result.append(ARRAY_SUFFIX);
		}
		result.insert(0, clazz.getName());
		return result.toString();
	}

	/**
	 * @return the qualified name of the given method, consisting of
	 * fully qualified interface/class name + "." + method name.
	 * @param method the method
	 * @return the qualified name of the method
	 */
	public static String getQualifiedMethodName(Method method) {
		return method.getDeclaringClass().getName() + "." + method.getName();
	}

	/**
	 * @return all interfaces that the given instance implements as array,
	 * including ones implemented by superclasses.
	 * @param instance the instance to analyze for interfaces
	 * @return all interfaces that the given instance implements as array
	 */
	public static Class[] getAllInterfaces(Object instance) {
		return getAllInterfacesForClass(instance.getClass());
	}

	/**
	 * @return all interfaces that the given class implements as array,
	 * including ones implemented by superclasses.
	 * <p>If the class itself is an interface, it gets returned as sole interface.
	 * @param clazz the class to analyze for interfaces
	 * @return all interfaces that the given object implements as array
	 */
	public static Class<?>[] getAllInterfacesForClass(Class<?> clazz) {
		return getAllInterfacesForClass(clazz, null);
	}

	/**
	 * @return all interfaces that the given class implements as array,
	 * including ones implemented by superclasses.
	 * <p>If the class itself is an interface, it gets returned as sole interface.
	 * @param clazz the class to analyze for interfaces
	 * @param classLoader the ClassLoader that the interfaces need to be visible in
	 * (may be <code>null</code> when accepting all declared interfaces)
	 * @return all interfaces that the given object implements as array
	 */
	public static Class<?>[] getAllInterfacesForClass(Class<?> clazz, ClassLoader classLoader) {
		Set<Class> ifcs = getAllInterfacesForClassAsSet(clazz, classLoader);
		return ifcs.toArray(new Class[ifcs.size()]);
	}

	/**
	 * @return all interfaces that the given instance implements as Set,
	 * including ones implemented by superclasses.
	 * @param instance the instance to analyze for interfaces
	 * @return all interfaces that the given instance implements as Set
	 */
	public static Set<Class> getAllInterfacesAsSet(Object instance) {
		return getAllInterfacesForClassAsSet(instance.getClass());
	}

	/**
	 * @return all interfaces that the given class implements as Set,
	 * including ones implemented by superclasses.
	 * <p>If the class itself is an interface, it gets returned as sole interface.
	 * @param clazz the class to analyze for interfaces
	 * @return all interfaces that the given object implements as Set
	 */
	public static Set<Class> getAllInterfacesForClassAsSet(Class clazz) {
		return getAllInterfacesForClassAsSet(clazz, null);
	}

	/**
	 * @return all interfaces that the given class implements as Set,
	 * including ones implemented by superclasses.
	 * <p>If the class itself is an interface, it gets returned as sole interface.
	 * @param clazz the class to analyze for interfaces
	 * @param classLoader the ClassLoader that the interfaces need to be visible in
	 * (may be <code>null</code> when accepting all declared interfaces)
	 * @return all interfaces that the given object implements as Set
	 */
	public static Set<Class> getAllInterfacesForClassAsSet(Class clazz, ClassLoader classLoader) {
		if (clazz.isInterface() && isVisible(clazz, classLoader)) {
			return Collections.singleton(clazz);
		}
		Set<Class> interfaces = new LinkedHashSet<Class>();
		while (clazz != null) {
			Class<?>[] ifcs = clazz.getInterfaces();
			for (Class<?> ifc : ifcs) {
				interfaces.addAll(getAllInterfacesForClassAsSet(ifc, classLoader));
			}
			clazz = clazz.getSuperclass();
		}
		return interfaces;
	}

	/**
	 * Check whether the given class is visible in the given ClassLoader.
	 * @param clazz the class to check (typically an interface)
	 * @param classLoader the ClassLoader to check against (may be <code>null</code>,
	 * in which case this method will always return <code>true</code>)
	 * @return 返回是否可见
	 */
	public static boolean isVisible(Class<?> clazz, ClassLoader classLoader) {
		if (classLoader == null) {
			return true;
		}
		try {
			Class<?> actualClass = classLoader.loadClass(clazz.getName());
			return (clazz == actualClass);
			// Else: different interface class found...
		} catch (ClassNotFoundException ex) {
			// No interface class found...
			return false;
		}
	}

	/**
	 * Determine the name of the class file, relative to the containing
	 * package: e.g. "String.class"
	 * @param clazz the class
	 * @return the file name of the ".class" file
	 */
	public static String getClassFileName(Class clazz) {
		String className = clazz.getName();
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		return className.substring(lastDotIndex + 1) + CLASS_FILE_SUFFIX;
	}

	/**
	 * 判断该类型是不是包装类型
	 * @param clazz  传入的class
	 * @return 是否为包装类型
	 */
	public static boolean isBasicClass(Class<?> clazz) {
		boolean isPrimitive = false;
		try {
			if (clazz.isPrimitive() || clazz.isAssignableFrom(String.class)) {
				isPrimitive = true;
			} else {
				isPrimitive = ((Class<?>) clazz.getField("TYPE").get(null)).isPrimitive();
			}
		} catch (Exception e) {
			isPrimitive = false;
		}
		return isPrimitive;
	}

}
