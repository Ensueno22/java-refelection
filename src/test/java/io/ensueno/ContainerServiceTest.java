package io.ensueno;

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
