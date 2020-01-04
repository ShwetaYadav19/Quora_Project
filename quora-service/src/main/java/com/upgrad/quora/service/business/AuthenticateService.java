package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AuthenticateService {

    @Autowired
    private UserDao userDao;


    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity authenticateUser(final String userName, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = this.userDao.existingUserName( userName );

        if (userEntity == null) {
            throw new AuthenticationFailedException( "ATH-001", "This username does not exist" );
        }

        String encryptedPassword = this.passwordCryptographyProvider.encrypt( password, userEntity.getSalt() );

        if (encryptedPassword.equals( userEntity.getPassword() )) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider( encryptedPassword );
            UserAuthTokenEntity userAuthTokenEntity = new UserAuthTokenEntity();
            userAuthTokenEntity.setUser( userEntity );
            userAuthTokenEntity.setUuid( UUID.randomUUID().toString() );
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours( 8 );

            userAuthTokenEntity.setAccessToken( jwtTokenProvider.generateToken( userEntity.getUuid(), now, expiresAt ) );

            userAuthTokenEntity.setLoginAt( now );
            userAuthTokenEntity.setExpiresAt( expiresAt );

            userDao.createAuthToken( userAuthTokenEntity );

            userDao.updateUser( userEntity );

            return userAuthTokenEntity;
        } else {
            throw new AuthenticationFailedException( "ATH-002", "Password failed" );
        }

    }


    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity logoutUser(final String accessToken) throws SignOutRestrictedException {
        UserAuthTokenEntity userAuthTokenEntity = this.userDao.getAuthToken( accessToken );

        if (userAuthTokenEntity == null) {
            throw new SignOutRestrictedException( "SGR-001", "User is not Signed in" );
        }
        return userAuthTokenEntity;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity updateUserAuthTokeEntity(UserAuthTokenEntity userAuthTokenEntity) {
        return this.userDao.updateUserAuthTokenEntity( userAuthTokenEntity );
    }

    public UserEntity getUser(final String accessToken) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = this.userDao.getAuthToken( accessToken );
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException( "ATHR-001", "User has not signed in" );
        }

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException( "ATHR-002", "User is signed out.Sign in first to post a question" );
        }

        return userAuthTokenEntity.getUser();

    }

    public UserEntity getUserById(final String userId) throws  UserNotFoundException {

        UserEntity userEntity = this.userDao.getUser( userId );

        if (userEntity == null) {
            throw new UserNotFoundException( "USR-001", "User with entered uuid whose question details are to be seen does not exist" );
        }

        return userEntity;

    }

}
