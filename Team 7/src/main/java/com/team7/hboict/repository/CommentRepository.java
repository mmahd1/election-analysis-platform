package com.team7.hboict.repository;

import com.team7.hboict.model.CommentForum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public class CommentRepository {
    @PersistenceContext
    private EntityManager em;

    public CommentForum save(CommentForum comment) {
        em.persist(comment);
        return comment;
    }

    public List<CommentForum> findByPostId(Long postId) {
        return em.createQuery("SELECT c FROM CommentForum c WHERE c.postId = :postId ORDER BY c.id DESC", CommentForum.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    public void deleteById(Long id) {
        CommentForum comment = em.find(CommentForum.class, id);
        if (comment != null) {
            em.remove(comment);
        }
    }

    public CommentForum findById(Long id) {
        return em.find(CommentForum.class, id);
    }

    public void deleteByPostId(Long postId) {
        em.createQuery("DELETE FROM CommentForum c WHERE c.postId = :postId")
                .setParameter("postId", postId)
                .executeUpdate();
    }
}
