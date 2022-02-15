# Java Reflection에 대한 이해

Java를 사용한 Annotaiton & Reflection을 사용하는 방법에 대한 간단히 구현해 보았다.
사실 Spring에서는 ApplicationContext getBean을 통해 손쉽게 리플렉션할 수 있지만 원래를 이해하기 위해서 간략하게 구현한 예제이다.

자바 기동시 Class<T> 형태로 힙에 생성되어 지는 클래스 객체에 대해서 어떤 형태로 불러와서 사용할 수 있는지
이해하는게 목적인 코드로 간결하게 구현해 놓았다.

```java
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface Inject {
}
```

```java
public class BookService {
    @Inject
    BookRepository bookRepository;
}
```

```java
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ContainerService {

    public static <T> T getObject(Class<T> classType){
        T instance = createInstance(classType);
        // Stream을 통해 Fields 객체를 루프 돌면서 Inject를 가지고 있는 어노테이션을 찾아 
        // 해당 타입의 객체를 주입해준다.
        Arrays.stream(classType.getDeclaredFields()).forEach(f -> {
            if(f.getAnnotation(Inject.class) != null){
                Object fieldInstance = createInstance(f.getType());
                f.setAccessible(true);
                try {
                    // Inject Annotation을 찾아서 Reflection set 으로 주입 해준다.
                    f.set(instance, fieldInstance);
                } catch (IllegalAccessException e){
                    throw new RuntimeException(e);
                }
            }
        });
        return instance;
    }

    private static <T> T createInstance(Class<T> classType){
        try {
            return classType.getConstructor(null).newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
```

```java
import static org.junit.Assert.assertNotNull;

import io.ensueno.reflection.ContainerService;
import org.junit.Test;

public class ContainerServiceTest {

    @Test
    public void getObject(){
        BookService bookService = ContainerService.getObject(BookService.class);
        assertNotNull(bookService);
    }
}
```