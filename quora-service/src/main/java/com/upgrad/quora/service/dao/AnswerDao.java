package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Answer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;


    public Answer createAnswer(Answer answer) {
        this.entityManager.persist( answer );
        return answer;
    }

    public Answer getAnswer(final String answerId){
        try{
            return this.entityManager.createNamedQuery( "answerById",Answer.class ).setParameter( "answerId",answerId )
                    .getSingleResult();
        }   catch (NoResultException nre){
            return null;
        }

    }


    public Answer updateAnswer(Answer answer) {
        this.entityManager.merge( answer );
        return answer;
    }
}
