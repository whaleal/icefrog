package com.whaleal.icefrog.core.util;

import com.whaleal.icefrog.core.convert.BasicType;
import com.whaleal.icefrog.core.exceptions.UtilException;
import com.whaleal.icefrog.core.lang.JarClassLoader;
import com.whaleal.icefrog.core.lang.Preconditions;
import com.whaleal.icefrog.core.lang.SimpleCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.whaleal.icefrog.core.collection.CollUtil.createHashSet;
import static com.whaleal.icefrog.core.collection.CollUtil.createLinkedList;

/**
 * <p>
 * 查找并装入类和资源的辅助类。
 * </p>
 * <p>
 * <code>ClassLoaderUtil</code>查找类和资源的效果， 相当于<code>ClassLoader.loadClass</code>
 * 方法和<code>ClassLoader.getResource</code>方法。 但<code>ClassLoaderUtil</code>
 * 总是首先尝试从<code>Thread.getContextClassLoader()</code>方法取得
 * <code>ClassLoader</code>中并装入类和资源。 这种方法避免了在多级<code>ClassLoader</code>
 * 的情况下，找不到类或资源的情况。
 * </p>
 * <p>
 * 假设有如下情况:
 * </p>
 * <ul>
 * <li>工具类<code>A</code>是从系统<code>ClassLoader</code>装入的(classpath)</li>
 * <li>类<code>B</code>是Web Application中的一个类，是由servlet引擎的<code>ClassLoader</code>
 * 动态装入的</li>
 * <li>资源文件<code>C.properties</code>也在Web Application中，只有servlet引擎的动态
 * <code>ClassLoader</code>可以找到它</li>
 * <li>类<code>B</code>调用工具类<code>A</code>的方法，希望通过类<code>A</code>取得资源文件
 * <code>C.properties</code></li>
 * </ul>
 * <p>
 * 如果类<code>A</code>使用
 * <code>getClass().getClassLoader().getResource(&quot;C.properties&quot;)</code>
 * ， 就会失败，因为系统<code>ClassLoader</code>不能找到此资源。
 * 但类A可以使用ClassLoaderUtil.getResource(&quot;C.properties&quot;)，就可以找到这个资源，
 * 因为ClassLoaderUtil调用<code>Thread.currentThead().getContextClassLoader()</code>
 * 取得了servlet引擎的<code>ClassLoader</code>， 从而找到了这个资源文件。
 * </p>
 * <p>
 * {@link ClassLoader}工具类
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class ClassLoaderUtil {

    /**
     * 数组类的结尾符: "[]"
     */
    public static final String ARRAY_SUFFIX = "[]";
    /**
     * 包名分界符: '.'
     */
    public static final char PACKAGE_SEPARATOR = StrUtil.C_DOT;
    /**
     * 内部类分界符: '$'
     */
    public static final char INNER_CLASS_SEPARATOR = '$';
    /**
     * The CGLIB class separator character "$$"
     */
    public static final String CGLIB_CLASS_SEPARATOR = "$$";
    /**
     * The ".class" file suffix
     */
    public static final String CLASS_FILE_SUFFIX = ".class";
    /**
     * 内部数组类名前缀: "["
     */
    private static final String INTERNAL_ARRAY_PREFIX = "[";
    /**
     * 内部非原始类型类名前缀: "[L"
     */
    private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";
    /**
     * 原始类型名和其class对应表，例如：int =》 int.class
     */
    private static final Map<String, Class<?>> PRIMITIVE_TYPE_NAME_MAP = new ConcurrentHashMap<>(32);
    private static final SimpleCache<String, Class<?>> CLASS_CACHE = new SimpleCache<>();

    static {
        List<Class<?>> primitiveTypes = new ArrayList<>(32);
        // 加入原始类型
        primitiveTypes.addAll(BasicType.PRIMITIVE_WRAPPER_MAP.keySet());
        // 加入原始类型数组类型
        primitiveTypes.add(boolean[].class);
        primitiveTypes.add(byte[].class);
        primitiveTypes.add(char[].class);
        primitiveTypes.add(double[].class);
        primitiveTypes.add(float[].class);
        primitiveTypes.add(int[].class);
        primitiveTypes.add(long[].class);
        primitiveTypes.add(short[].class);
        primitiveTypes.add(void.class);
        for (Class<?> primitiveType : primitiveTypes) {
            PRIMITIVE_TYPE_NAME_MAP.put(primitiveType.getName(), primitiveType);
        }
    }

    /**
     * 获取当前线程的{@link ClassLoader}
     *
     * @return 当前线程的class loader
     * @see Thread#getContextClassLoader()
     */
    public static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        } else {
            // 绕开权限检查
            return AccessController.doPrivileged(
                    (PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader());
        }
    }

    /**
     * 获取系统{@link ClassLoader}
     *
     * @return 系统{@link ClassLoader}
     * @see ClassLoader#getSystemClassLoader()
     * @since 1.0.0
     */
    public static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() == null) {
            return ClassLoader.getSystemClassLoader();
        } else {
            // 绕开权限检查
            return AccessController.doPrivileged(
                    (PrivilegedAction<ClassLoader>) ClassLoader::getSystemClassLoader);
        }
    }


    /**
     * 获取{@link ClassLoader}<br>
     * 获取顺序如下：<br>
     *
     * <pre>
     * 1、获取当前线程的ContextClassLoader
     * 2、获取当前类对应的ClassLoader
     * 3、获取系统ClassLoader（{@link ClassLoader#getSystemClassLoader()}）
     * </pre>
     *
     * @return 类加载器
     */
    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoaderUtil.class.getClassLoader();
            if (null == classLoader) {
                classLoader = getSystemClassLoader();
            }
        }
        return classLoader;
    }

    // ----------------------------------------------------------------------------------- loadClass

    /**
     * 加载类，通过传入类的字符串，返回其对应的类名，使用默认ClassLoader并初始化类（调用static模块内容和初始化static属性）<br>
     * 扩展{@link Class#forName(String, boolean, ClassLoader)}方法，支持以下几类类名的加载：
     *
     * <pre>
     * 1、原始类型，例如：int
     * 2、数组类型，例如：int[]、Long[]、String[]
     * 3、内部类，例如：java.lang.Thread.State会被转为java.lang.Thread$State加载
     * </pre>
     *
     * @param name 类名
     * @return 类名对应的类
     * @throws UtilException 包装{@link ClassNotFoundException}，没有类名对应的类时抛出此异常
     */
    public static Class<?> loadClass( String name ) throws UtilException {
        return loadClass(name, true);
    }

    /**
     * 加载类，通过传入类的字符串，返回其对应的类名，使用默认ClassLoader<br>
     * 扩展{@link Class#forName(String, boolean, ClassLoader)}方法，支持以下几类类名的加载：
     *
     * <pre>
     * 1、原始类型，例如：int
     * 2、数组类型，例如：int[]、Long[]、String[]
     * 3、内部类，例如：java.lang.Thread.State会被转为java.lang.Thread$State加载
     * </pre>
     *
     * @param name          类名
     * @param isInitialized 是否初始化类（调用static模块内容和初始化static属性）
     * @return 类名对应的类
     * @throws UtilException 包装{@link ClassNotFoundException}，没有类名对应的类时抛出此异常
     */
    public static Class<?> loadClass( String name, boolean isInitialized ) throws UtilException {
        return loadClass(name, null, isInitialized);
    }

    /**
     * 加载类，通过传入类的字符串，返回其对应的类名<br>
     * 此方法支持缓存，第一次被加载的类之后会读取缓存中的类<br>
     * 加载失败的原因可能是此类不存在或其关联引用类不存在<br>
     * 扩展{@link Class#forName(String, boolean, ClassLoader)}方法，支持以下几类类名的加载：
     *
     * <pre>
     * 1、原始类型，例如：int
     * 2、数组类型，例如：int[]、Long[]、String[]
     * 3、内部类，例如：java.lang.Thread.State会被转为java.lang.Thread$State加载
     * </pre>
     *
     * @param name          类名
     * @param classLoader   {@link ClassLoader}，{@code null} 则使用系统默认ClassLoader
     * @param isInitialized 是否初始化类（调用static模块内容和初始化static属性）
     * @return 类名对应的类
     * @throws UtilException 包装{@link ClassNotFoundException}，没有类名对应的类时抛出此异常
     */
    public static Class<?> loadClass( String name, ClassLoader classLoader, boolean isInitialized ) throws UtilException {
        Preconditions.notNull(name, "Name must not be null");

        // 加载原始类型和缓存中的类
        Class<?> clazz = loadPrimitiveClass(name);
        if (clazz == null) {
            clazz = CLASS_CACHE.get(name);
        }
        if (clazz != null) {
            return clazz;
        }

        if (name.endsWith(ARRAY_SUFFIX)) {
            // 对象数组"java.lang.String[]"风格
            final String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            final Class<?> elementClass = loadClass(elementClassName, classLoader, isInitialized);
            clazz = Array.newInstance(elementClass, 0).getClass();
        } else if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
            // "[Ljava.lang.String;" 风格
            final String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
            final Class<?> elementClass = loadClass(elementName, classLoader, isInitialized);
            clazz = Array.newInstance(elementClass, 0).getClass();
        } else if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
            // "[[I" 或 "[[Ljava.lang.String;" 风格
            final String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
            final Class<?> elementClass = loadClass(elementName, classLoader, isInitialized);
            clazz = Array.newInstance(elementClass, 0).getClass();
        } else {
            // 加载普通类
            if (null == classLoader) {
                classLoader = getClassLoader();
            }
            try {
                clazz = Class.forName(name, isInitialized, classLoader);
            } catch (ClassNotFoundException ex) {
                // 尝试获取内部类，例如java.lang.Thread.State =》java.lang.Thread$State
                clazz = tryLoadInnerClass(name, classLoader, isInitialized);
                if (null == clazz) {
                    throw new UtilException(ex);
                }
            }
        }

        // 加入缓存并返回
        return CLASS_CACHE.put(name, clazz);
    }

    /**
     * 加载原始类型的类。包括原始类型、原始类型数组和void
     *
     * @param name 原始类型名，比如 int
     * @return 原始类型类
     */
    public static Class<?> loadPrimitiveClass( String name ) {
        Class<?> result = null;
        if (StrUtil.isNotBlank(name)) {
            name = name.trim();
            if (name.length() <= 8) {
                result = PRIMITIVE_TYPE_NAME_MAP.get(name);
            }
        }
        return result;
    }

    /**
     * 创建新的{@link JarClassLoader}，并使用此Classloader加载目录下的class文件和jar文件
     *
     * @param jarOrDir jar文件或者包含jar和class文件的目录
     * @return {@link JarClassLoader}
     * @since 1.0.0
     */
    public static JarClassLoader getJarClassLoader( File jarOrDir ) {
        return JarClassLoader.load(jarOrDir);
    }

    /**
     * 加载外部类
     *
     * @param jarOrDir jar文件或者包含jar和class文件的目录
     * @param name     类名
     * @return 类
     * @since 1.0.0
     */
    public static Class<?> loadClass( File jarOrDir, String name ) {
        try {
            return getJarClassLoader(jarOrDir).loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new UtilException(e);
        }
    }

    // ----------------------------------------------------------------------------------- isPresent

    /**
     * 指定类是否被提供，使用默认ClassLoader<br>
     * 通过调用{@link #loadClass(String, ClassLoader, boolean)}方法尝试加载指定类名的类，如果加载失败返回false<br>
     * 加载失败的原因可能是此类不存在或其关联引用类不存在
     *
     * @param className 类名
     * @return 是否被提供
     */
    public static boolean isPresent( String className ) {
        return isPresent(className, null);
    }

    /**
     * 指定类是否被提供<br>
     * 通过调用{@link #loadClass(String, ClassLoader, boolean)}方法尝试加载指定类名的类，如果加载失败返回false<br>
     * 加载失败的原因可能是此类不存在或其关联引用类不存在
     *
     * @param className   类名
     * @param classLoader {@link ClassLoader}
     * @return 是否被提供
     */
    public static boolean isPresent( String className, ClassLoader classLoader ) {
        try {
            loadClass(className, classLoader, false);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }


    // ==========================================================================
    // 装入类的方法。
    // ==========================================================================


    /**
     * 从指定的调用者的<code>ClassLoader</code>装入类。
     * <p>
     *
     * @param className 要装入的类名
     * @param referrer  调用者类，如果为<code>null</code>，则该方法相当于
     *                  <code>Class.forName</code>
     * @return 已装入的类
     * @throws ClassNotFoundException 如果类没找到
     */
    public static Class<?> loadClass( String className, Class<?> referrer ) throws ClassNotFoundException {
        ClassLoader classLoader = getReferrerClassLoader(referrer);

        // 如果classLoader为null，表示从ClassLoaderUtil所在的classloader中装载，
        // 这个classloader未必是System class loader。
        return loadClass(className, classLoader);
    }

    /**
     * 从指定的<code>ClassLoader</code>中装入类。如果未指定<code>ClassLoader</code>， 则从装载
     * <code>ClassLoaderUtil</code>的<code>ClassLoader</code>中装入。
     * <p>
     *
     * @param className   要装入的类名
     * @param classLoader 从指定的<code>ClassLoader</code>中装入类，如果为<code>null</code>
     *                    ，表示从<code>ClassLoaderUtil</code>所在的class loader中装载
     * @return 已装入的类
     * @throws ClassNotFoundException 如果类没找到
     */
    public static Class<?> loadClass( String className, ClassLoader classLoader ) throws ClassNotFoundException {
        if (className == null) {
            return null;
        }

        if (classLoader == null) {
            return Class.forName(className);
        } else {
            return Class.forName(className, true, classLoader);
        }
    }


    /**
     * 取得调用者的class loader。
     * <p>
     *
     * @param referrer 调用者类
     * @return 调用者的class loader，如果referrer为<code>null</code>，则返回
     * <code>null</code>
     */
    private static ClassLoader getReferrerClassLoader( Class<?> referrer ) {
        ClassLoader classLoader = null;

        if (referrer != null) {
            classLoader = referrer.getClassLoader();

            // classLoader为null，说明referrer类是由bootstrap classloader装载的，
            // 例如：java.lang.String
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
        }

        return classLoader;
    }


    // ==========================================================================
    // 装入和查找资源文件的方法。
    // ==========================================================================

    /**
     * 从<code>ClassLoader</code>取得所有resource URL。按如下顺序查找:
     * <ol>
     * <li>在当前线程的<code>ClassLoader</code>中查找。</li>
     * <li>在装入自己的<code>ClassLoader</code>中查找。</li>
     * <li>通过<code>ClassLoader.getSystemResource</code>方法查找。</li>
     * </ol>
     * <p>
     *
     * @param resourceName 要查找的资源名，就是以&quot;/&quot;分隔的标识符字符串
     * @return resource的URL数组，如果没找到，则返回空数组。数组中保证不包含重复的URL。
     */
    public static URL[] getResources( String resourceName ) {
        List<URL> urls = createLinkedList();
        boolean found = false;

        // 首先试着从当前线程的ClassLoader中查找。
        found = getResources(urls, resourceName, getContextClassLoader(), false);

        // 如果没找到，试着从装入自己的ClassLoader中查找。
        if (!found) {
            found = getResources(urls, resourceName, ClassLoaderUtil.class.getClassLoader(), false);
        }

        // 最后的尝试: 在系统ClassLoader中查找(JDK1.2以上)，
        // 或者在JDK的内部ClassLoader中查找(JDK1.2以下)。
        if (!found) {
            found = getResources(urls, resourceName, null, true);
        }

        // 返回不重复的列表。
        return getDistinctURLs(urls);
    }

    /**
     * 从指定调用者所属的<code>ClassLoader</code>取得所有resource URL。
     * <p>
     *
     * @param resourceName 要查找的资源名，就是以&quot;/&quot;分隔的标识符字符串
     * @param referrer     调用者类，如果为<code>null</code>，表示在<code>ClassLoaderUtil</code>
     *                     的class loader中找
     * @return resource的URL数组，如果没找到，则返回空数组。数组中保证不包含重复的URL。
     */
    public static URL[] getResources( String resourceName, Class<?> referrer ) {
        ClassLoader classLoader = getReferrerClassLoader(referrer);
        List<URL> urls = createLinkedList();

        getResources(urls, resourceName, classLoader, classLoader == null);

        // 返回不重复的列表。
        return getDistinctURLs(urls);
    }

    /**
     * 从指定的<code>ClassLoader</code>中取得所有resource URL。如果未指定
     * <code>ClassLoader</code>， 则从装载<code>ClassLoaderUtil</code>的
     * <code>ClassLoader</code>中取得所有resource URL。
     * <p>
     *
     * @param resourceName 要查找的资源名，就是以&quot;/&quot;分隔的标识符字符串
     * @param classLoader  从指定的<code>ClassLoader</code>中查找
     * @return resource的URL数组，如果没找到，则返回空数组。数组中保证不包含重复的URL。
     */
    public static URL[] getResources( String resourceName, ClassLoader classLoader ) {
        List<URL> urls = createLinkedList();

        getResources(urls, resourceName, classLoader, classLoader == null);

        // 返回不重复的列表。
        return getDistinctURLs(urls);
    }

    /**
     * 在指定class loader中查找指定名称的resource，把所有找到的resource的URL放入指定的集合中。
     * <p>
     *
     * @param urlSet         存放resource URL的集合
     * @param resourceName   资源名
     * @param classLoader    类装入器
     * @param sysClassLoader 是否用system class loader装载资源
     * @return 如果找到，则返回<code>true</code>
     */
    private static boolean getResources( List<URL> urlSet, String resourceName, ClassLoader classLoader, boolean sysClassLoader ) {
        if (resourceName == null) {
            return false;
        }

        Enumeration<URL> i = null;

        try {
            if (classLoader != null) {
                i = classLoader.getResources(resourceName);
            } else if (sysClassLoader) {
                i = ClassLoader.getSystemResources(resourceName);
            }
        } catch (IOException e) {
        }

        if (i != null && i.hasMoreElements()) {
            while (i.hasMoreElements()) {
                urlSet.add(i.nextElement());
            }

            return true;
        }

        return false;
    }

    /**
     * 去除URL列表中的重复项。
     * <p>
     *
     * @param urls URL列表
     * @return 不重复的URL数组，如果urls为<code>null</code>，则返回空数组
     */
    private static URL[] getDistinctURLs( List<URL> urls ) {
        if (urls == null || urls.size() == 0) {
            return new URL[0];
        }

        Set<URL> urlSet = createHashSet();

        for (Iterator<URL> i = urls.iterator(); i.hasNext(); ) {
            URL url = i.next();

            if (urlSet.contains(url)) {
                i.remove();
            } else {
                urlSet.add(url);
            }
        }

        return urls.toArray(new URL[urls.size()]);
    }

    /**
     * <p>
     * 从<code>ClassLoader</code>取得resource URL。按如下顺序查找:
     * </p>
     * <ol>
     * <li>在当前线程的<code>ClassLoader</code>中查找。</li>
     * <li>在装入自己的<code>ClassLoader</code>中查找。</li>
     * <li>通过<code>ClassLoader.getSystemResource</code>方法查找。</li>
     * </ol>
     * <p>
     *
     * @param resourceName 要查找的资源名，就是以&quot;/&quot;分隔的标识符字符串
     * @return resource的URL
     */
    public static URL getResource( String resourceName ) {
        if (resourceName == null) {
            return null;
        }

        ClassLoader classLoader = null;
        URL url = null;

        // 首先试着从当前线程的ClassLoader中查找。
        classLoader = getContextClassLoader();

        if (classLoader != null) {
            url = classLoader.getResource(resourceName);

            if (url != null) {
                return url;
            }
        }

        // 如果没找到，试着从装入自己的ClassLoader中查找。
        classLoader = ClassLoaderUtil.class.getClassLoader();

        if (classLoader != null) {
            url = classLoader.getResource(resourceName);

            if (url != null) {
                return url;
            }
        }

        // 最后的尝试: 在系统ClassLoader中查找(JDK1.2以上)，
        // 或者在JDK的内部ClassLoader中查找(JDK1.2以下)。
        return ClassLoader.getSystemResource(resourceName);
    }

    /**
     * 从指定调用者所属的<code>ClassLoader</code>取得resource URL。
     * <p>
     *
     * @param resourceName 要查找的资源名，就是以&quot;/&quot;分隔的标识符字符串
     * @param referrer     调用者类，如果为<code>null</code>，表示在<code>ClassLoaderUtil</code>
     *                     的class loader中找。
     * @return resource URL，如果没找到，则返回<code>null</code>
     */
    public static URL getResource( String resourceName, Class<?> referrer ) {
        if (resourceName == null) {
            return null;
        }

        ClassLoader classLoader = getReferrerClassLoader(referrer);

        return classLoader == null ? ClassLoaderUtil.class.getClassLoader().getResource(resourceName) : classLoader.getResource(resourceName);
    }

    /**
     * 从指定的<code>ClassLoader</code>取得resource URL。
     * <p>
     *
     * @param resourceName 要查找的资源名，就是以&quot;/&quot;分隔的标识符字符串
     * @param classLoader  在指定classLoader中查找，如果为<code>null</code>，表示在
     *                     <code>ClassLoaderUtil</code>的class loader中找。
     * @return resource URL，如果没找到，则返回<code>null</code>
     */
    public static URL getResource( String resourceName, ClassLoader classLoader ) {
        if (resourceName == null) {
            return null;
        }

        return classLoader == null ? ClassLoaderUtil.class.getClassLoader().getResource(resourceName) : classLoader.getResource(resourceName);
    }

    /**
     * 从<code>ClassLoader</code>取得resource的输入流。 相当于
     * <code>getResource(resourceName).openStream()</code>。
     * <p>
     *
     * @param resourceName 要查找的资源名，就是以"/"分隔的标识符字符串
     * @return resource的输入流
     */
    public static InputStream getResourceAsStream( String resourceName ) {
        URL url = getResource(resourceName);

        try {
            if (url != null) {
                return url.openStream();
            }
        } catch (IOException e) {
            // 打开URL失败。
        }

        return null;
    }

    /**
     * 从<code>ClassLoader</code>取得resource的输入流。 相当于
     * <code>getResource(resourceName,
     * referrer).openStream()</code>。
     * <p>
     *
     * @param resourceName 要查找的资源名，就是以"/"分隔的标识符字符串
     * @param referrer     调用者类，如果为<code>null</code>，表示在<code>ClassLoaderUtil</code>
     *                     的class loader中找。
     * @return resource的输入流
     */
    public static InputStream getResourceAsStream( String resourceName, Class<?> referrer ) {
        URL url = getResource(resourceName, referrer);

        try {
            if (url != null) {
                return url.openStream();
            }
        } catch (IOException e) {
            // 打开URL失败。
        }

        return null;
    }

    /**
     * 从<code>ClassLoader</code>取得resource的输入流。 相当于
     * <code>getResource(resourceName,
     * classLoader).openStream()</code>。
     * <p>
     *
     * @param resourceName 要查找的资源名，就是以"/"分隔的标识符字符串
     * @param classLoader  在指定classLoader中查找，如果为<code>null</code>，表示在
     *                     <code>ClassLoaderUtil</code>的class loader中找。
     * @return resource的输入流
     */
    public static InputStream getResourceAsStream( String resourceName, ClassLoader classLoader ) {
        URL url = getResource(resourceName, classLoader);

        try {
            if (url != null) {
                return url.openStream();
            }
        } catch (IOException e) {
            // 打开URL失败。
        }

        return null;
    }

    // ==========================================================================
    // 查找class的位置。
    //
    // 类似于UNIX的which方法。
    // ==========================================================================

    /**
     * 从当前线程的<code>ClassLoader</code>中查找指定名称的类。
     * <p>
     *
     * @param className 要查找的类名
     * @return URL数组，列举了系统中所有可找到的同名类，如果未找到，则返回一个空数组
     */
    public static URL[] whichClasses( String className ) {
        return getResources(ClassUtil.getResourceNameForClass(className));
    }

    /**
     * 从当前线程的<code>ClassLoader</code>中查找指定名称的类。
     * <p>
     *
     * @param className 要查找的类名
     * @param referrer  调用者类，如果为<code>null</code>，表示在<code>ClassLoaderUtil</code>
     *                  的class loader中找。
     * @return URL数组，列举了系统中所有可找到的同名类，如果未找到，则返回一个空数组
     */
    public static URL[] whichClasses( String className, Class<?> referrer ) {
        return getResources(ClassUtil.getResourceNameForClass(className), referrer);
    }

    /**
     * 从当前线程的<code>ClassLoader</code>中查找指定名称的类。
     * <p>
     *
     * @param className   要查找的类名
     * @param classLoader 在指定classLoader中查找，如果为<code>null</code>，表示在
     *                    <code>ClassLoaderUtil</code>的class loader中找。
     * @return URL数组，列举了系统中所有可找到的同名类，如果未找到，则返回一个空数组
     */
    public static URL[] whichClasses( String className, ClassLoader classLoader ) {
        return getResources(ClassUtil.getResourceNameForClass(className), classLoader);
    }

    /**
     * 从当前线程的<code>ClassLoader</code>中查找指定名称的类。
     * <p>
     *
     * @param className 要查找的类名
     * @return 类文件的URL，如果未找到，则返回<code>null</code>
     */
    public static URL whichClass( String className ) {
        return getResource(ClassUtil.getResourceNameForClass(className));
    }

    /**
     * 从当前线程的<code>ClassLoader</code>中查找指定名称的类。
     * <p>
     *
     * @param className 要查找的类名
     * @param referrer  调用者类，如果为<code>null</code>，表示在<code>ClassLoaderUtil</code>
     *                  的class loader中找。
     * @return 类文件的URL，如果未找到，则返回<code>null</code>
     */
    public static URL whichClass( String className, Class<?> referrer ) {
        return getResource(ClassUtil.getResourceNameForClass(className), referrer);
    }

    /**
     * 从当前线程的<code>ClassLoader</code>中查找指定名称的类。
     * <p>
     *
     * @param className   要查找的类名
     * @param classLoader 在指定classLoader中查找，如果为<code>null</code>，表示在
     *                    <code>ClassLoaderUtil</code>的class loader中找。
     * @return 类文件的URL，如果未找到，则返回<code>null</code>
     */
    public static URL whichClass( String className, ClassLoader classLoader ) {
        return getResource(ClassUtil.getResourceNameForClass(className), classLoader);
    }


    // ----------------------------------------------------------------------------------- Private method start

    /**
     * 尝试转换并加载内部类，例如java.lang.Thread.State =》java.lang.Thread$State
     *
     * @param name          类名
     * @param classLoader   {@link ClassLoader}，{@code null} 则使用系统默认ClassLoader
     * @param isInitialized 是否初始化类（调用static模块内容和初始化static属性）
     * @return 类名对应的类
     * @since 1.0.0
     */
    private static Class<?> tryLoadInnerClass( String name, ClassLoader classLoader, boolean isInitialized ) {
        // 尝试获取内部类，例如java.lang.Thread.State =》java.lang.Thread$State
        final int lastDotIndex = name.lastIndexOf(PACKAGE_SEPARATOR);
        if (lastDotIndex > 0) {// 类与内部类的分隔符不能在第一位，因此>0
            final String innerClassName = name.substring(0, lastDotIndex) + INNER_CLASS_SEPARATOR + name.substring(lastDotIndex + 1);
            try {
                return Class.forName(innerClassName, isInitialized, classLoader);
            } catch (ClassNotFoundException ex2) {
                // 尝试获取内部类失败时，忽略之。
            }
        }
        return null;
    }
    // ----------------------------------------------------------------------------------- Private method end
}
