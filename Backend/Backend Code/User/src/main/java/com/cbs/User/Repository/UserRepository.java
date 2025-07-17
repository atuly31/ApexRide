package com.cbs.User.Repository;

import com.cbs.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User,Long> {

    Optional<User> findByEmail (String email);
    Optional<User> findByUserName(String UserName);
    boolean existsByEmail (String email);
    boolean existsByPhoneNumber (String phoneNumber);

    Optional<User> findByPhoneNumber(String phoneNumber);
}
