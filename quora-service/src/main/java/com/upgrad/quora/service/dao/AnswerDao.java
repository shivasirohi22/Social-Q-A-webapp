package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;


@Repository
public class AnswerDao {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * createAnswer
   * @param answerEntity
   * @return
   */
  public AnswerEntity createAnswer(AnswerEntity answerEntity) {
    entityManager.persist(answerEntity);
    return answerEntity;
  }

  /**
   * getAnswerById
   * @param answerId
   * @return
   */
  public AnswerEntity getAnswerById(final String answerId) {
    try {

      TypedQuery<AnswerEntity> tQuery= entityManager
              .createNamedQuery("getAnswerById", AnswerEntity.class).setParameter("uuid", answerId);
      return tQuery.getSingleResult();
    } catch (NoResultException noResultException) {
      return null;
    }
  }

  /**
   * updateAnswer
   * @param answerEntity
   */
  public void updateAnswer(AnswerEntity answerEntity) {
    entityManager.merge(answerEntity);
  }

  /**
   * deleteAnswer
   * @param answerId
   * @return
   */
  public AnswerEntity deleteAnswer(final String answerId) {
    AnswerEntity deleteAnswer = getAnswerById(answerId);
    if (deleteAnswer != null) {
      entityManager.remove(deleteAnswer);
    }
    return deleteAnswer;
  }

  /**
   * get all answers
   * @param questionId
   * @return
   */
  public List<AnswerEntity> getAllAnswersToQuestion(final String questionId) {
    return entityManager
        .createNamedQuery("getAllAnswersToQuestion", AnswerEntity.class)
        .setParameter("uuid", questionId)
        .getResultList();
  }
}
