package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public Question createQuestion(final Question question, final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = this.userDao.getAuthToken( accessToken );

        if(userAuthTokenEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

        if(userAuthTokenEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException( "ATHR-002", "User is signed out.Sign in first to post a question");
        }

        if(!userAuthTokenEntity.getUser().getRole().equals( "admin" )){
            throw new AuthorizationFailedException( "ATHR-003","Unauthorized Access, Entered user is not an admin" );
        }

        question.setUser( userAuthTokenEntity.getUser() );

        Question createdQuestion = this.userDao.createQuestion( question );
        return createdQuestion;
    }
}
