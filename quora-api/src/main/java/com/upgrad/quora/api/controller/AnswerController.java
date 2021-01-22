package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import org.springframework.web.bind.annotation.*;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;



@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    /**
     * This API creates an answer in the database.
     *
     * @param authToken to authorise user
     * @param questionId querstion of the answer to be created
     * @param answerRequest  the answer to be updated
     * @return
     * @throws AuthorizationFailedException for authorisation failure
     * @throws InvalidQuestionException for the question is invalid in DB
     */
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/question/{questionId}/answer/create",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(
            @RequestHeader("authorization") final String authToken,
            @PathVariable("questionId") final String questionId,
            AnswerRequest answerRequest)
            throws InvalidQuestionException, AuthorizationFailedException {
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity = answerService.createAnswer(answerEntity, authToken, questionId);
        AnswerResponse answerResponse = new AnswerResponse();
        answerResponse.setStatus("ANSWER CREATED");
        answerResponse.setId(answerEntity.getUuid());
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    /**
     * Edits the answer already existing in Database
     *
     * @param authToken for user authorisation
     * @param answerId to identify answer to be edited
     * @param answerEditRequest new content fo the anser
     * @return
     * @throws AuthorizationFailedException for authorisaition failure
     * @throws AnswerNotFoundException in case id is invalid for any answer
     */
    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/answer/edit/{answerId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(
            @RequestHeader("authorization") final String accessToken,
            @PathVariable("answerId") final String answerId,
            AnswerEditRequest answerEditRequest)
            throws AuthorizationFailedException, AnswerNotFoundException {
        AnswerEditResponse answerEditResponse = new AnswerEditResponse();
        AnswerEntity answerEntity =
                answerService.editAnswerContent(accessToken, answerId, answerEditRequest.getContent());

        answerEditResponse.setStatus("ANSWER EDITED");
        answerEditResponse.setId(answerEntity.getUuid());
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    /**
     * delete a answer using answerId
     *
     * @param answerId id of the answer to be delete.
     * @param authToken authToken token to authorise user.
     * @return Id and status of the answer deleted.
     * @throws AuthorizationFailedException In case the authToken token is invalid.
     * @throws AnswerNotFoundException if answer with answerId doesn't exist.
     */
    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "/answer/delete/{answerId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
            @RequestHeader("authorization") final String authToken,
            @PathVariable("answerId") String answerId)
            throws AnswerNotFoundException, AuthorizationFailedException {
        AnswerEntity answerEntity = answerService.deleteAnswer(answerId, authToken);
        AnswerDeleteResponse answerDeleteResponse =
                new AnswerDeleteResponse().id(answerId).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }

    /**
     * Get all answers to the question.
     *
     * @param questionId to fetch qeustion
     * @param authToken access token to authorise user.
     * @return response of answer
     * @throws AuthorizationFailedException if use ris unauthorised
     *
     * @throws InvalidQuestionException if questionuu id is invalid
     *
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/answer/all/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(
            @PathVariable("questionId") String questionId,
            @RequestHeader("authorization") final String authToken
            )
            throws AuthorizationFailedException, InvalidQuestionException {
        List<AnswerEntity> answers = answerService.getAllAnswersToQuestion(questionId, authToken);
        List<AnswerDetailsResponse> answerDetailsResponses = new ArrayList<>();
        for (AnswerEntity answerEntity : answers) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
            answerDetailsResponse.setId(answerEntity.getUuid());
            answerDetailsResponse.setAnswerContent(answerEntity.getAnswer());
            answerDetailsResponse.setQuestionContent(answerEntity.getQuestionEntity().getContent());

            answerDetailsResponses.add(answerDetailsResponse);
        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponses, HttpStatus.OK);
    }
}
