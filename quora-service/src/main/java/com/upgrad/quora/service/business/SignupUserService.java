package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupUserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signupUser(final UserEntity userEntity) throws SignUpRestrictedException {
        UserEntity existingUserName = this.userDao.existingUserName( userEntity.getUserName() );
        if (existingUserName != null) {
            throw new SignUpRestrictedException( "SGR-001", "Try any other Username, this Username has already been taken" );
        }

        UserEntity existingEmail = this.userDao.existingEmail( userEntity.getEmail() );
        if (existingEmail != null) {
            throw new SignUpRestrictedException( "SGR-002", "This user has already been registered, try with any other emailId" );
        }

        String[] encryptedPassword = this.passwordCryptographyProvider.encrypt( userEntity.getPassword() );
        userEntity.setSalt( encryptedPassword[0] );
        userEntity.setPassword( encryptedPassword[1] );
        return this.userDao.signupUser( userEntity );
    }

}
