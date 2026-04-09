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
    private LocalDateTime creadoEn;

    @Column(name = "esnormal", nullable = false)
    private Boolean esNormal;

    @Column(nullable = false)
    private Boolean verificado = false;

    @Column(nullable = false)
    private Boolean valvulopatia = false;

    @Column(nullable = false)
    private Integer edad;

    @Column(nullable = false, length = 150)
    private String institucion;

    @Column(nullable = false)
    private Double altura;

    @Column(nullable = false)
    private Double peso;

    @Column(nullable = false, length = 20)
    private String genero;

        @Column(name = "diagnostico_texto", nullable = false, columnDefinition = "TEXT")
        private String diagnosticoTexto;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "foco_id", nullable = false)
        private FocoEntity foco;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "categoria_anomalia_id", nullable = false)
        private CategoriaAnomaliaEntity categoriaAnomalia;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "usuario_crea_id")
        private UserEntity usuarioCrea;

        @ManyToMany
        @JoinTable(
            name = "diagnostics_enfermedades_base",
            joinColumns = @JoinColumn(name = "diagnostic_id"),
            inverseJoinColumns = @JoinColumn(name = "enfermedad_base_id")
        )
        private Set<EnfermedadBaseEntity> enfermedadesBase = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public Boolean getEsNormal() { return esNormal; }
    public void setEsNormal(Boolean esNormal) { this.esNormal = esNormal; }

    public Boolean getVerificado() { return verificado; }
    public void setVerificado(Boolean verificado) { this.verificado = verificado; }

    public Boolean getValvulopatia() { return valvulopatia; }
    public void setValvulopatia(Boolean valvulopatia) { this.valvulopatia = valvulopatia; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getInstitucion() { return institucion; }
    public void setInstitucion(String institucion) { this.institucion = institucion; }

    public Double getAltura() { return altura; }
    public void setAltura(Double altura) { this.altura = altura; }

    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getDiagnosticoTexto() { return diagnosticoTexto; }
    public void setDiagnosticoTexto(String diagnosticoTexto) { this.diagnosticoTexto = diagnosticoTexto; }

    public FocoEntity getFoco() { return foco; }
    public void setFoco(FocoEntity foco) { this.foco = foco; }

    public CategoriaAnomaliaEntity getCategoriaAnomalia() { return categoriaAnomalia; }
    public void setCategoriaAnomalia(CategoriaAnomaliaEntity categoriaAnomalia) { this.categoriaAnomalia = categoriaAnomalia; }

    public UserEntity getUsuarioCrea() { return usuarioCrea; }
    public void setUsuarioCrea(UserEntity usuarioCrea) { this.usuarioCrea = usuarioCrea; }

    public Set<EnfermedadBaseEntity> getEnfermedadesBase() { return enfermedadesBase; }
    public void setEnfermedadesBase(Set<EnfermedadBaseEntity> enfermedadesBase) { this.enfermedadesBase = enfermedadesBase; }
}