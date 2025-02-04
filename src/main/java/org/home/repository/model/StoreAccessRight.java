package org.home.repository.model;

public interface StoreAccessRight {
    int NONE = 0;
    int READ = 1;
    int WRITE = 2;
    int CONTROL = READ | WRITE;
}
