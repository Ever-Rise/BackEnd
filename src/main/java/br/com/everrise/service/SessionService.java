package br.com.everrise.service;

import br.com.everrise.domain.entity.GuinchoSession;

public interface SessionService {

    GuinchoSession bindDevice(Long guinchoId, String deviceFingerprint);

    void unbindDevice(Long guinchoId);
}
