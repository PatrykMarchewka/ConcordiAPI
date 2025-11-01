package com.patrykmarchewka.concordiapi.HydrationContracts.User;

public interface UserWithCredentials extends UserIdentity{
    String getLogin();
    String getPassword();
}
