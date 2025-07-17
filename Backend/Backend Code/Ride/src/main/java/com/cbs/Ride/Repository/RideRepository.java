package com.cbs.Ride.Repository;

import com.cbs.Ride.Dto.RideDto;
import com.cbs.Ride.Entity.Rides;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RideRepository extends JpaRepository<Rides,Long> {

    List<Rides> findByUserId(long id);
    List<Rides> findByDriverId(long id);
    Optional<Rides> findFirstByDriverIdAndStatusOrderByStartTimeDesc(Long driverId, Rides.RideStatus status);
    Optional<Rides> findFirstByUserIdAndStatusOrderByStartTimeDesc(Long userId, Rides.RideStatus status);


}
