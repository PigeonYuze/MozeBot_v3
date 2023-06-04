package com.pigeonyuze.exception;

/**
 * 已存在
 * */
public class AlreadyExistException extends Exception {

    public AlreadyExistException(String explanation){
        super(explanation);
    }

    public AlreadyExistException(){
        super();
    }
}
