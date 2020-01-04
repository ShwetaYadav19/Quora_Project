package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Answer;
import com.upgrad.quora.service.entity.Question;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

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

    public Answer deleteAnswer(Answer answer) {
        this.entityManager.remove( answer );
        return answer;
    }

    public List<Answer> getAllAnswers(Question question) {
        return this.entityManager.createNamedQuery( "answerByQuestionId", Answer.class ).setParameter( "question", question )
                .getResultList();
    }
}
