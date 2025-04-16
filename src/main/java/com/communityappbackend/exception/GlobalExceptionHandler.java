package com.communityappbackend.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleCategoryAlreadyExists(CategoryAlreadyExistsException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFound(UserNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleItemNotFound(ItemNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotificationNotFound(NotificationNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(TradeRequestNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleTradeRequestNotFound(TradeRequestNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleEmailExists(EmailAlreadyExistsException ex) {
        return ex.getMessage();
    }

    // Fallback for any other runtime exception
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleRuntime(RuntimeException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(DonationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleDonationNotFound(DonationNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(DonationImageSaveException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDonationImageSave(DonationImageSaveException ex) {
        return ex.getMessage();
    }

}
