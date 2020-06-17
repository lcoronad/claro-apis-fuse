package com.claro.configuration;

import org.springframework.stereotype.Component;

@Component
public class MyException extends Exception{
	
	private static final long serialVersionUID = 7718828512143293558L;
	
	public MyException(){
		
	}
	
	public MyException(String message) {
        super(message);
    }
	
	

}
