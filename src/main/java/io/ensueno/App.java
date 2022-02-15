package io.ensueno;

import io.ensueno.reflection.Book;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {

        Class<?> bookClass = Class.forName("io.ensueno.reflection.Book");
        Constructor<?> constructor = bookClass.getConstructor(String.class);
        Book book = (Book) constructor.newInstance("myBook");

        Field a = Book.class.getDeclaredField("A");
        System.out.println(a.get(null));
        a.set(null, "AAAAAAAAA");
        System.out.println(a.get(null));

        Field b = Book.class.getDeclaredField("B");
        // private 일 경우 setAccessible true 로 변경해줘야 값을 가져올 수 있다.
        b.setAccessible(true);
        b.set(book, "BBBBBB");
        System.out.println(b.get(book));

        // 메소드 호출
        Method c = Book.class.getDeclaredMethod("c");
        c.invoke(book);

        Method sum = Book.class.getDeclaredMethod("sum", int.class, int.class);
        System.out.println(sum.invoke(book, 10, 20));

    }
}
