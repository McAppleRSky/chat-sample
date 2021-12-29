package rarus.chat._3_service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceImpl implements UserService{

    Set<String> users = Collections.newSetFromMap(new ConcurrentHashMap<>());


    @Override
    public void addUser(String name) {
        users.add(name);
    }

}
