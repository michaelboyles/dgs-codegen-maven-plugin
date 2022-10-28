package com.github.michaelboyles.dgs.it;

import org.junit.Test;

import com.github.michaeboyles.dgs.generated.DgsConstants;
import com.github.michaeboyles.dgs.generated.sub.IPerson;
import com.github.michaeboyles.dgs.generated.sub.Person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeneratedTest {
    @Test
    public void constantName() {
        assertEquals("Person", DgsConstants.PERSON.TYPE_NAME);
    }

    @Test
    public void builder() {
        IPerson person = Person.newBuilder()
            .name("Michael")
            .age(28)
            .likesDogs(true)
            .build();

        assertEquals("Michael", person.getName());
        assertEquals(Integer.valueOf(28), person.getAge());
        assertTrue(person.getLikesDogs());
    }
}
