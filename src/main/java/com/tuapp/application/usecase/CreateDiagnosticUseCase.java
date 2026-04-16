package com.tuapp.application.usecase;

import com.tuapp.domain.repository.CategoriaAnomaliaRepository;
import com.tuapp.domain.repository.DiagnosticRepository;
import com.tuapp.domain.repository.EnfermedadBaseRepository;
import com.tuapp.domain.repository.FocoRepository;
import com.tuapp.infrastructure.persistence.entity.CategoriaAnomaliaEntity;
import com.tuapp.infrastructure.persistence.entity.DiagnosticEntity;
import com.tuapp.infrastructure.persistence.entity.EnfermedadBaseEntity;
import com.tuapp.infrastructure.persistence.entity.FocoEntity;
import com.tuapp.infrastructure.persistence.entity.InstitucionEntity;
import com.tuapp.infrastructure.persistence.entity.UserEntity;
import com.tuapp.infrastructure.persistence.repository.InstitucionJpaRepository;
import com.tuapp.infrastructure.persistence.repository.UserJpaRepository;
import com.tuapp.presentation.dto.CreateDiagnosticRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class CreateDiagnosticUseCase {

    private final DiagnosticRepository diagnosticRepository;
    private final FocoRepository focoRepository;
    private final CategoriaAnomaliaRepository categoriaAnomaliaRepository;
    private final EnfermedadBaseRepository enfermedadBaseRepository;
    private final UserJpaRepository userJpaRepository;
    private final InstitucionJpaRepository institucionJpaRepository;

    public CreateDiagnosticUseCase(DiagnosticRepository diagnosticRepository,
                                   FocoRepository focoRepository,
                                   CategoriaAnomaliaRepository categoriaAnomaliaRepository,
                                   EnfermedadBaseRepository enfermedadBaseRepository,
                                   UserJpaRepository userJpaRepository,
                                   InstitucionJpaRepository institucionJpaRepository) {
        this.diagnosticRepository = diagnosticRepository;
        this.focoRepository = focoRepository;
        this.categoriaAnomaliaRepository = categoriaAnomaliaRepository;
        this.enfermedadBaseRepository = enfermedadBaseRepository;
        this.userJpaRepository = userJpaRepository;
        this.institucionJpaRepository = institucionJpaRepository;
    }

    @Transactional
    public DiagnosticEntity execute(CreateDiagnosticRequest request) {
        FocoEntity foco = focoRepository.findById(request.getFocoId())
                .orElseThrow(() -> new IllegalArgumentException("focoId no existe"));

        CategoriaAnomaliaEntity categoriaAnomalia = categoriaAnomaliaRepository
                .findById(request.getCategoriaAnomaliaId())
                .orElseThrow(() -> new IllegalArgumentException("categoriaAnomaliaId no existe"));

        Set<Long> enfermedadesIds = normalizeEnfermedadesIds(request.getEnfermedadesBaseIds());
        List<EnfermedadBaseEntity> enfermedades = enfermedadBaseRepository.findAllById(enfermedadesIds);
        UserEntity usuarioCrea = userJpaRepository.findById(request.getUsuarioCreaId())
                .orElseThrow(() -> new IllegalArgumentException("usuarioCreaId no existe"));
        InstitucionEntity institucion = institucionJpaRepository.findById(request.getInstitucionId())
            .orElseThrow(() -> new IllegalArgumentException("institucionId no existe"));

        if (enfermedades.size() != enfermedadesIds.size()) {
            throw new IllegalArgumentException("Uno o más enfermedadesBaseIds no existen");
        }

        DiagnosticEntity entity = new DiagnosticEntity();
        entity.setInstitucion(institucion);
        entity.setEsNormal(request.getEsNormal());
        entity.setEdad(request.getEdad());
        entity.setGenero(normalizeGender(request.getGenero()));
        entity.setAltura(request.getAltura());
        entity.setPeso(request.getPeso());
        entity.setPrecision(request.getPrecision());
        entity.setDiagnosticoTexto(normalizeText(request.getDiagnosticoTexto()));
        entity.setVerificado(defaultBoolean(request.getVerificado()));
        entity.setValvulopatia(defaultBoolean(request.getValvulopatia()));
        entity.setFoco(foco);
        entity.setCategoriaAnomalia(categoriaAnomalia);
        entity.setUsuarioCrea(usuarioCrea);
        entity.setEnfermedadesBase(new LinkedHashSet<>(enfermedades));

        return diagnosticRepository.save(entity);
    }

    private String normalizeGender(String gender) {
        return gender == null ? null : gender.trim().toUpperCase();
    }

    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private Set<Long> normalizeEnfermedadesIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Set.of();
        }
        return new LinkedHashSet<>(ids);
    }

    private boolean defaultBoolean(Boolean value) {
        return value != null && value;
    }
}