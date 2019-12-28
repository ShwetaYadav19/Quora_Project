package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public Question createQuestion(final Question question, final String accessToken) throws AuthenticationFailedException {
        UserEntity userEntity = getUser( accessToken );

        question.setUser( userEntity );

        Question createdQuestion = this.userDao.createQuestion( question );
        return createdQuestion;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(final String accessToken) throws AuthenticationFailedException {

            UserAuthTokenEntity userAuthTokenEntity = this.userDao.getAuthToken( accessToken );
            if (userAuthTokenEntity == null) {
                throw new AuthenticationFailedException( "ATHR-001", "User has not signed in" );
            }

            if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthenticationFailedException( "ATHR-002", "User is signed out.Sign in first to post a question" );
            }

            return userAuthTokenEntity.getUser();

    }

    public String getAllQuestions(final UserEntity user) {
        List<Question> allQuestions =  this.userDao.getAllQuestions(user);
        StringBuilder content = new StringBuilder(  );

        for(Question q: allQuestions){
            content.append( q.toString() +" " );
        }

        return content.toString();
    }
}
