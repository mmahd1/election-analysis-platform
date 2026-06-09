package com.team7.hboict.repository;

import com.team7.hboict.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager em;

    public User findByEmail(String email) {

        List<User> users = em.createQuery(
                        "SELECT u FROM User u WHERE u.email = :email",
                        User.class
                )
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultList();

        if (users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }

    public void save(User user) {
        em.persist(user);
    }
}
