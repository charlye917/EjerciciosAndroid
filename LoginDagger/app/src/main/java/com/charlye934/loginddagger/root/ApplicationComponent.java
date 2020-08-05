package com.charlye934.loginddagger.root;

import com.charlye934.loginddagger.login.LoginActivity;
import com.charlye934.loginddagger.login.LoginModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, LoginModule.class})
public interface ApplicationComponent {
    void inject(LoginActivity target);
}
