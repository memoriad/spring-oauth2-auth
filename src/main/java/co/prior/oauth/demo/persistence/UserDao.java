package co.prior.oauth.demo.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.prior.oauth.demo.model.User;

public interface UserDao extends JpaRepository<User, Integer> {

	@Query("SELECT DISTINCT u FROM User u WHERE u.userName = :username")
	User findByUsername(@Param("username") String username);
}