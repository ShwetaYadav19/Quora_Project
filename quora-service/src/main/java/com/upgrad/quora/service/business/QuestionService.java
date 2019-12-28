package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticateService authenticateService;
    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public Question createQuestion(final Question question, final String accessToken) throws  AuthorizationFailedException {
        UserEntity userEntity = authenticateService.getUser( accessToken );

        question.setUser( userEntity );

        Question createdQuestion = this.questionDao.createQuestion( question );
        return createdQuestion;
    }


    public String getAllQuestions() {
        List<Question> allQuestions =  this.questionDao.getAllQuestions();
        StringBuilder content = new StringBuilder(  );

        for(Question q: allQuestions){
            content.append( q.toString() +" " );
        }

        return content.toString();
    }

    public String getAllQuestionsByUser(final UserEntity user) {
        List<Question> allQuestions =  this.questionDao.getAllQuestionsByUser(user);
        StringBuilder content = new StringBuilder(  );

        for(Question q: allQuestions){
            content.append( q.getUuid()+ " -> " +q.toString() + "\r\n" );
        }

        return content.toString();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public  Question getQuestion(final String questionId) throws InvalidQuestionException {

        Question question =  this.questionDao.getQuestion( questionId );
        if(question == null){
            throw new InvalidQuestionException("QUES-001","'Entered question uuid does not exist");
        }

      return question;

    }



    public Boolean isQuestionOwner(final String questionId,final UserEntity userEntity) throws AuthorizationFailedException, InvalidQuestionException {

        Question question = getQuestion( questionId );
        if (!userEntity.equals( question.getUser() )) {
            throw new AuthorizationFailedException( "ATHR-003", "Only the question owner can edit the question" );
        }
        return true;

    }


    public Boolean isUserOwnerorAdmin(final String questionId,final UserEntity userEntity) throws AuthorizationFailedException, InvalidQuestionException {
        Question question = getQuestion( questionId );
        if (!userEntity.getRole().equals( "admin" ) || !userEntity.equals( question.getUser() ) ) {
            throw new AuthorizationFailedException( "ATHR-003", "Only the question owner or admin can delete the question" );
        }
        return true;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Question updateQuestion(Question question) {
        return this.questionDao.updateQuestion( question );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Question deleteQuestion(String questionId) {
        Question question = this.questionDao.getQuestion( questionId );
        this.questionDao.deleteQuestion(question);
        return question;
    }
}
