package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserProfileServcie;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private UserProfileServcie userProfileServcie;

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId") String userId,
                                                       @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, UserNotFoundException {


        UserEntity userEntity = userProfileServcie.getUserProfile( userId, accessToken );

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().userName( userEntity.getUserName() )
                                                       .firstName( userEntity.getFirstName() ).lastName( userEntity.getLastName() )
                                                        .emailAddress( userEntity.getEmail() ).contactNumber( userEntity.getContactNumber() )
                                                         .country( userEntity.getCountry() ).aboutMe( userEntity.getAboutMe() ).dob( userEntity.getDob() );

        return new ResponseEntity<>( userDetailsResponse,HttpStatus.OK );

    }


}
