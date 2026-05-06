package br.com.everrise.exception;

public class EmergencyStateException extends RuntimeException {
    public EmergencyStateException(String message) {
        super(message);
    }
}

