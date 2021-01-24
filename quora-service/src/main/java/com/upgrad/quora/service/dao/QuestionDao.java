package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;


@Repository
public class QuestionDao {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * createQuestion
   * @param questionEntity
   * @return
   */
  public QuestionEntity createQuestion(QuestionEntity questionEntity) {
    entityManager.persist(questionEntity);
    return questionEntity;
  }

  /**
   * getAllQuestions
   * @return
   */
  public List<QuestionEntity> getAllQuestions() {
    return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
  }

  /**
   * getQuestionById
   * @param questionId
   * @return
   */
  public QuestionEntity getQuestionById(final String questionId) {
    try {
      return entityManager
          .createNamedQuery("getQuestionById", QuestionEntity.class)
          .setParameter("uuid", questionId)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * updateQuestion
   * @param questionEntity
   */
  public void updateQuestion(QuestionEntity questionEntity) {
    entityManager.merge(questionEntity);
  }

  /**
   * deleteQuestion
   * @param questionEntity
   */
  public void deleteQuestion(QuestionEntity questionEntity) {
    entityManager.remove(questionEntity);
  }

  /**
   * getAllQuestionsByUser
   * @param userId
   * @return
   */
  public List<QuestionEntity> getAllQuestionsByUser(final UserEntity userId) {
    return entityManager
        .createNamedQuery("getQuestionByUser", QuestionEntity.class)
        .setParameter("user", userId)
        .getResultList();
  }
}
