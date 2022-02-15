# Java Reflection에 대한 이해

Java를 사용한 Annotaiton & Reflection을 사용하는 방법에 대한 간단히 구현해 보았다.
사실 Spring에서는 ApplicationContext getBean을 통해 손쉽게 리플렉션할 수 있지만 원래를 이해하기 위해서 간략하게 구현한 예제이다.

자바 기동시 Class<T> 형태로 힙에 생성되어 지는 클래스 객체에 대해서 어떤 형태로 불러와서 사용할 수 있는지
이해하는게 목적인 코드로 간결하게 구현해 놓았다.

리플렉션 사용시 주의할 것
* 지나친 사용은 성능 이슈를 야기할 수 있다. 꼭 필요한 경우에만 사용
* 컴파일 타임에 확인되지 않고 런타임 시에만 발생되는 문제를 만들 가능성이 있다.
* 접근 지시자를 무시할 수 있다. (private를 setAccessible(true) 함수 같은 사용을 통해)
* 스프링 같은 경우 싱글턴 스코프에 대한 설정을 알어서 해주나 간략하게 구현한 샘플의 경우 객체를 계속 생성 된다.

스프링
* 의존성 주입
* MVC 뷰에서 넘어온 데이터를 객체에 바인딩 할 때

하이버네이트
* @Entity 클래스에 Setter가 없다면 리플렉션을 사용한다.

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