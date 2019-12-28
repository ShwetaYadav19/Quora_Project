package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;


    public Question createQuestion(Question question) {
        this.entityManager.persist( question );
        return question;
    }

    public List<Question> getAllQuestionsByUser(UserEntity user) {
        List<Question> allQuestions = this.entityManager.createNamedQuery( "questionByUser", Question.class )
                .setParameter( "user", user ).getResultList();
        return allQuestions;
    }

    public List<Question> getAllQuestions() {
        List<Question> allQuestions = this.entityManager.createNamedQuery( "allQuestions", Question.class ).getResultList();
        return allQuestions;
    }

    public Question getQuestion(final String questionId){
        try{
           return this.entityManager.createNamedQuery( "questionById",Question.class ).setParameter( "questionId",questionId )
                    .getSingleResult();
        }   catch (NoResultException nre){
            return null;
        }

    }

    public Question updateQuestion(Question question) {
        this.entityManager.merge( question );
        return question;
    }
}
