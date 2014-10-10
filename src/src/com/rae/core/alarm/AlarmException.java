package com.rae.core.alarm;

public class AlarmException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1235030220450325531L;

	public AlarmException() {
		super();
	}

	public AlarmException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public AlarmException(String detailMessage) {
		super(detailMessage);
	}

	public AlarmException(Throwable throwable) {
		super(throwable);
	}

}
