package net.johannbarbie.persistance.exceptions;



public class IdConflictException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IdConflictException(String m){
		super(m);
	}
}
