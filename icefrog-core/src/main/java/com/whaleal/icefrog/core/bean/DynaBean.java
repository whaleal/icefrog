package com.whaleal.icefrog.core.bean;

import com.whaleal.icefrog.core.clone.CloneSupport;
import com.whaleal.icefrog.core.lang.Precondition;
import com.whaleal.icefrog.core.util.ClassUtil;
import com.whaleal.icefrog.core.util.ReflectUtil;

import java.io.Serializable;
import java.util.Map;

/**
 * 动态Bean，通过反射对Bean的相关方法做操作<br>
 * 支持Map和普通Bean
 * 所谓的动态 主要是针对 Map接口的实现
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class DynaBean extends CloneSupport<DynaBean> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Class<?> beanClass;
    private final Object bean;

    /**
     * 构造
     *
     * @param beanClass Bean类
     * @param params    构造Bean所需要的参数
     */
    public DynaBean( Class<?> beanClass, Object... params ) {
        this(ReflectUtil.newInstance(beanClass, params));
    }

    /**
     * 构造
     *
     * @param beanClass Bean类
     */
    public DynaBean( Class<?> beanClass ) {
        this(ReflectUtil.newInstance(beanClass));
    }


    /**
     * 构造
     *
     * @param bean 原始Bean
     */
    public DynaBean( Object bean ) {
        Precondition.notNull(bean);
        if (bean instanceof DynaBean) {
            bean = ((DynaBean) bean).getBean();
        }
        this.bean = bean;
        this.beanClass = ClassUtil.getClass(bean);
    }

    //------------------------------------------------------------------------ Constructor start

    /**
     * 创建一个DynaBean
     *
     * @param bean 普通Bean
     * @return DynaBean
     */
    public static DynaBean create( Object bean ) {
        return new DynaBean(bean);
    }

    /**
     * 创建一个DynaBean
     *
     * @param beanClass Bean类
     * @return DynaBean
     */
    public static DynaBean create( Class<?> beanClass ) {
        return new DynaBean(beanClass);
    }

    /**
     * 创建一个DynaBean
     *
     * @param beanClass Bean类
     * @param params    构造Bean所需要的参数
     * @return DynaBean
     */
    public static DynaBean create( Class<?> beanClass, Object... params ) {
        return new DynaBean(beanClass, params);
    }
    //------------------------------------------------------------------------ Constructor end

    /**
     * 获得字段对应值
     *
     * @param <T>       属性值类型
     * @param fieldName 字段名
     * @return 字段值
     * @throws BeanException 反射获取属性值或字段值导致的异常
     */
    @SuppressWarnings("unchecked")
    public <T> T get( String fieldName ) throws BeanException {
        if (Map.class.isAssignableFrom(beanClass)) {
            return (T) ((Map<?, ?>) bean).get(fieldName);
        } else {
            final PropDesc prop = BeanUtil.getBeanDesc(beanClass).getProp(fieldName);
            if (null == prop) {
                throw new BeanException("No public field or get method for {}", fieldName);
            }
            return (T) prop.getValue(bean);
        }
    }

    /**
     * 检查是否有指定名称的bean属性
     *
     * @param fieldName 字段名
     * @return 是否有bean属性
     * @since 1.0.0
     */
    public boolean containsProp( String fieldName ) {
        return null != BeanUtil.getBeanDesc(beanClass).getProp(fieldName);
    }

    /**
     * 获得字段对应值，获取异常返回{@code null}
     *
     * @param <T>       属性值类型
     * @param fieldName 字段名
     * @return 字段值
     * @since 1.0.0
     */
    public <T> T safeGet( String fieldName ) {
        try {
            return get(fieldName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 设置字段值
     *
     * @param fieldName 字段名
     * @param value     字段值
     * @throws BeanException 反射获取属性值或字段值导致的异常
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void set( String fieldName, Object value ) throws BeanException {
        if (Map.class.isAssignableFrom(beanClass)) {
            ((Map) bean).put(fieldName, value);
        } else {
            final PropDesc prop = BeanUtil.getBeanDesc(beanClass).getProp(fieldName);
            if (null == prop) {
                throw new BeanException("No public field or set method for {}", fieldName);
            }
            prop.setValue(bean, value);
        }
    }

    /**
     * 执行原始Bean中的方法
     *
     * @param methodName 方法名
     * @param params     参数
     * @return 执行结果，可能为null
     */
    public Object invoke( String methodName, Object... params ) {
        return ReflectUtil.invoke(this.bean, methodName, params);
    }

    /**
     * 获得原始Bean
     *
     * @param <T> Bean类型
     * @return bean
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean() {
        return (T) this.bean;
    }

    /**
     * 获得Bean的类型
     *
     * @param <T> Bean类型
     * @return Bean类型
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> getBeanClass() {
        return (Class<T>) this.beanClass;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bean == null) ? 0 : bean.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DynaBean other = (DynaBean) obj;
        if (bean == null) {
            return other.bean == null;
        } else return bean.equals(other.bean);
    }

    @Override
    public String toString() {
        return this.bean.toString();
    }
}
