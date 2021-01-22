package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerService {

  @Autowired private UserAuthDao userAuthDao;

  @Autowired private AnswerDao answerDao;

  @Autowired private QuestionDao questionDao;

  /**
   * creates an answer in the database.
   *
   * @param answerEntity answer data
   * @param authToken to authorise user for using the service
   * @param questionId to identify question
   * @return
   * @throws AuthorizationFailedException  for user auhtorisation failure
   * @throws InvalidQuestionException question is invalid or doesnot exist
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity createAnswer(
      AnswerEntity answerEntity, final String accessToken, final String questionId)
      throws InvalidQuestionException, AuthorizationFailedException {

    UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);

    if (userAuthEntity == null) {

      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    } else if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to post an answer");
    }

    QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
    if (questionEntity == null) {
      throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
    }
    answerEntity.setQuestionEntity(questionEntity);
    answerEntity.setUuid(UUID.randomUUID().toString());
    answerEntity.setUserEntity(userAuthEntity.getUserEntity());
    answerEntity.setDate(ZonedDateTime.now());
    return answerDao.createAnswer(answerEntity);
  }

  /**
   * edits the answer which already exist in the database.
   *
   * @param authToken to authorise user for using the service
   * @param answerId Id of the answer which is to be edited.
   * @param newAnswer Contains the new content of the answer.
   * @return
   * @throws AnswerNotFoundException if answer not found
   * @throws AuthorizationFailedException  for user auhtorisation failure
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity editAnswerContent(
      final String authToken, final String answerId, final String newAnswer)
      throws AnswerNotFoundException, AuthorizationFailedException {
    UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(authToken);
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    } else if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to edit an answer");
    }
    AnswerEntity answerEntity = answerDao.getAnswerById(answerId);
    if (answerEntity == null) {
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
    }
    String id= answerEntity.getUserEntity().getUuid();
    if (!id.equals(userAuthEntity.getUserEntity().getUuid())) {
      throw new AuthorizationFailedException(
          "ATHR-003", "Only the answer owner can edit the answer");
    }
    answerEntity.setAnswer(newAnswer);
    answerDao.updateAnswer(answerEntity);
    return answerEntity;
  }

  /**
   * delete the answer
   *
   * @param answerId id of the answer to delete
   * @param authToken to authorise user for using the service
   * @throws AuthorizationFailedException  for user auhtorisation failure
   * @throws AnswerNotFoundException if the answer is not found
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity deleteAnswer(final String answerId, final String authToken)
      throws AuthorizationFailedException, AnswerNotFoundException {

    UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(authToken);
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    } else if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to delete an answer");
    }

    AnswerEntity answerEntity = answerDao.getAnswerById(answerId);
    if (answerEntity == null) {
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
    }
    String role=userAuthEntity.getUserEntity().getRole();
    String id=answerEntity.getUserEntity().getUuid();
    if (role.equals("admin")
        || id.equals(userAuthEntity.getUserEntity().getUuid())) {
      return answerDao.deleteAnswer(answerId);
    } else {
      throw new AuthorizationFailedException(
          "ATHR-003", "Only the answer owner or admin can delete the answer");
    }
  }

  /**
   * get all the answers for a question
   *
   * @param questionId id of the question to fetch the answers.
   * @param authToken to authorise user for using the service
   * @throws AuthorizationFailedException for user auhtorisation failure
   *
   * @throws InvalidQuestionException The question doesnot exists
   *
   */
  public List<AnswerEntity> getAllAnswersToQuestion(
      final String questionId, final String authToken)
      throws AuthorizationFailedException, InvalidQuestionException {
    UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(authToken);
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    } else if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to get the answers");
    }
    QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
    if (questionEntity == null) {
      throw new InvalidQuestionException(
          "QUES-001", "The question with entered id whose details are to be seen does not exist");
    }
    return answerDao.getAllAnswersToQuestion(questionId);
  }
}
