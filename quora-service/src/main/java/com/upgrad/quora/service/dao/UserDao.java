package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity existingUserName(final String userName) {
        try {
            UserEntity existingUserName = this.entityManager.createNamedQuery( "userByUserName", UserEntity.class )
                    .setParameter( "userName", userName ).getSingleResult();
            return existingUserName;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity existingEmail(final String email) {
        try {
            UserEntity existingEmail = this.entityManager.createNamedQuery( "userByEmail", UserEntity.class )
                    .setParameter( "email", email ).getSingleResult();
            return existingEmail;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity signupUser(final UserEntity userEntity) {
        this.entityManager.persist( userEntity );
        return userEntity;
    }

    public UserAuthTokenEntity createAuthToken(UserAuthTokenEntity userAuthTokenEntity) {
        this.entityManager.persist( userAuthTokenEntity );
        return userAuthTokenEntity;
    }

    public void updateUser(UserEntity userEntity) {
        this.entityManager.merge( userEntity );
    }

    public UserAuthTokenEntity getAuthToken(final String accessToken){
        try {
            return this.entityManager.createNamedQuery( "userAuthTokenByAccessToken", UserAuthTokenEntity.class )
                    .setParameter( "accessToken", accessToken ).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public UserAuthTokenEntity updateUserAuthTokenEntity(UserAuthTokenEntity userAuthTokenEntity) {
        this.entityManager.merge( userAuthTokenEntity );
        return userAuthTokenEntity;
    }

    public UserEntity getUser(final String userid){
        try {
            return this.entityManager.createNamedQuery( "userById", UserEntity.class ).setParameter( "uuid", userid )
                    .getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }
}
