package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     *
     * @param userId
     * @return
     */
    public UserEntity getUserById(final String userId) {
        try {
            return entityManager.createNamedQuery("userByUserId", UserEntity.class).setParameter("userId", userId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     *
     * @param userEntity
     * @return
     */
    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    /**
     *
     * @param userName
     * @return
     */
    public UserEntity getUserByUserName(final String userName) {
    try {
        return entityManager.createNamedQuery("userByUserName", UserEntity.class).setParameter("userName", userName).getSingleResult();
    } catch (NoResultException nre) {
        return null;
    }
}

    /**
     *
     * @param email
     * @return
     */
    public UserEntity getUserByEmail(final String email) {
    try {
        return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
    } catch (NoResultException nre) {
        return null;
    }
}

    public void updateUserEntity(final UserEntity updatedUserEntity) {

    entityManager.merge(updatedUserEntity);
    }

    /**
     *
     * @param userId
     * @return
     */
    public UserEntity deleteUser(final String userId) {
        UserEntity deleteUser = getUserById( userId );
        if (deleteUser != null) {
            this.entityManager.remove( deleteUser );
        }
        return deleteUser;
    }
}
