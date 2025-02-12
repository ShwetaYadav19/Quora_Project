package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.AuthenticateService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.Answer;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AuthenticateService authenticateService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create",produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest, @PathVariable("questionId") final String questionId,
                                                           @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        Question question = this.answerService.getQuestionById( questionId );
        UserEntity user = this.answerService.getUserForQuestion( authorization );

        Date date = new Date();
        Answer answer = new Answer();
        answer.setUuid( UUID.randomUUID().toString() );
        answer.setAns( answerRequest.getAnswer());
        answer.setDate(new Timestamp( date.getTime()));
        answer.setQuestion( question );
        answer.setUser( user );

        Answer createdAnswer = answerService.createAnswer(answer);

        AnswerResponse answerResponse = new AnswerResponse().id( createdAnswer.getUuid() )
                                                                   .status( "ANSWER CREATED" );

        return new ResponseEntity<AnswerResponse>( answerResponse,HttpStatus.CREATED );

    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(final AnswerEditRequest answerEditRequest, @PathVariable("answerId") final String answerId,
                                                           @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {

        Answer answer = this.answerService.getAnswer( answerId );
        UserEntity user = this.answerService.getUserForQuestion( authorization );
        Boolean isOwner = this.answerService.isAnswerOwner( answerId, user );
        AnswerEditResponse answerResponse = new AnswerEditResponse();

        if(isOwner) {
            Date date = new Date();
            answer.setAns( answerEditRequest.getContent() );
            answer.setDate( new Timestamp( date.getTime() ) );

            Answer updatedAnswer = answerService.updateAnswer( answer );

            answerResponse.id( updatedAnswer.getUuid() )
                    .status( "ANSWER UPDATED" );
        }

        return new ResponseEntity<AnswerEditResponse>( answerResponse,HttpStatus.CREATED );

    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}")
    public ResponseEntity<AnswerEditResponse> delete( @PathVariable("answerId") final String answerId,
                                                           @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {

        Answer answer = this.answerService.getAnswer( answerId );
        UserEntity user = this.answerService.getUserForQuestion( authorization );
        Boolean isOwner = this.answerService.isAnswerOwner( answerId, user );
        Boolean isAdmin = this.answerService.isAdmin( answerId, user );
        AnswerEditResponse answerResponse = new AnswerEditResponse();

        if(isOwner || isAdmin) {
            Answer deletedAnswer = answerService.deleteAnswer( answer );

            answerResponse.id( deletedAnswer.getUuid() )
                    .status( "ANSWER UPDATED" );
        }

        return new ResponseEntity<AnswerEditResponse>( answerResponse,HttpStatus.CREATED );

    }

    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswerForGivenQuestion(@PathVariable("questionId") final String questionId,
                                                             @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException, InvalidQuestionException {

        UserEntity userEntity = this.answerService.authenticateUser( authorization );

        Question question = this.questionService.getQuestion( questionId );

        List<Answer> answers = this.answerService.getAllAnswers(question);
        List<AnswerDetailsResponse> answerDetailsResponses = new ArrayList<>(  );
        for(Answer answer: answers) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
            answerDetailsResponse.setAnswerContent( answer.getAns() );
            answerDetailsResponse.setId( answer.getUuid() );
            answerDetailsResponse.setQuestionContent( answer.getQuestion().getContent() );

            answerDetailsResponses.add( answerDetailsResponse );

        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponses, HttpStatus.OK);

    }

}
