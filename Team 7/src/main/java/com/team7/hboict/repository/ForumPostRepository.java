package com.team7.hboict.repository;

import com.team7.hboict.model.ForumPost;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Transactional
@Repository
public class ForumPostRepository {
    @PersistenceContext
    private EntityManager em;

    public ForumPost save(ForumPost post) {
        if (post.getId() == null) {
            em.persist(post);
            return post;
        }

        return em.merge(post);
    }

    public Page<ForumPost> findPage(Pageable pageable) {
        List<ForumPost> content = em.createQuery(
                        "SELECT p FROM ForumPost p ORDER BY p.id DESC",
                        ForumPost.class
                )
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = em.createQuery("SELECT COUNT(p) FROM ForumPost p", Long.class)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    public ForumPost findById(Long id) {
        return em.find(ForumPost.class, id);
    }

    public List<ForumPost> findByAuthor(String author) {
        return em.createQuery(
                        "SELECT p FROM ForumPost p WHERE LOWER(p.author) = LOWER(:author) ORDER BY p.id DESC",
                        ForumPost.class
                )
                .setParameter("author", author)
                .getResultList();
    }

    public void deleteById(Long id) {
        ForumPost post = em.find(ForumPost.class, id);
        if (post != null) {
            em.remove(post);
        }
    }
}
