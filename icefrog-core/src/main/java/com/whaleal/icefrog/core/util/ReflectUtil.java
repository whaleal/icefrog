package com.whaleal.icefrog.core.util;

import com.whaleal.icefrog.core.annotation.Alias;
import com.whaleal.icefrog.core.bean.NullWrapperBean;
import com.whaleal.icefrog.core.collection.CollUtil;
import com.whaleal.icefrog.core.convert.Convert;
import com.whaleal.icefrog.core.exceptions.UtilException;
import com.whaleal.icefrog.core.lang.Precondition;
import com.whaleal.icefrog.core.lang.Predicate;
import com.whaleal.icefrog.core.lang.SimpleCache;
import com.whaleal.icefrog.core.lang.reflect.MethodHandleUtil;
import com.whaleal.icefrog.core.map.MapUtil;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.*;

import static com.whaleal.icefrog.core.lang.Precondition.*;

/**
 * 反射工具类
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class ReflectUtil {

    /**
     * Pre-built MethodFilter that matches all non-bridge non-synthetic methods
     * which are not declared on {@code java.lang.Object}.
     * <p>
     *
     */
    public static final MethodFilter USER_DECLARED_METHODS =
            (method -> !method.isBridge() && !method.isSynthetic());
    /**
     * Pre-built FieldFilter that matches all non-static, non-final fields.
     */
    public static final FieldFilter COPYABLE_FIELDS =
            (field -> !(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())));
    /**
     * 构造对象缓存
     */
    private static final SimpleCache<Class<?>, Constructor<?>[]> CONSTRUCTORS_CACHE = new SimpleCache<>();
   /**
     * 字段缓存
     */
    private static final SimpleCache<Class<?>, Field[]> FIELDS_CACHE = new SimpleCache<>();
    /**
     * 方法缓存
     */
    private static final SimpleCache<Class<?>, Method[]> METHODS_CACHE = new SimpleCache<>();
    /**
     * Naming prefix for CGLIB-renamed methods.
     *
     * @see #isCglibRenamedMethod
     */
    private static final String CGLIB_RENAMED_METHOD_PREFIX = "CGLIB$";

    // --------------------------------------------------------------------------------------------------------- Field
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
    private static final Method[] EMPTY_METHOD_ARRAY = new Method[0];
    private static final Field[] EMPTY_FIELD_ARRAY = new Field[0];
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /**
     * 查找类中的指定参数的构造方法，如果找到构造方法，会自动设置可访问为true
     *
     * @param <T>            对象类型
     * @param clazz          类
     * @param parameterTypes 参数类型，只要任何一个参数是指定参数的父类或接口或相等即可，此参数可以不传
     * @return 构造方法，如果未找到返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getConstructor( Class<T> clazz, Class<?>... parameterTypes ) {
        if (null == clazz) {
            return null;
        }

        final Constructor<?>[] constructors = getConstructors(clazz);
        Class<?>[] pts;
        for (Constructor<?> constructor : constructors) {
            pts = constructor.getParameterTypes();
            if (ClassUtil.isAllAssignableFrom(pts, parameterTypes)) {
                // 构造可访问
                setAccessible(constructor);
                return (Constructor<T>) constructor;
            }
        }
        return null;
    }

    /**
     * 获得一个类中所有构造列表
     *
     * @param <T>       构造的对象类型
     * @param beanClass 类
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T>[] getConstructors( Class<T> beanClass ) throws SecurityException {
        notNull(beanClass);
        Constructor<?>[] constructors = CONSTRUCTORS_CACHE.get(beanClass);
        if (null != constructors) {
            return (Constructor<T>[]) constructors;
        }

        constructors = getConstructorsDirectly(beanClass);
        return (Constructor<T>[]) CONSTRUCTORS_CACHE.put(beanClass, constructors);
    }

    /**
     * 获得一个类中所有构造列表，直接反射获取，无缓存
     *
     * @param beanClass 类
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    public static Constructor<?>[] getConstructorsDirectly( Class<?> beanClass ) throws SecurityException {
        notNull(beanClass);
        return beanClass.getDeclaredConstructors();
    }

    /**
     * 查找指定类中是否包含指定名称对应的字段，包括所有字段（包括非public字段），也包括父类和Object类的字段
     *
     * @param beanClass 被查找字段的类,不能为null
     * @param name      字段名
     * @return 是否包含字段
     * @throws SecurityException 安全异常
     * @since 1.0.0
     */
    public static boolean hasField( Class<?> beanClass, String name ) throws SecurityException {
        return null != getField(beanClass, name);
    }

    /**
     * 获取字段名，如果存在{@link Alias}注解，读取注解的值作为名称
     *
     * @param field 字段
     * @return 字段名
     * @since 1.0.0
     */
    public static String getFieldName( Field field ) {
        if (null == field) {
            return null;
        }

        final Alias alias = field.getAnnotation(Alias.class);
        if (null != alias) {
            return alias.value();
        }

        return field.getName();
    }

    /**
     * 查找指定类中的指定name的字段（包括非public字段），也包括父类和Object类的字段， 字段不存在则返回{@code null}
     *
     * @param beanClass 被查找字段的类,不能为null
     * @param name      字段名
     * @return 字段
     * @throws SecurityException 安全异常
     */
    public static Field getField( Class<?> beanClass, String name ) throws SecurityException {
        final Field[] fields = getFields(beanClass);
        return ArrayUtil.firstMatch(( field ) -> name.equals(getFieldName(field)), fields);
    }

    /**
     * 获取指定类中字段名和字段对应的有序Map，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param beanClass 类
     * @return 字段名和字段对应的Map，有序
     * @since 1.0.0
     */
    public static Map<String, Field> getFieldMap( Class<?> beanClass ) {
        final Field[] fields = getFields(beanClass);
        final HashMap<String, Field> map = MapUtil.newHashMap(fields.length, true);
        for (Field field : fields) {
            map.put(field.getName(), field);
        }
        return map;
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param beanClass 类
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    public static Field[] getFields( Class<?> beanClass ) throws SecurityException {
        Field[] allFields = FIELDS_CACHE.get(beanClass);
        if (null != allFields) {
            return allFields;
        }

        allFields = getFieldsDirectly(beanClass, true);
        return FIELDS_CACHE.put(beanClass, allFields);
    }

    /**
     * 获得一个类中所有满足条件的字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param beanClass      类
     * @param fieldPredicate field过滤器，过滤掉不需要的field
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     * @since 1.0.0
     */
    public static Field[] getFields( Class<?> beanClass, Predicate<Field> fieldPredicate ) throws SecurityException {
        return ArrayUtil.filter(getFields(beanClass), fieldPredicate);
    }

    // --------------------------------------------------------------------------------------------------------- method

    /**
     * 获得一个类中所有字段列表，直接反射获取，无缓存<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param beanClass            类
     * @param withSuperClassFields 是否包括父类的字段列表
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    public static Field[] getFieldsDirectly( Class<?> beanClass, boolean withSuperClassFields ) throws SecurityException {
        notNull(beanClass);

        Field[] allFields = null;
        Class<?> searchType = beanClass;
        Field[] declaredFields;
        while (searchType != null) {
            declaredFields = searchType.getDeclaredFields();
            if (null == allFields) {
                allFields = declaredFields;
            } else {
                allFields = ArrayUtil.append(allFields, declaredFields);
            }
            searchType = withSuperClassFields ? searchType.getSuperclass() : null;
        }

        return allFields;
    }

    /**
     * 获取字段值
     *
     * @param obj       对象，如果static字段，此处为类
     * @param fieldName 字段名
     * @return 字段值
     * @throws UtilException 包装IllegalAccessException异常
     */
    public static Object getFieldValue( Object obj, String fieldName ) throws UtilException {
        if (null == obj || StrUtil.isBlank(fieldName)) {
            return null;
        }
        return getFieldValue(obj, getField(obj instanceof Class ? (Class<?>) obj : obj.getClass(), fieldName));
    }

    /**
     * 获取静态字段值
     *
     * @param field 字段
     * @return 字段值
     * @throws UtilException 包装IllegalAccessException异常
     * @since 1.0.0
     */
    public static Object getStaticFieldValue( Field field ) throws UtilException {
        return getFieldValue(null, field);
    }

    /**
     * 获取字段值
     *
     * @param obj   对象，static字段则此字段为null
     * @param field 字段
     * @return 字段值
     * @throws UtilException 包装IllegalAccessException异常
     */
    public static Object getFieldValue( Object obj, Field field ) throws UtilException {
        if (null == field) {
            return null;
        }
        if (obj instanceof Class) {
            // 静态字段获取时对象为null
            obj = null;
        }

        setAccessible(field);
        Object result;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            throw new UtilException(e, "IllegalAccess for {}.{}", field.getDeclaringClass(), field.getName());
        }
        return result;
    }

    /**
     * 获取所有字段的值
     *
     * @param obj bean对象，如果是static字段，此处为类class
     * @return 字段值数组
     * @since 1.0.0
     */
    public static Object[] getFieldsValue( Object obj ) {
        if (null != obj) {
            final Field[] fields = getFields(obj instanceof Class ? (Class<?>) obj : obj.getClass());
            if (null != fields) {
                final Object[] values = new Object[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    values[i] = getFieldValue(obj, fields[i]);
                }
                return values;
            }
        }
        return null;
    }

    /**
     * 设置字段值
     *
     * @param obj       对象,static字段则此处传Class
     * @param fieldName 字段名
     * @param value     值，值类型必须与字段类型匹配，不会自动转换对象类型
     * @throws UtilException 包装IllegalAccessException异常
     */
    public static void setFieldValue( Object obj, String fieldName, Object value ) throws UtilException {
        notNull(obj);
        notBlank(fieldName);

        final Field field = getField((obj instanceof Class) ? (Class<?>) obj : obj.getClass(), fieldName);
        notNull(field, "Field [{}] is not exist in [{}]", fieldName, obj.getClass().getName());
        setFieldValue(obj, field, value);
    }

    /**
     * 设置字段值
     *
     * @param obj   对象，如果是static字段，此参数为null
     * @param field 字段
     * @param value 值，值类型必须与字段类型匹配，不会自动转换对象类型
     * @throws UtilException UtilException 包装IllegalAccessException异常
     */
    public static void setFieldValue( Object obj, Field field, Object value ) throws UtilException {
        notNull(field, "Field in [{}] not exist !", obj);

        final Class<?> fieldType = field.getType();
        if (null != value) {
            if (false == fieldType.isAssignableFrom(value.getClass())) {
                //对于类型不同的字段，尝试转换，转换失败则使用原对象类型
                final Object targetValue = Convert.convert(fieldType, value);
                if (null != targetValue) {
                    value = targetValue;
                }
            }
        } else {
            // 获取null对应默认值，防止原始类型造成空指针问题
            value = ClassUtil.getDefaultValue(fieldType);
        }

        setAccessible(field);
        try {
            field.set(obj instanceof Class ? null : obj, value);
        } catch (IllegalAccessException e) {
            throw new UtilException(e, "IllegalAccess for {}.{}", obj, field.getName());
        }
    }

    /**
     * 获得指定类本类及其父类中的Public方法名<br>
     * 去重重载的方法
     *
     * @param clazz 类
     * @return 方法名Set
     */
    public static Set<String> getPublicMethodNames( Class<?> clazz ) {
        final HashSet<String> methodSet = new HashSet<>();
        final Method[] methodArray = getPublicMethods(clazz);
        if (ArrayUtil.isNotEmpty(methodArray)) {
            for (Method method : methodArray) {
                methodSet.add(method.getName());
            }
        }
        return methodSet;
    }

    /**
     * 获得本类及其父类所有Public方法
     *
     * @param clazz 查找方法的类
     * @return 过滤后的方法列表
     */
    public static Method[] getPublicMethods( Class<?> clazz ) {
        return null == clazz ? null : clazz.getMethods();
    }

    /**
     * 获得指定类过滤后的Public方法列表
     *
     * @param clazz     查找方法的类
     * @param predicate 过滤器
     * @return 过滤后的方法列表
     */
    public static List<Method> getPublicMethods( Class<?> clazz, Predicate<Method> predicate ) {
        if (null == clazz) {
            return null;
        }

        final Method[] methods = getPublicMethods(clazz);
        List<Method> methodList;
        if (null != predicate) {
            methodList = new ArrayList<>();
            for (Method method : methods) {
                if (predicate.apply(method)) {
                    methodList.add(method);
                }
            }
        } else {
            methodList = CollUtil.newArrayList(methods);
        }
        return methodList;
    }

    /**
     * 获得指定类过滤后的Public方法列表
     *
     * @param clazz          查找方法的类
     * @param excludeMethods 不包括的方法
     * @return 过滤后的方法列表
     */
    public static List<Method> getPublicMethods( Class<?> clazz, Method... excludeMethods ) {
        final HashSet<Method> excludeMethodSet = CollUtil.newHashSet(excludeMethods);
        return getPublicMethods(clazz, method -> false == excludeMethodSet.contains(method));
    }

    /**
     * 获得指定类过滤后的Public方法列表
     *
     * @param clazz              查找方法的类
     * @param excludeMethodNames 不包括的方法名列表
     * @return 过滤后的方法列表
     */
    public static List<Method> getPublicMethods( Class<?> clazz, String... excludeMethodNames ) {
        final HashSet<String> excludeMethodNameSet = CollUtil.newHashSet(excludeMethodNames);
        return getPublicMethods(clazz, method -> false == excludeMethodNameSet.contains(method.getName()));
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
    public static Method getPublicMethod( Class<?> clazz, String methodName, Class<?>... paramTypes ) throws SecurityException {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    /**
     * 查找指定对象中的所有方法（包括非public方法），也包括父对象和Object类的方法
     *
     * <p>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。
     * </p>
     *
     * @param obj        被查找的对象，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param args       参数
     * @return 方法
     * @throws SecurityException 无访问权限抛出异常
     */
    public static Method getMethodOfObj( Object obj, String methodName, Object... args ) throws SecurityException {
        if (null == obj || StrUtil.isBlank(methodName)) {
            return null;
        }
        return getMethod(obj.getClass(), methodName, ClassUtil.getClasses(args));
    }

    /**
     * 忽略大小写查找指定方法，如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param paramTypes 参数类型，指定参数类型如果是方法的子类也算
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     * @since 1.0.0
     */
    public static Method getMethodIgnoreCase( Class<?> clazz, String methodName, Class<?>... paramTypes ) throws SecurityException {
        return getMethod(clazz, true, methodName, paramTypes);
    }

    /**
     * 查找指定方法 如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param paramTypes 参数类型，指定参数类型如果是方法的子类也算
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     */
    public static Method getMethod( Class<?> clazz, String methodName, Class<?>... paramTypes ) throws SecurityException {
        return getMethod(clazz, false, methodName, paramTypes);
    }

    /**
     * 查找指定方法 如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param ignoreCase 是否忽略大小写
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param paramTypes 参数类型，指定参数类型如果是方法的子类也算
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     * @since 1.0.0
     */
    public static Method getMethod( Class<?> clazz, boolean ignoreCase, String methodName, Class<?>... paramTypes ) throws SecurityException {
        if (null == clazz || StrUtil.isBlank(methodName)) {
            return null;
        }

        final Method[] methods = getMethods(clazz);
        if (ArrayUtil.isNotEmpty(methods)) {
            for (Method method : methods) {
                if (StrUtil.equals(methodName, method.getName(), ignoreCase)) {
                    if (ClassUtil.isAllAssignableFrom(method.getParameterTypes(), paramTypes)) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 按照方法名查找指定方法名的方法，只返回匹配到的第一个方法，如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法只检查方法名是否一致，并不检查参数的一致性。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     * @since 1.0.0
     */
    public static Method getMethodByName( Class<?> clazz, String methodName ) throws SecurityException {
        return getMethodByName(clazz, false, methodName);
    }

    /**
     * 按照方法名查找指定方法名的方法，只返回匹配到的第一个方法，如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法只检查方法名是否一致（忽略大小写），并不检查参数的一致性。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     * @since 1.0.0
     */
    public static Method getMethodByNameIgnoreCase( Class<?> clazz, String methodName ) throws SecurityException {
        return getMethodByName(clazz, true, methodName);
    }

    /**
     * 按照方法名查找指定方法名的方法，只返回匹配到的第一个方法，如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法只检查方法名是否一致，并不检查参数的一致性。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param ignoreCase 是否忽略大小写
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     * @since 1.0.0
     */
    public static Method getMethodByName( Class<?> clazz, boolean ignoreCase, String methodName ) throws SecurityException {
        if (null == clazz || StrUtil.isBlank(methodName)) {
            return null;
        }

        final Method[] methods = getMethods(clazz);
        if (ArrayUtil.isNotEmpty(methods)) {
            for (Method method : methods) {
                if (StrUtil.equals(methodName, method.getName(), ignoreCase)) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 获得指定类中的Public方法名<br>
     * 去重重载的方法
     *
     * @param clazz 类
     * @return 方法名Set
     * @throws SecurityException 安全异常
     */
    public static Set<String> getMethodNames( Class<?> clazz ) throws SecurityException {
        final HashSet<String> methodSet = new HashSet<>();
        final Method[] methods = getMethods(clazz);
        for (Method method : methods) {
            methodSet.add(method.getName());
        }
        return methodSet;
    }

    // --------------------------------------------------------------------------------------------------------- newInstance

    /**
     * 获得指定类过滤后的Public方法列表
     *
     * @param clazz     查找方法的类
     * @param predicate 过滤器
     * @return 过滤后的方法列表
     * @throws SecurityException 安全异常
     */
    public static Method[] getMethods( Class<?> clazz, Predicate<Method> predicate ) throws SecurityException {
        if (null == clazz) {
            return null;
        }
        return ArrayUtil.filter(getMethods(clazz), predicate);
    }

    /**
     * 获得一个类中所有方法列表，包括其父类中的方法
     *
     * @param beanClass 类
     * @return 方法列表
     * @throws SecurityException 安全检查异常
     */
    public static Method[] getMethods( Class<?> beanClass ) throws SecurityException {
        Method[] allMethods = METHODS_CACHE.get(beanClass);
        if (null != allMethods) {
            return allMethods;
        }

        allMethods = getMethodsDirectly(beanClass, true);
        return METHODS_CACHE.put(beanClass, allMethods);
    }

    /**
     * 获得一个类中所有方法列表，直接反射获取，无缓存
     *
     * @param beanClass             类
     * @param withSuperClassMethods 是否包括父类的方法列表
     * @return 方法列表
     * @throws SecurityException 安全检查异常
     */
    public static Method[] getMethodsDirectly( Class<?> beanClass, boolean withSuperClassMethods ) throws SecurityException {
        notNull(beanClass);

        Method[] allMethods = null;
        Class<?> searchType = beanClass;
        Method[] declaredMethods;
        while (searchType != null) {
            declaredMethods = searchType.getDeclaredMethods();
            if (null == allMethods) {
                allMethods = declaredMethods;
            } else {
                allMethods = ArrayUtil.append(allMethods, declaredMethods);
            }
            searchType = withSuperClassMethods ? searchType.getSuperclass() : null;
        }

        return allMethods;
    }

    /**
     * 是否为equals方法
     *
     * @param method 方法
     * @return 是否为equals方法
     */
    public static boolean isEqualsMethod( Method method ) {
        if (method == null || false == "equals".equals(method.getName())) {
            return false;
        }
        final Class<?>[] paramTypes = method.getParameterTypes();
        return (1 == paramTypes.length && paramTypes[0] == Object.class);
    }

    // --------------------------------------------------------------------------------------------------------- invoke

    /**
     * 是否为hashCode方法
     *
     * @param method 方法
     * @return 是否为hashCode方法
     */
    public static boolean isHashCodeMethod( Method method ) {
        return method != null//
                && "hashCode".equals(method.getName())//
                && isEmptyParam(method);
    }

    /**
     * 是否为toString方法
     *
     * @param method 方法
     * @return 是否为toString方法
     */
    public static boolean isToStringMethod( Method method ) {
        return method != null//
                && "toString".equals(method.getName())//
                && isEmptyParam(method);
    }

    /**
     * 是否为无参数方法
     *
     * @param method 方法
     * @return 是否为无参数方法
     * @since 1.0.0
     */
    public static boolean isEmptyParam( Method method ) {
        return method.getParameterTypes().length == 0;
    }

    /**
     * 实例化对象
     *
     * @param <T>   对象类型
     * @param clazz 类名
     * @return 对象
     * @throws UtilException 包装各类异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance( String clazz ) throws UtilException {
        try {
            return (T) Class.forName(clazz).newInstance();
        } catch (Exception e) {
            throw new UtilException(e, "Instance class [{}] error!", clazz);
        }
    }

    /**
     * 实例化对象
     *
     * @param <T>    对象类型
     * @param clazz  类
     * @param params 构造函数参数
     * @return 对象
     * @throws UtilException 包装各类异常
     */
    public static <T> T newInstance( Class<T> clazz, Object... params ) throws UtilException {
        if (clazz.isInterface()) {
            throw new UtilException(clazz.getName(), "Specified class is an interface");
        }
        if (ArrayUtil.isEmpty(params)) {
            final Constructor<T> constructor = getConstructor(clazz);
            try {
                return constructor.newInstance();
            } catch (Exception e) {
                throw new UtilException(e, "Instance class [{}] error!", clazz);
            }
        }

        final Class<?>[] paramTypes = ClassUtil.getClasses(params);
        final Constructor<T> constructor = getConstructor(clazz, paramTypes);
        if (null == constructor) {
            throw new UtilException("No Constructor matched for parameter types: [{}]", new Object[]{paramTypes});
        }
        try {
            return newInstance(constructor, params);
        } catch (Exception e) {
            throw new UtilException(e, "Instance class [{}] error!", clazz);
        }
    }

    /**
     * 根据构造方法实例化对象
     *
     * @param <T>    对象类型
     * @param ctor   构造方法
     * @param params 构造函数参数
     * @return 对象
     * @throws UtilException 包装各类异常
     */
    public static <T> T newInstance( Constructor<T> ctor, Object... params ) throws UtilException {
        notNull(ctor, "Constructor must not be null");
        try {
            setAccessible(ctor);
            return ctor.newInstance(params);
        } catch (Exception e) {
            throw new UtilException(e, "No Constructor matched for parameter types: [{}]", new Object[]{params});
        }
    }


    //-------------------undo

    /**
     * 尝试遍历并调用此类的所有构造方法，直到构造成功并返回
     * <p>
     * 对于某些特殊的接口，按照其默认实现实例化，例如：
     * <pre>
     *     Map       -》 HashMap
     *     Collction -》 ArrayList
     *     List      -》 ArrayList
     *     Set       -》 HashSet
     * </pre>
     *
     * @param <T>       对象类型
     * @param beanClass 被构造的类
     * @return 构造后的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstanceIfPossible( Class<T> beanClass ) {
        notNull(beanClass, "Class must not be null");

        // 某些特殊接口的实例化按照默认实现进行
        if (beanClass.isAssignableFrom(AbstractMap.class)) {
            beanClass = (Class<T>) HashMap.class;
        } else if (beanClass.isAssignableFrom(List.class)) {
            beanClass = (Class<T>) ArrayList.class;
        } else if (beanClass.isAssignableFrom(Set.class)) {
            beanClass = (Class<T>) HashSet.class;
        }

        try {
            return newInstance(beanClass);
        } catch (Exception e) {
            // ignore
            // 默认构造不存在的情况下查找其它构造
        }

        final Constructor<T>[] constructors = getConstructors(beanClass);
        Class<?>[] parameterTypes;
        for (Constructor<T> constructor : constructors) {
            parameterTypes = constructor.getParameterTypes();
            if (0 == parameterTypes.length) {
                continue;
            }
            setAccessible(constructor);
            try {
                return newInstance(constructor, ClassUtil.getDefaultValues(parameterTypes));
            } catch (Exception ignore) {
                // 构造出错时继续尝试下一种构造方式
            }
        }
        return null;
    }

    /**
     * 执行静态方法
     *
     * @param <T>    对象类型
     * @param method 方法（对象方法或static方法都可）
     * @param args   参数对象
     * @return 结果
     * @throws UtilException 多种异常包装
     */
    public static <T> T invokeStatic( Method method, Object... args ) throws UtilException {
        return invoke(null, method, args);
    }

    /**
     * 执行方法<br>
     * 执行前要检查给定参数：
     *
     * <pre>
     * 1. 参数个数是否与方法参数个数一致
     * 2. 如果某个参数为null但是方法这个位置的参数为原始类型，则赋予原始类型默认值
     * </pre>
     *
     * @param <T>    返回对象类型
     * @param obj    对象，如果执行静态方法，此值为{@code null}
     * @param method 方法（对象方法或static方法都可）
     * @param args   参数对象
     * @return 结果
     * @throws UtilException 一些列异常的包装
     */
    public static <T> T invokeWithCheck( Object obj, Method method, Object... args ) throws UtilException {
        final Class<?>[] types = method.getParameterTypes();
        if (null != args) {
            isTrue(args.length == types.length, "Params length [{}] is not fit for param length [{}] of method !", args.length, types.length);
            Class<?> type;
            for (int i = 0; i < args.length; i++) {
                type = types[i];
                if (type.isPrimitive() && null == args[i]) {
                    // 参数是原始类型，而传入参数为null时赋予默认值
                    args[i] = ClassUtil.getDefaultValue(type);
                }
            }
        }

        return invoke(obj, method, args);
    }

    /**
     * 执行方法
     *
     * <p>
     * 对于用户传入参数会做必要检查，包括：
     *
     * <pre>
     *     1、忽略多余的参数
     *     2、参数不够补齐默认值
     *     3、传入参数为null，但是目标参数类型为原始类型，做转换
     * </pre>
     *
     * @param <T>    返回对象类型
     * @param obj    对象，如果执行静态方法，此值为{@code null}
     * @param method 方法（对象方法或static方法都可）
     * @param args   参数对象
     * @return 结果
     * @throws UtilException 一些列异常的包装
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke( Object obj, Method method, Object... args ) throws UtilException {
        setAccessible(method);

        // 检查用户传入参数：
        // 1、忽略多余的参数
        // 2、参数不够补齐默认值
        // 3、通过NullWrapperBean传递的参数,会直接赋值null
        // 4、传入参数为null，但是目标参数类型为原始类型，做转换
        // 5、传入参数类型不对应，尝试转换类型
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Object[] actualArgs = new Object[parameterTypes.length];
        if (null != args) {
            for (int i = 0; i < actualArgs.length; i++) {
                if (i >= args.length || null == args[i]) {
                    // 越界或者空值
                    actualArgs[i] = ClassUtil.getDefaultValue(parameterTypes[i]);
                } else if (args[i] instanceof NullWrapperBean) {
                    //如果是通过NullWrapperBean传递的null参数,直接赋值null
                    actualArgs[i] = null;
                } else if (false == parameterTypes[i].isAssignableFrom(args[i].getClass())) {
                    //对于类型不同的字段，尝试转换，转换失败则使用原对象类型
                    final Object targetValue = Convert.convert(parameterTypes[i], args[i]);
                    if (null != targetValue) {
                        actualArgs[i] = targetValue;
                    }
                } else {
                    actualArgs[i] = args[i];
                }
            }
        }

        if (method.isDefault()) {
            // 当方法是default方法时，尤其对象是代理对象，需使用句柄方式执行
            // 代理对象情况下调用method.invoke会导致循环引用执行，最终栈溢出
            return MethodHandleUtil.invokeSpecial(obj, method, args);
        }

        try {
            return (T) method.invoke(ClassUtil.isStatic(method) ? null : obj, actualArgs);
        } catch (Exception e) {
            throw new UtilException(e);
        }
    }

    /**
     * 执行对象中指定方法
     * 如果需要传递的参数为null,请使用NullWrapperBean来传递,不然会丢失类型信息
     *
     * @param <T>        返回对象类型
     * @param obj        方法所在对象
     * @param methodName 方法名
     * @param args       参数列表
     * @return 执行结果
     * @throws UtilException IllegalAccessException包装
     * @see NullWrapperBean
     * @since 1.0.0
     */
    public static <T> T invoke( Object obj, String methodName, Object... args ) throws UtilException {
        notNull(obj, "Object to get method must be not null!");
        notBlank(methodName, "Method name must be not blank!");

        final Method method = getMethodOfObj(obj, methodName, args);
        if (null == method) {
            throw new UtilException("No such method: [{}] from [{}]", methodName, obj.getClass());
        }
        return invoke(obj, method, args);
    }

    /**
     * 设置方法为可访问（私有方法可以被外部调用）
     *
     * @param <T>              AccessibleObject的子类，比如Class、Method、Field等
     * @param accessibleObject 可设置访问权限的对象，比如Class、Method、Field等
     * @return 被设置可访问的对象
     * @since 1.0.0
     */
    public static <T extends AccessibleObject> T setAccessible( T accessibleObject ) {
        if(accessibleObject ==null){
            return accessibleObject ;
        }else if(accessibleObject instanceof Member ){
            Member method = (Member) accessibleObject;
            if ((!Modifier.isPublic(method.getModifiers()) ||
                         !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !accessibleObject.isAccessible()){
                accessibleObject.setAccessible(true);
            }
        }else if(false == accessibleObject.isAccessible()){
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;

    }

    /**
     * 获取 方法数组中与 给定方法名一致且参数最少的方法（最少可以为 0，即无需参数）,当名称无法匹配的时候回返回 null。
     *
     * @param methods    方法列表
     * @param methodName 方法名称
     * @return 返回 命中的Method对象 ，在没有命中时返回null
     * @throws IllegalArgumentException if methods of the given name were found but
     *                                  could not be resolved to a unique method with minimal parameters
     */
    @Nullable
    public static Method findMethodWithMinimalParameters( Method[] methods, String methodName )
            throws IllegalArgumentException {

        Method targetMethod = null;
        int numMethodsFoundWithCurrentMinimumArgs = 0;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                int numParams = method.getParameterCount();
                if (targetMethod == null || numParams < targetMethod.getParameterCount()) {
                    targetMethod = method;
                    numMethodsFoundWithCurrentMinimumArgs = 1;
                } else if (!method.isBridge() && targetMethod.getParameterCount() == numParams) {
                    if (targetMethod.isBridge()) {
                        // Prefer regular method over bridge...
                        targetMethod = method;
                    } else {
                        // Additional candidate with same length
                        numMethodsFoundWithCurrentMinimumArgs++;
                    }
                }
            }
        }
        if (numMethodsFoundWithCurrentMinimumArgs > 1) {
            throw new IllegalArgumentException("Cannot resolve method '" + methodName +
                    "' to a unique method. Attempted to resolve to overloaded method with " +
                    "the least number of parameters but there were " +
                    numMethodsFoundWithCurrentMinimumArgs + " candidates.");
        }
        return targetMethod;
    }

    /**
     * Handle the given reflection exception.
     * <p>Should only be called if no checked exception is expected to be thrown
     * by a target method, or if an error occurs while accessing a method or field.
     * <p>Throws the underlying RuntimeException or Error in case of an
     * InvocationTargetException with such a root cause. Throws an
     * IllegalStateException with an appropriate message or
     * UndeclaredThrowableException otherwise.
     *
     * @param ex the reflection exception to handle
     */
    public static void handleReflectionException( Exception ex ) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method or field: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            handleInvocationTargetException((InvocationTargetException) ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /**
     * Handle the given invocation target exception. Should only be called if no
     * checked exception is expected to be thrown by the target method.
     * <p>Throws the underlying RuntimeException or Error in case of such a root
     * cause. Throws an UndeclaredThrowableException otherwise.
     *
     * @param ex the invocation target exception to handle
     */
    public static void handleInvocationTargetException( InvocationTargetException ex ) {
        rethrowRuntimeException(ex.getTargetException());
    }

    /**
     * Rethrow the given {@link Throwable exception}, which is presumably the
     * <em>target exception</em> of an {@link InvocationTargetException}.
     * Should only be called if no checked exception is expected to be thrown
     * by the target method.
     * <p>Rethrows the underlying exception cast to a {@link RuntimeException} or
     * {@link Error} if appropriate; otherwise, throws an
     * {@link UndeclaredThrowableException}.
     *
     * @param ex the exception to rethrow
     * @throws RuntimeException the rethrown exception
     */
    public static void rethrowRuntimeException( Throwable ex ) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }


    // Constructor handling

    /**
     * Rethrow the given {@link Throwable exception}, which is presumably the
     * <em>target exception</em> of an {@link InvocationTargetException}.
     * Should only be called if no checked exception is expected to be thrown
     * by the target method.
     * <p>Rethrows the underlying exception cast to an {@link Exception} or
     * {@link Error} if appropriate; otherwise, throws an
     * {@link UndeclaredThrowableException}.
     *
     * @param ex the exception to rethrow
     * @throws Exception the rethrown exception (in case of a checked exception)
     */
    public static void rethrowException( Throwable ex ) throws Exception {
        if (ex instanceof Exception) {
            throw (Exception) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    /**
     * Obtain an accessible constructor for the given class and parameters.
     *
     * @param clazz          the clazz to check
     * @param parameterTypes the parameter types of the desired constructor
     * @return the constructor reference
     * @throws NoSuchMethodException if no such constructor exists
     */
    public static <T> Constructor<T> accessibleConstructor( Class<T> clazz, Class<?>... parameterTypes )
            throws NoSuchMethodException {

        Constructor<T> ctor = clazz.getDeclaredConstructor(parameterTypes);
        setAccessible(ctor);
        return ctor;
    }



    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and no parameters. Searches all superclasses up to {@code Object}.
     * <p>Returns {@code null} if no {@link Method} can be found.
     *
     * @param clazz the class to introspect
     * @param name  the name of the method
     * @return the Method object, or {@code null} if none found
     */

    public static Method findMethod( Class<?> clazz, String name ) {
        return findMethod(clazz, name, EMPTY_CLASS_ARRAY);
    }

    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and parameter types. Searches all superclasses up to {@code Object}.
     * <p>Returns {@code null} if no {@link Method} can be found.
     *
     * @param clazz      the class to introspect
     * @param name       the name of the method
     * @param paramTypes the parameter types of the method
     *                   (may be {@code null} to indicate any signature)
     * @return the Method object, or {@code null} if none found
     */

    public static Method findMethod( Class<?> clazz, String name, Class<?>... paramTypes ) {
        Precondition.notNull(clazz, "Class must not be null");
        Precondition.notNull(name, "Method name must not be null");
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() :
                    getDeclaredMethods(searchType, false));
            for (Method method : methods) {
                if (name.equals(method.getName()) && (paramTypes == null || hasSameParams(method, paramTypes))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    private static boolean hasSameParams( Method method, Class<?>[] paramTypes ) {
        return (paramTypes.length == method.getParameterCount() &&
                Arrays.equals(paramTypes, method.getParameterTypes()));
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object with no arguments.
     * The target object can be {@code null} when invoking a static {@link Method}.
     * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException}.
     *
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @return the invocation result, if any
     * @see #invokeMethod(Method, Object, Object[])
     */

    public static Object invokeMethod( Method method, Object target ) {
        return invokeMethod(method, target, EMPTY_OBJECT_ARRAY);
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object with the
     * supplied arguments. The target object can be {@code null} when invoking a
     * static {@link Method}.
     * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException}.
     *
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @param args   the invocation arguments (may be {@code null})
     * @return the invocation result, if any
     */

    public static Object invokeMethod( Method method, Object target, Object... args ) {
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    /**
     * Determine whether the given method explicitly declares the given
     * exception or one of its superclasses, which means that an exception
     * of that type can be propagated as-is within a reflective invocation.
     *
     * @param method        the declaring method
     * @param exceptionType the exception to throw
     * @return {@code true} if the exception can be thrown as-is;
     * {@code false} if it needs to be wrapped
     */
    public static boolean declaresException( Method method, Class<?> exceptionType ) {
        Precondition.notNull(method, "Method must not be null");
        Class<?>[] declaredExceptions = method.getExceptionTypes();
        for (Class<?> declaredException : declaredExceptions) {
            if (declaredException.isAssignableFrom(exceptionType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Perform the given callback operation on all matching methods of the given
     * class, as locally declared or equivalent thereof (such as default methods
     * on Java 8 based interfaces that the given class implements).
     *
     * @param clazz the class to introspect
     * @param mc    the callback to invoke for each method
     * @throws IllegalStateException if introspection fails
     * @see #doWithMethods
     */
    public static void doWithLocalMethods( Class<?> clazz, MethodCallback mc ) {
        Method[] methods = getDeclaredMethods(clazz, false);
        for (Method method : methods) {
            try {
                mc.doWith(method);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
            }
        }
    }

    /**
     * Perform the given callback operation on all matching methods of the given
     * class and superclasses.
     * <p>The same named method occurring on subclass and superclass will appear
     * twice, unless excluded by a {@link MethodFilter}.
     *
     * @param clazz the class to introspect
     * @param mc    the callback to invoke for each method
     * @throws IllegalStateException if introspection fails
     * @see #doWithMethods(Class, MethodCallback, MethodFilter)
     */
    public static void doWithMethods( Class<?> clazz, MethodCallback mc ) {
        doWithMethods(clazz, mc, null);
    }

    /**
     * Perform the given callback operation on all matching methods of the given
     * class and superclasses (or given interface and super-interfaces).
     * <p>The same named method occurring on subclass and superclass will appear
     * twice, unless excluded by the specified {@link MethodFilter}.
     *
     * @param clazz the class to introspect
     * @param mc    the callback to invoke for each method
     * @param mf    the filter that determines the methods to apply the callback to
     * @throws IllegalStateException if introspection fails
     */
    public static void doWithMethods( Class<?> clazz, MethodCallback mc, MethodFilter mf ) {
        // Keep backing up the inheritance hierarchy.
        Method[] methods = getDeclaredMethods(clazz, false);
        for (Method method : methods) {
            if (mf != null && !mf.matches(method)) {
                continue;
            }
            try {
                mc.doWith(method);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
            }
        }
        if (clazz.getSuperclass() != null && (mf != USER_DECLARED_METHODS || clazz.getSuperclass() != Object.class)) {
            doWithMethods(clazz.getSuperclass(), mc, mf);
        } else if (clazz.isInterface()) {
            for (Class<?> superIfc : clazz.getInterfaces()) {
                doWithMethods(superIfc, mc, mf);
            }
        }
    }

    /**
     * Get all declared methods on the leaf class and all superclasses.
     * Leaf class methods are included first.
     *
     * @param leafClass the class to introspect
     * @throws IllegalStateException if introspection fails
     */
    public static Method[] getAllDeclaredMethods( Class<?> leafClass ) {
        final List<Method> methods = new ArrayList<>(32);
        doWithMethods(leafClass, methods::add);
        return methods.toArray(EMPTY_METHOD_ARRAY);
    }

    /**
     * Get the unique set of declared methods on the leaf class and all superclasses.
     * Leaf class methods are included first and while traversing the superclass hierarchy
     * any methods found with signatures matching a method already included are filtered out.
     *
     * @param leafClass the class to introspect
     * @throws IllegalStateException if introspection fails
     */
    public static Method[] getUniqueDeclaredMethods( Class<?> leafClass ) {
        return getUniqueDeclaredMethods(leafClass, null);
    }

    /**
     * Get the unique set of declared methods on the leaf class and all superclasses.
     * Leaf class methods are included first and while traversing the superclass hierarchy
     * any methods found with signatures matching a method already included are filtered out.
     *
     * @param leafClass the class to introspect
     * @param mf        the filter that determines the methods to take into account
     * @throws IllegalStateException if introspection fails
     */
    public static Method[] getUniqueDeclaredMethods( Class<?> leafClass, MethodFilter mf ) {
        final List<Method> methods = new ArrayList<>(32);
        doWithMethods(leafClass, method -> {
            boolean knownSignature = false;
            Method methodBeingOverriddenWithCovariantReturnType = null;
            for (Method existingMethod : methods) {
                if (method.getName().equals(existingMethod.getName()) &&
                        method.getParameterCount() == existingMethod.getParameterCount() &&
                        Arrays.equals(method.getParameterTypes(), existingMethod.getParameterTypes())) {
                    // Is this a covariant return type situation?
                    if (existingMethod.getReturnType() != method.getReturnType() &&
                            existingMethod.getReturnType().isAssignableFrom(method.getReturnType())) {
                        methodBeingOverriddenWithCovariantReturnType = existingMethod;
                    } else {
                        knownSignature = true;
                    }
                    break;
                }
            }
            if (methodBeingOverriddenWithCovariantReturnType != null) {
                methods.remove(methodBeingOverriddenWithCovariantReturnType);
            }
            if (!knownSignature && !isCglibRenamedMethod(method)) {
                methods.add(method);
            }
        }, mf);
        return methods.toArray(EMPTY_METHOD_ARRAY);
    }

    /**
     * Variant of {@link Class#getDeclaredMethods()} that uses a local cache in
     * order to avoid the JVM's SecurityManager check and new Method instances.
     * In addition, it also includes Java 8 default methods from locally
     * implemented interfaces, since those are effectively to be treated just
     * like declared methods.
     *
     * @param clazz the class to introspect
     * @return the cached array of methods
     * @throws IllegalStateException if introspection fails
     * @see Class#getDeclaredMethods()
     */
    public static Method[] getDeclaredMethods( Class<?> clazz ) {
        return getDeclaredMethods(clazz, true);
    }

    private static Method[] getDeclaredMethods( Class<?> clazz, boolean defensive ) {
        Precondition.notNull(clazz, "Class must not be null");
        Method[] result = METHODS_CACHE.get(clazz);
        if (result == null) {
            try {
                Method[] declaredMethods = clazz.getDeclaredMethods();
                List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
                if (defaultMethods != null) {
                    result = new Method[declaredMethods.length + defaultMethods.size()];
                    System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
                    int index = declaredMethods.length;
                    for (Method defaultMethod : defaultMethods) {
                        result[index] = defaultMethod;
                        index++;
                    }
                } else {
                    result = declaredMethods;
                }
                METHODS_CACHE.put(clazz, (result.length == 0 ? EMPTY_METHOD_ARRAY : result));
            } catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +
                        "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }
        return (result.length == 0 || !defensive) ? result : result.clone();
    }


    private static List<Method> findConcreteMethodsOnInterfaces( Class<?> clazz ) {
        List<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(ifcMethod);
                }
            }
        }
        return result;
    }


    /**
     * Determine whether the given method is originally declared by {@link Object}.
     */
    public static boolean isObjectMethod( Method method ) {
        return (method != null && (method.getDeclaringClass() == Object.class ||
                isEqualsMethod(method) || isHashCodeMethod(method) || isToStringMethod(method)));
    }

    /**
     * Determine whether the given method is a CGLIB 'renamed' method,
     * following the pattern "CGLIB$methodName$0".
     *
     * @param renamedMethod the method to check
     */
    public static boolean isCglibRenamedMethod( Method renamedMethod ) {
        String name = renamedMethod.getName();
        if (name.startsWith(CGLIB_RENAMED_METHOD_PREFIX)) {
            int i = name.length() - 1;
            while (i >= 0 && Character.isDigit(name.charAt(i))) {
                i--;
            }
            return (i > CGLIB_RENAMED_METHOD_PREFIX.length() && (i < name.length() - 1) && name.charAt(i) == '$');
        }
        return false;
    }




    /**
     * Attempt to find a {@link Field field} on the supplied {@link Class} with the
     * supplied {@code name}. Searches all superclasses up to {@link Object}.
     *
     * @param clazz the class to introspect
     * @param name  the name of the field
     * @return the corresponding Projection object, or {@code null} if not found
     */

    public static Field findField( Class<?> clazz, String name ) {
        return findField(clazz, name, null);
    }

    /**
     * Attempt to find a {@link Field field} on the supplied {@link Class} with the
     * supplied {@code name} and/or {@link Class type}. Searches all superclasses
     * up to {@link Object}.
     *
     * @param clazz the class to introspect
     * @param name  the name of the field (may be {@code null} if type is specified)
     * @param type  the type of the field (may be {@code null} if name is specified)
     * @return the corresponding Projection object, or {@code null} if not found
     */

    public static Field findField( Class<?> clazz, String name, Class<?> type ) {
        Precondition.notNull(clazz, "Class must not be null");
        Precondition.isTrue(name != null || type != null, "Either name or type of the field must be specified");
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            Field[] fields = getDeclaredFields(searchType);
            for (Field field : fields) {
                if ((name == null || name.equals(field.getName())) &&
                        (type == null || type.equals(field.getType()))) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * Set the field represented by the supplied {@linkplain Field field object} on
     * the specified {@linkplain Object target object} to the specified {@code value}.
     * <p>In accordance with {@link Field#set(Object, Object)} semantics, the new value
     * is automatically unwrapped if the underlying field has a primitive type.
     * <p>This method does not support setting {@code static final} fields.
     * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException(Exception)}.
     *
     * @param field  the field to set
     * @param target the target object on which to set the field
     * @param value  the value to set (may be {@code null})
     */
    public static void setField( Field field, Object target, Object value ) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
        }
    }

    /**
     * Get the field represented by the supplied {@link Field field object} on the
     * specified {@link Object target object}. In accordance with {@link Field#get(Object)}
     * semantics, the returned value is automatically wrapped if the underlying field
     * has a primitive type.
     * <p>Thrown exceptions are handled via a call to {@link #handleReflectionException(Exception)}.
     *
     * @param field  the field to get
     * @param target the target object from which to get the field
     * @return the field's current value
     */

    public static Object getField( Field field, Object target ) {
        try {
            return field.get(target);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    /**
     * Invoke the given callback on all locally declared fields in the given class.
     *
     * @param clazz the target class to analyze
     * @param fc    the callback to invoke for each field
     * @throws IllegalStateException if introspection fails
     * @see #doWithFields
     */
    public static void doWithLocalFields( Class<?> clazz, FieldCallback fc ) {
        for (Field field : getDeclaredFields(clazz)) {
            try {
                fc.doWith(field);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
            }
        }
    }

    /**
     * Invoke the given callback on all fields in the target class, going up the
     * class hierarchy to get all declared fields.
     *
     * @param clazz the target class to analyze
     * @param fc    the callback to invoke for each field
     * @throws IllegalStateException if introspection fails
     */
    public static void doWithFields( Class<?> clazz, FieldCallback fc ) {
        doWithFields(clazz, fc, null);
    }

    /**
     * Invoke the given callback on all fields in the target class, going up the
     * class hierarchy to get all declared fields.
     *
     * @param clazz the target class to analyze
     * @param fc    the callback to invoke for each field
     * @param ff    the filter that determines the fields to apply the callback to
     * @throws IllegalStateException if introspection fails
     */
    public static void doWithFields( Class<?> clazz, FieldCallback fc, FieldFilter ff ) {
        // Keep backing up the inheritance hierarchy.
        Class<?> targetClass = clazz;
        do {
            Field[] fields = getDeclaredFields(targetClass);
            for (Field field : fields) {
                if (ff != null && !ff.matches(field)) {
                    continue;
                }
                try {
                    fc.doWith(field);
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);
    }

    /**
     * 获取本类及超类的字段 Fields
     *
     * @param object
     * @return
     */
    public static Field[] getAllDeclaredFields( Object object ) {
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        Field[] result = FIELDS_CACHE.get(clazz);
        if (result == null) {
            while (clazz != null) {
                Field[] supperClassFiled = getDeclaredFields(clazz);
                fieldList.addAll(new ArrayList<>(Arrays.asList(supperClassFiled)));
                clazz = clazz.getSuperclass();
            }

            result = new Field[fieldList.size()];
            fieldList.toArray(result);
            //  这个时候 clazz  已经被赋值为null  了
            FIELDS_CACHE.put(object.getClass(), (result.length == 0 ? EMPTY_FIELD_ARRAY : result));
        }


        return result;
    }

    /**
     * This variant retrieves {@link Class#getDeclaredFields()} from a local cache
     * in order to avoid the JVM's SecurityManager check and defensive array copying.
     *
     * @param clazz the class to introspect
     * @return the cached array of fields
     * @throws IllegalStateException if introspection fails
     * @see Class#getDeclaredFields()
     */
    private static Field[] getDeclaredFields( Class<?> clazz ) {
        Precondition.notNull(clazz, "Class must not be null");
        Field[] result = FIELDS_CACHE.get(clazz);
        if (result == null) {
            try {
                result = clazz.getDeclaredFields();
                FIELDS_CACHE.put(clazz, (result.length == 0 ? EMPTY_FIELD_ARRAY : result));
            } catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +
                        "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }
        return result;
    }

    /**
     * Given the source object and the destination, which must be the same class
     * or a subclass, copy all fields, including inherited fields. Designed to
     * work on objects with public no-arg constructors.
     *
     * @throws IllegalStateException if introspection fails
     */
    public static void shallowCopyFieldState( final Object src, final Object dest ) {
        Precondition.notNull(src, "Source for field copy cannot be null");
        Precondition.notNull(dest, "Destination for field copy cannot be null");
        if (!src.getClass().isAssignableFrom(dest.getClass())) {
            throw new IllegalArgumentException("Destination class [" + dest.getClass().getName() +
                    "] must be same or subclass as source class [" + src.getClass().getName() + "]");
        }
        doWithFields(src.getClass(), field -> {
            setAccessible(field);
            Object srcValue = field.get(src);
            field.set(dest, srcValue);
        }, COPYABLE_FIELDS);
    }

    /**
     * Determine whether the given field is a "public static final" constant.
     *
     * @param field the field to check
     */
    public static boolean isPublicStaticFinal( Field field ) {
        int modifiers = field.getModifiers();
        return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
    }




    /**
     * Clear the internal method/field cache.
     */
    public static void clearCache() {
        METHODS_CACHE.clear();
        FIELDS_CACHE.clear();
    }

    /**
     * Creates an instance of the given class.
     *
     * @param <T>   the type
     * @param clazz the class
     * @param args  the arguments to pass to the constructor
     * @return an instance of the object
     * @throws InstantiationException    on error
     * @throws IllegalAccessException    on error
     * @throws InvocationTargetException on error
     */
    @SuppressWarnings("unchecked")
    public static <T> T instantiate( Class<T> clazz, Object... args )
            throws InstantiationException,
            IllegalAccessException,
            InvocationTargetException {
        if (args.length == 0) {
            return clazz.newInstance();
        }
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args.getClass();
        }
        for (Constructor<?> c : clazz.getConstructors()) {
            Class<?>[] paramTypes = c.getParameterTypes();
            if (paramTypes.length != args.length) {
                continue;
            }
            boolean foundCtr = true;
            for (int i = 0; i < paramTypes.length; i++) {
                if (!paramTypes[i].isAssignableFrom(types[i])) {
                    foundCtr = false;
                    break;
                }
            }
            if (foundCtr) {
                return (T) c.newInstance(args);
            }
        }
        throw new IllegalArgumentException(
                "Unable to find suitable constructor for " + clazz.getName());
    }


    /**
     * Action to take on each method.
     */
    @FunctionalInterface
    public interface MethodCallback {

        /**
         * Perform an operation using the given method.
         *
         * @param method the method to operate on
         */
        void doWith( Method method ) throws IllegalArgumentException, IllegalAccessException;
    }

    /**
     * Callback optionally used to filter methods to be operated on by a method callback.
     */
    @FunctionalInterface
    public interface MethodFilter {

        /**
         * Determine whether the given method matches.
         *
         * @param method the method to check
         */
        boolean matches( Method method );

        /**
         * Create a composite filter based on this filter <em>and</em> the provided filter.
         * <p>If this filter does not match, the next filter will not be applied.
         *
         * @param next the next {@code MethodFilter}
         * @return a composite {@code MethodFilter}
         * @throws IllegalArgumentException if the MethodFilter argument is {@code null}
         *                                  5.3.2
         */
        default MethodFilter and( MethodFilter next ) {
            Precondition.notNull(next, "Next MethodFilter must not be null");
            return method -> matches(method) && next.matches(method);
        }
    }

    /**
     * Callback interface invoked on each field in the hierarchy.
     */
    @FunctionalInterface
    public interface FieldCallback {

        /**
         * Perform an operation using the given field.
         *
         * @param field the field to operate on
         */
        void doWith( Field field ) throws IllegalArgumentException, IllegalAccessException;
    }

    /**
     * Callback optionally used to filter fields to be operated on by a field callback.
     */
    @FunctionalInterface
    public interface FieldFilter {

        /**
         * Determine whether the given field matches.
         *
         * @param field the field to check
         */
        boolean matches( Field field );

        /**
         * Create a composite filter based on this filter <em>and</em> the provided filter.
         * <p>If this filter does not match, the next filter will not be applied.
         *
         * @param next the next {@code FieldFilter}
         * @return a composite {@code FieldFilter}
         * @throws IllegalArgumentException if the FieldFilter argument is {@code null}
         */
        default FieldFilter and( FieldFilter next ) {
            Precondition.notNull(next, "Next FieldFilter must not be null");
            return field -> matches(field) && next.matches(field);
        }
    }


}