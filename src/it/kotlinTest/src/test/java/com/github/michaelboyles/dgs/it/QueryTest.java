package com.github.michaelboyles.dgs.it;

import com.github.michaeboyles.dgs.generated.MeQuery;
import com.github.michaeboyles.dgs.generated.YouQuery;
import com.github.michaeboyles.dgs.generated.sub.Person;

public class QueryTest implements MeQuery, YouQuery {
    @Override
    public Person me() {
        return null;
    }

    @Override
    public Person you() {
        return null;
    }
}
