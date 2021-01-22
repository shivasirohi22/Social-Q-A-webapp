package com.upgrad.quora.api.controller;


*import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.entity.*;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class QuestionController {
    @Autowired
    private QuestionService questionService;


    /**
     * @param  questionRequest data of question to be created
     * @param  authToken auth token to authorise user
     * @return success status  CREATED return
     */
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/question/create",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(
            @RequestHeader("authorization") final String authToken, QuestionRequest questionRequest)
            throws AuthorizationFailedException {

        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity = questionService.createQuestion(questionEntity, authToken);

    QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setId(questionEntity.getUuid().toString());
        questionResponse.setStatus("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);

    }

    /**
     * Get all questions posted by any user.
     *
     * @param authToken  auth token to authorise user
     * @return List of QuestionDetailsResponse
     * @throws AuthorizationFailedException In case the access token is invalid.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/question/all",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(
            @RequestHeader("authorization") final String authToken)
            throws AuthorizationFailedException {
        List<QuestionEntity> questions = questionService.getAllQuestions(authToken);
        List<QuestionDetailsResponse> questionDetailResponses = new ArrayList<>();

        for (QuestionEntity questionEntity : questions) {
            QuestionDetailsResponse questionDetailResponse = new QuestionDetailsResponse();

            questionDetailResponse.setId(questionEntity.getUuid());
            questionDetailResponse.setContent(questionEntity.getContent());

            questionDetailResponses.add(questionDetailResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(
                questionDetailResponses, HttpStatus.OK);
    }

    /**
     * Edit a question
     *
     * @param authToken  auth token to authorise user
     * @param questionId question id for quesiton
     * @param questionEditRequest question edit data
     * @return detials of edited question
     * @throws AuthorizationFailedException for authorisation failure
     * @throws InvalidQuestionException for invlaid quesiton id
     */
    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/question/edit/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(
            QuestionEditRequest questionEditRequest,
            @PathVariable("questionId") final String questionId,
            @RequestHeader("authorization") final String authToken)
            throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity =
                questionService.editQuestion(authToken, questionId, questionEditRequest.getContent());
        QuestionEditResponse questionEditResponse = new QuestionEditResponse();
        questionEditResponse.setId(questionEntity.getUuid());
        questionEditResponse.setStatus("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    /**
     * endpoint is used to delete a question that has been posted by a user
     *
     * @param authToken  auth token to authorise user
     * @param questionId for mapping question to be delete
     * @return status of deleted question
     * @throws AuthorizationFailedException for authorisation failure
     * @throws InvalidQuestionException if question with questionId doesn't exist.for question deletion exception
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}")
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
            @PathVariable("questionId") final String questionId,
            @RequestHeader("authorization") final String authToken)
            throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity questionEntity = questionService.deleteQuestion(authToken, questionId);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse();

        questionDeleteResponse.setStatus("QUESTION DELETED");
        questionDeleteResponse.setId(questionEntity.getUuid());

        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    /**
     * endpoint is used to fetch all the questions posed by a specific user
     *
     * @param userId mapped to the question and user
     * @param authToken auth token to authorise user
     * @return questions list
     * @throws AuthorizationFailedException for handling authorisation failure
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "question/all/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(
            @RequestHeader("authorization") final String authToken,
            @PathVariable("userId") String userId)
            throws UserNotFoundException, AuthorizationFailedException {

        List<QuestionEntity> questions = questionService.getAllQuestionsByUser(userId, authToken);
        List<QuestionDetailsResponse> questionDetailResponses = new ArrayList<>();

        for (QuestionEntity questionEntity : questions) {
            QuestionDetailsResponse questionDetailResponse = new QuestionDetailsResponse()
                    .id(questionEntity.getUuid())
                    .content(questionEntity.getContent());
            questionDetailResponses.add(questionDetailResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(
                questionDetailResponses, HttpStatus.OK);
    }
}
