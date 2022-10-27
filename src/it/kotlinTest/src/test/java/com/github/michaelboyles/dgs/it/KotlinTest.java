package com.github.michaelboyles.dgs.it;

import org.junit.Test;

import com.github.michaeboyles.dgs.generated.DgsConstants;
import com.github.michaeboyles.dgs.generated.sub.Person;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KotlinTest {
    @Test
    public void constantName() {
        assertEquals("Person", DgsConstants.PERSON.TYPE_NAME);
    }

    @Test
    public void builder() {
        Person person = new Person("Michael", 28, true, new ArrayList<>());

        assertEquals("Michael", person.getName());
        assertEquals(Integer.valueOf(28), person.getAge());
        assertTrue(person.getLikesDogs());
    }
}
