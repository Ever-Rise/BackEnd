package br.com.everrise.service;

import br.com.everrise.dto.request.BindDeviceRequest;
import br.com.everrise.dto.request.LoginRequest;
import br.com.everrise.dto.request.RefreshTokenRequest;
import br.com.everrise.dto.request.RegisterRequest;
import br.com.everrise.dto.response.AuthResponse;
import br.com.everrise.dto.response.GuinchoSessionResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(String bearerToken);

    GuinchoSessionResponse bindDevice(BindDeviceRequest request);

    void unbindDevice(Long guinchoId);
}
