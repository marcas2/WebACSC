package com.tuapp.infrastructure.persistence;

import com.tuapp.infrastructure.persistence.entity.CategoriaAnomaliaEntity;
import com.tuapp.infrastructure.persistence.entity.FocoEntity;
import com.tuapp.infrastructure.persistence.repository.CategoriaAnomaliaJpaRepository;
import com.tuapp.infrastructure.persistence.repository.FocoJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CatalogDataInitializer implements CommandLineRunner {

    private final FocoJpaRepository focoJpaRepository;
    private final CategoriaAnomaliaJpaRepository categoriaAnomaliaJpaRepository;

    public CatalogDataInitializer(FocoJpaRepository focoJpaRepository,
                                  CategoriaAnomaliaJpaRepository categoriaAnomaliaJpaRepository) {
        this.focoJpaRepository = focoJpaRepository;
        this.categoriaAnomaliaJpaRepository = categoriaAnomaliaJpaRepository;
    }

    @Override
    public void run(String... args) {
        seedFocos();
        seedCategoriasAnomalias();
    }

    private void seedFocos() {
        if (focoJpaRepository.count() > 0) {
            return;
        }

        focoJpaRepository.saveAll(List.of(
                foco("Aórtico", "01"),
                foco("Pulmonar", "02"),
                foco("Tricuspídeo", "03"),
                foco("Mitral", "04")
        ));
    }

    private void seedCategoriasAnomalias() {
        if (categoriaAnomaliaJpaRepository.count() > 0) {
            return;
        }

        categoriaAnomaliaJpaRepository.saveAll(List.of(
                categoria("Soplo sistólico", "SA"),
                categoria("Soplo diastólico", "SD"),
                categoria("Soplo continuo", "SC"),
                categoria("Arritmia", "AR"),
                categoria("Taquicardia", "TA"),
                categoria("Bradicardia", "BR"),
                categoria("Clic de eyección", "CE"),
                categoria("Frote pericárdico", "FP"),
                categoria("Galope S3", "G3"),
                categoria("Galope S4", "G4"),
                categoria("Otro", "OT")
        ));
    }

    private FocoEntity foco(String nombre, String codigo) {
        FocoEntity entity = new FocoEntity();
        entity.setNombre(nombre);
        entity.setCodigo(codigo);
        return entity;
    }

    private CategoriaAnomaliaEntity categoria(String nombre, String codigo) {
        CategoriaAnomaliaEntity entity = new CategoriaAnomaliaEntity();
        entity.setNombre(nombre);
        entity.setCodigo(codigo);
        return entity;
    }
}
