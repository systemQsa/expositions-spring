package com.myproject.expo.expositions.service.impl;

import com.myproject.expo.expositions.build.Build;
import com.myproject.expo.expositions.dto.ExpoDto;
import com.myproject.expo.expositions.dto.UserDto;
import com.myproject.expo.expositions.entity.Exposition;
import com.myproject.expo.expositions.entity.Status;
import com.myproject.expo.expositions.entity.User;
import com.myproject.expo.expositions.exception.custom.ExpoException;
import com.myproject.expo.expositions.exception.custom.UserException;
import com.myproject.expo.expositions.repository.ExpoRepo;
import com.myproject.expo.expositions.repository.UserRepo;
import com.myproject.expo.expositions.service.UserService;
import com.myproject.expo.expositions.service.facade.UserServiceFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * The UserServiceLogic class do transfer operations for user requests,do logic with requests
 */
@Service
@Qualifier("userServiceLogic")
public class UserServiceImpl implements UserService {
    private static final Logger log = LogManager.getLogger(UserServiceImpl.class);
    private final UserRepo userRepo;
    private final ExpoRepo expoRepo;
    private final Build<ExpoDto, Exposition> buildExpos;
    private final UserServiceFacade userServiceFacade;
    private final Build<UserDto, User> build;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, ExpoRepo expoRepo, @Qualifier("expoBuild") Build<ExpoDto,
            Exposition> buildExpos, UserServiceFacade userServiceFacade,
                           @Qualifier("getUserBuild") Build<UserDto, User> build) {
        this.userRepo = userRepo;
        this.expoRepo = expoRepo;
        this.buildExpos = buildExpos;
        this.userServiceFacade = userServiceFacade;
        this.build = build;
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public User save(UserDto userDto) {
        return userServiceFacade.save(userDto);
    }

    @Override
    public Page<UserDto> getAll(Pageable pageable) {
        Page<User> all = userRepo.findAll(pageable);
        return all.map(build::toDto);
    }

    @Override
    public boolean blockUnblock(Long id, String status) {
        Long statusId = Arrays.stream(Status.values())
                .filter(elem -> elem.getName().equals(status))
                .mapToLong(Status::getStatusId)
                .findFirst()
                .orElse(1);
        int res = userRepo.changeStatus(statusId, id);
        return res > 0;
    }

    @Override
    public User topUpBalance(User user, BigDecimal amount) {
        return userServiceFacade.topUpBalance(user, amount);
    }

    @Transactional
    @Override
    public boolean buyExpo(User user, Exposition expo) {
        checkCaseExpoStatusCanceledUserBalanceNotOk(user, expo);
        try {
            userServiceFacade.buyExposition(expo, user);
        } catch (Exception e) {
            log.warn("User with email {} cannot buy expo id {} = {}", user.getEmail(), expo.getIdExpo(), expo.getName());
            throw new ExpoException("err.buy_expo");
        }
        return true;
    }

    @Override
    public Exposition getExpoById(Long id) {
        return expoRepo.getById(id);
    }

    private void checkCaseExpoStatusCanceledUserBalanceNotOk(User user, Exposition expo) {
        if (Status.CANCELED.getStatusId() == expo.getStatusId()) {
            throw new UserException("err.canceled_expo");
        }
        if (user.getBalance().compareTo(expo.getPrice()) <= 0) {
            throw new UserException("err.not_enough_balance");
        }
    }

    @Override
    public Page<ExpoDto> getUserExpos(Page<Exposition> resList) {
        if (resList.getSize() == 0) {
            throw new UserException("err.user_dont_have_expos");
        }
        return resList.map(buildExpos::toDto);
    }

    @Override
    public Page<Exposition> getAllExposByStatusIdAndUser(Integer statusId, User user, Pageable pageable) {
        return expoRepo.findExpositionsByUsersAndStatusId(user, statusId, pageable);
    }

    @Override
    public int changeEmail(String oldEmail, String newEmail) {
        int res = userRepo.changeEmail(oldEmail, newEmail);
        if (res <= 0) {
            log.warn("Cannot change user old email {} to new one {}", oldEmail, newEmail);
            throw new UserException("err.change_user_email");
        }
        return res;
    }
}
