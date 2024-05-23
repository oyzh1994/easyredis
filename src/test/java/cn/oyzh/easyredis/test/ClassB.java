package cn.oyzh.easyredis.test;

import java.lang.reflect.Method;

/**
 * @author oyzh
 * @since 2024/5/20
 */
public class ClassB extends ClassA {


    public void m1() {

    }

    public void m2() {

    }

    public void m3() {

    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException {
        ClassB classB = ClassB.class.newInstance();
        Method method1 = classB.getClass().getSuperclass().getMethod("m4");
        Method method2 = classB.getClass().getMethod("m4");
        System.out.println("method1=" + method1);
        System.out.println("method2=" + method2);
        Method method3 = classB.getClass().getSuperclass().getDeclaredMethod("m3");
        Method method4 = classB.getClass().getMethod("m3");
        System.out.println("method3=" + method3);
        System.out.println("method4=" + method4);
    }
}
