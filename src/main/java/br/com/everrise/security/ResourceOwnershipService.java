package br.com.everrise.security;

import br.com.everrise.domain.entity.Guincho;
import br.com.everrise.domain.entity.User;
import br.com.everrise.repository.GuinchoRepository;
import br.com.everrise.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceOwnershipService {

    private final GuinchoRepository guinchoRepository;
    private final SecurityUtils securityUtils;

    /**
     * Verifica se o usuário autenticado pode acessar um equipamento (guincho).
     * Regra: Usuário é dono OU é ADMIN
     * @param guinchoId ID do guincho
     * @return true se acesso permitido, false caso contrário
     */
    public boolean usuarioPodeAcessarGuincho(Long guinchoId) {
        try {
            User currentUser = securityUtils.getCurrentUser();
            
            // ADMIN pode acessar tudo
            if (isAdmin(currentUser)) {
                return true;
            }

            // Usuário comum só pode acessar seus próprios guinchos
            Guincho guincho = guinchoRepository.findById(guinchoId)
                .orElse(null);
            
            if (guincho == null) {
                return false;
            }

            return guincho.getOwner() != null && 
                   guincho.getOwner().getId().equals(currentUser.getId());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica se o usuário é ADMIN
     */
    private boolean isAdmin(User user) {
        return user.getRole() != null && 
               user.getRole().getAuthority().equals("ROLE_ADMIN");
    }

    /**
     * Valida acesso a um recurso, lançando exceção se não autorizado
     * @param guinchoId ID do guincho
     * @throws SecurityException se acesso negado
     */
    public void validarAcessoGuincho(Long guinchoId) {
        if (!usuarioPodeAcessarGuincho(guinchoId)) {
            throw new SecurityException(
                "Você não tem permissão para acessar este recurso"
            );
        }
    }
}
