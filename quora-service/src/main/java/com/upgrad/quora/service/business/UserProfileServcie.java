package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServcie {

    @Autowired
    private UserDao userDao;


    public UserEntity getUserProfile(final String userId, final String acccessToken) throws AuthorizationFailedException,
            UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = this.userDao.getAuthToken( acccessToken );

        if(userAuthTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }

        UserEntity existingUser = this.userDao.getUser( userId );

        if(existingUser == null){
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        }

        return existingUser;

    }

}
