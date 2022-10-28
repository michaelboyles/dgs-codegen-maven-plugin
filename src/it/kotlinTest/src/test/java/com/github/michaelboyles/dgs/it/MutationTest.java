package com.github.michaelboyles.dgs.it;

import com.github.michaeboyles.dgs.generated.SetAgeMutation;
import com.github.michaeboyles.dgs.generated.sub.Person;

public class MutationTest implements SetAgeMutation {
    @Override
    public Person setAge(String name, Integer age) {
        return null;
    }
}
