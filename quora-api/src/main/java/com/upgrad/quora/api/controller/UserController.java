package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticateService;
import com.upgrad.quora.service.business.SignupUserService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private SignupUserService signupUserService;

    @Autowired
    private AuthenticateService authenticateService;

    @RequestMapping(method = RequestMethod.POST, path = "/user/signup",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signupUser(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        UserEntity userEntity = new UserEntity();
        userEntity.setUuid( UUID.randomUUID().toString() );
        userEntity.setUserName( signupUserRequest.getUserName() );
        userEntity.setPassword( signupUserRequest.getPassword() );
        userEntity.setEmail( signupUserRequest.getEmailAddress() );
        userEntity.setFirstName( signupUserRequest.getFirstName() );
        userEntity.setLastName( signupUserRequest.getLastName() );
        userEntity.setContactNumber( signupUserRequest.getContactNumber() );
        userEntity.setDob( signupUserRequest.getDob() );
        userEntity.setRole("nonadmin" );
        userEntity.setCountry( signupUserRequest.getCountry() );
        userEntity.setAboutMe( signupUserRequest.getAboutMe() );

        UserEntity createdUser = this.signupUserService.signupUser( userEntity );

        SignupUserResponse signupUserResponse = new SignupUserResponse().id( createdUser.getUuid() )
                .status( "'USER SUCCESSFULLY REGISTERED" );

        return new ResponseEntity<SignupUserResponse>(signupUserResponse,HttpStatus.CREATED);

    }


    @RequestMapping(method = RequestMethod.POST, path = "/user/signin",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signinUser(@RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException {
        byte[] decode = Base64.getDecoder().decode(authorization.split( "Basic " )[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        UserAuthTokenEntity userAuthTokenEntity = this.authenticateService.
                authenticateUser( decodedArray[0],decodedArray[1] );

        SigninResponse signupUserResponse = new SigninResponse().id( userAuthTokenEntity.getUser().getUuid() )
                .message( "SIGNED IN SUCCESSFULLY" );

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthTokenEntity.getAccessToken());

        ResponseEntity<SigninResponse> responseEntity = new ResponseEntity<SigninResponse>( signupUserResponse, headers,
                HttpStatus.OK);

        return responseEntity;

    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signoutUser(@RequestHeader("authorization") final String accessToken) throws SignOutRestrictedException {

        UserAuthTokenEntity loggedInUser = this.authenticateService.logoutUser( accessToken );

        loggedInUser.setLogoutAt( ZonedDateTime.now() );

        UserAuthTokenEntity loggedOutUser = this.authenticateService.updateUserAuthTokeEntity( loggedInUser );

        SignoutResponse signupUserResponse = new SignoutResponse().id( loggedInUser.getUuid() )
                .message( "SIGNED OUT SUCCESSFULLY" );

        return new ResponseEntity<SignoutResponse>(signupUserResponse, HttpStatus.OK);

    }



}
