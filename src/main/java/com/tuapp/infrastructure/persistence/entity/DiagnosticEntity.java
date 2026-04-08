package com.tuapp.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "diagnostics")
public class DiagnosticEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_normal", nullable = false)
    private Boolean isNormal;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 150)
    private String institucion;

    @Column(nullable = false)
    private Double altura;

    @Column(nullable = false)
    private Double peso;

    @Column(nullable = false, length = 20)
    private String gender;

        @Column(name = "diagnostico_texto", nullable = false, columnDefinition = "TEXT")
        private String diagnosticoTexto;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "foco_id", nullable = false)
        private FocoEntity foco;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "categoria_anomalia_id", nullable = false)
        private CategoriaAnomaliaEntity categoriaAnomalia;

        @ManyToMany
        @JoinTable(
            name = "diagnostics_enfermedades_base",
            joinColumns = @JoinColumn(name = "diagnostic_id"),
            inverseJoinColumns = @JoinColumn(name = "enfermedad_base_id")
        )
        private Set<EnfermedadBaseEntity> enfermedadesBase = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsNormal() { return isNormal; }
    public void setIsNormal(Boolean isNormal) { this.isNormal = isNormal; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getInstitucion() { return institucion; }
    public void setInstitucion(String institucion) { this.institucion = institucion; }

    public Double getAltura() { return altura; }
    public void setAltura(Double altura) { this.altura = altura; }

    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDiagnosticoTexto() { return diagnosticoTexto; }
    public void setDiagnosticoTexto(String diagnosticoTexto) { this.diagnosticoTexto = diagnosticoTexto; }

    public FocoEntity getFoco() { return foco; }
    public void setFoco(FocoEntity foco) { this.foco = foco; }

    public CategoriaAnomaliaEntity getCategoriaAnomalia() { return categoriaAnomalia; }
    public void setCategoriaAnomalia(CategoriaAnomaliaEntity categoriaAnomalia) { this.categoriaAnomalia = categoriaAnomalia; }

    public Set<EnfermedadBaseEntity> getEnfermedadesBase() { return enfermedadesBase; }
    public void setEnfermedadesBase(Set<EnfermedadBaseEntity> enfermedadesBase) { this.enfermedadesBase = enfermedadesBase; }
}