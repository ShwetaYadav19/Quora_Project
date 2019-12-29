package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.Answer;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AnswerDao answerDao;



    @Transactional(propagation = Propagation.REQUIRED)
    public  Question getQuestionById(final String questionId) throws InvalidQuestionException {

        Question question =  this.questionDao.getQuestion( questionId );
        if(question == null){
            throw new InvalidQuestionException("QUES-001","The question entered is invalid");
        }

      return question;

    }

    public UserEntity getUserForQuestion(final String accessToken) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = this.userDao.getAuthToken( accessToken );
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException( "ATHR-001", "User has not signed in" );
        }

        if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException( "ATHR-002", "User is signed out.Sign in first to post an answer" );
        }

        return userAuthTokenEntity.getUser();

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Answer createAnswer(Answer answer) {
        this.answerDao.createAnswer( answer );
        return answer;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public  Answer getAnswer(final String answerId) throws AnswerNotFoundException {

        Answer answer =  this.answerDao.getAnswer( answerId );
        if(answer == null){
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }

        return answer;

    }

    public Boolean isAnswerOwner(final String answerId,final UserEntity userEntity) throws AuthorizationFailedException, AnswerNotFoundException {

        Answer answer = getAnswer( answerId );
        if (!userEntity.equals( answer.getUser() )) {
            throw new AuthorizationFailedException( "ATHR-003", "Only the answer owner can edit the answer" );
        }
        return true;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Answer updateAnswer(Answer answer) {
       return this.answerDao.updateAnswer(answer);
    }
}
