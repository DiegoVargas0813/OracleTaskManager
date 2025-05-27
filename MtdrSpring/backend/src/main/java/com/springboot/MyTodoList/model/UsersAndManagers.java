package com.springboot.MyTodoList.model;

import java.util.List;

public class UsersAndManagers {
    private List<User> users;
    private List<Manager> managers;

    public UsersAndManagers(List<User> users, List<Manager> managers) {
        this.users = users;
        this.managers = managers;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Manager> getManagers() {
        return managers;
    }

    public void setManagers(List<Manager> managers) {
        this.managers = managers;
    }
}
