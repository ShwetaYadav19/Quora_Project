package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AuthenticateService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AuthenticateService authenticateService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create",produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,
                                                           @RequestHeader("authorization") final String authorization) throws  AuthorizationFailedException {

        Date date = new Date(  );


        Question question = new Question();
        question.setUuid( UUID.randomUUID().toString() );
        question.setContent( questionRequest.getContent() );
        question.setDate( new Timestamp( date.getTime() ) );

        Question createdQuestion = questionService.createQuestion(question , authorization);

        QuestionResponse questionResponse = new QuestionResponse().id( createdQuestion.getUuid() )
                                                                   .status( "QUESTION CREATED" );

        return new ResponseEntity<QuestionResponse>( questionResponse,HttpStatus.CREATED );

    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException, AuthorizationFailedException {

        UserEntity userEntity = this.authenticateService.getUser( authorization );

        List<Question> allQuestions = this.questionService.getAllQuestions();

        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<QuestionDetailsResponse>(  );

        for(Question q: allQuestions){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.setContent( q.getContent() );
            questionDetailsResponse.setId( q.getUuid() );
            questionDetailsResponses.add( questionDetailsResponse );

        }

        return new ResponseEntity<List<QuestionDetailsResponse>>( questionDetailsResponses,HttpStatus.OK );

    }

    @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getQuestionByUserId(@PathVariable("userId") String userId,
                                                                  @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity userEntity = this.authenticateService.getUser( authorization );

        UserEntity searchUser = this.authenticateService.getUserById( userId );

        List<Question> allQuestions = this.questionService.getAllQuestionsByUser(searchUser);

        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<QuestionDetailsResponse>(  );

        for(Question q: allQuestions){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.setContent( q.getContent() );
            questionDetailsResponse.setId( q.getUuid() );
            questionDetailsResponses.add( questionDetailsResponse );

        }

        return new ResponseEntity<List<QuestionDetailsResponse>>( questionDetailsResponses,HttpStatus.OK );

    }

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(@PathVariable("questionId") String questionId,  final QuestionEditRequest questionEditRequest,
                                                             @RequestHeader("authorization") final String authorization) throws  AuthorizationFailedException, InvalidQuestionException {

        UserEntity userEntity = this.authenticateService.getUser( authorization );
        Boolean isQuestionOwner = this.questionService.isQuestionOwner( questionId, userEntity );
        QuestionEditResponse questionEditResponse = new QuestionEditResponse();

        if(isQuestionOwner) {
            Date date = new Date();
            Question question = this.questionService.getQuestion( questionId );
            question.setContent( questionEditRequest.getContent() );
            question.setDate( new Timestamp( date.getTime() ) );
            Question createdQuestion = questionService.updateQuestion( question );
            questionEditResponse.id( createdQuestion.getUuid() ).status( "QUESTION EDITED" );
        }

        return new ResponseEntity<QuestionEditResponse>( questionEditResponse,HttpStatus.OK );

    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}")
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") String questionId,
                                                               @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserEntity userEntity = this.authenticateService.getUser( authorization );
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse();
        Boolean isUserAdminorOwner = this.questionService.isUserOwnerorAdmin( questionId, userEntity );

        if(isUserAdminorOwner) {
            Question deletedQuestion = questionService.deleteQuestion( questionId );
            questionDeleteResponse.id( deletedQuestion.getUuid() ).status( "QUESTION DELETED" );
        }

        return new ResponseEntity<QuestionDeleteResponse>( questionDeleteResponse,HttpStatus.OK );

    }
}
