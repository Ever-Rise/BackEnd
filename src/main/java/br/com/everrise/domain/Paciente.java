package br.com.everrise.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "paciente")
@DiscriminatorValue("PACIENTE")
public class Paciente extends Usuario {

    @Column(name = "data_nascimento")
    private LocalDateTime dataNascimento;

    @Column(name = "condicao_medica", length = 500)
    private String condicaoMedica;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Familiar> familiares = new ArrayList<>();
}


