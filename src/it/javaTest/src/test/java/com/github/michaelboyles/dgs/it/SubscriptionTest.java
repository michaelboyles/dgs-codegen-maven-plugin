package com.github.michaelboyles.dgs.it;

import com.github.michaeboyles.dgs.generated.ActiveUsersSubscription;
import com.github.michaeboyles.dgs.generated.sub.Person;
import org.reactivestreams.Publisher;
import java.util.List;

public class SubscriptionTest implements ActiveUsersSubscription {
    @Override
    public Publisher<List<Person>> activeUsers() {
        return null;
    }
}
