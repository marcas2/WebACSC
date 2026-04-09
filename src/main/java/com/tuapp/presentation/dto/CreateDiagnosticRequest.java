package com.tuapp.presentation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class CreateDiagnosticRequest {

    @NotBlank(message = "institucion es obligatorio")
    @Size(max = 150, message = "institucion no puede superar 150 caracteres")
    private String institucion;

    @NotNull(message = "esNormal es obligatorio")
    @JsonAlias("isNormal")
    private Boolean esNormal;

    @NotNull(message = "edad es obligatorio")
    @Min(value = 0, message = "La edad no puede ser menor que 0")
    @Max(value = 120, message = "La edad no puede ser mayor que 120")
    @JsonAlias("age")
    private Integer edad;

    @NotBlank(message = "genero es obligatorio")
    @Size(max = 20, message = "genero no puede superar 20 caracteres")
    @JsonAlias("gender")
    private String genero;

    @NotNull(message = "altura es obligatorio")
    @DecimalMin(value = "0.1", message = "altura debe ser mayor que 0")
    @DecimalMax(value = "3.0", message = "altura no puede ser mayor que 3.0")
    private Double altura;

    @NotNull(message = "peso es obligatorio")
    @DecimalMin(value = "1.0", message = "peso debe ser mayor que 0")
    @DecimalMax(value = "500.0", message = "peso no puede ser mayor que 500")
    private Double peso;

    @NotBlank(message = "diagnosticoTexto es obligatorio")
    @Size(max = 5000, message = "diagnosticoTexto no puede superar 5000 caracteres")
    private String diagnosticoTexto;

    @NotNull(message = "focoId es obligatorio")
    private Long focoId;

    @NotNull(message = "categoriaAnomaliaId es obligatorio")
    private Long categoriaAnomaliaId;

    @NotNull(message = "usuarioCreaId es obligatorio")
    @JsonAlias("usuarioCrea")
    private Long usuarioCreaId;

    private Boolean verificado;

    private Boolean valvulopatia;

    private List<Long> enfermedadesBaseIds = new ArrayList<>();

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public Boolean getEsNormal() {
        return esNormal;
    }

    public void setEsNormal(Boolean esNormal) {
        this.esNormal = esNormal;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public String getDiagnosticoTexto() {
        return diagnosticoTexto;
    }

    public void setDiagnosticoTexto(String diagnosticoTexto) {
        this.diagnosticoTexto = diagnosticoTexto;
    }

    public Long getFocoId() {
        return focoId;
    }

    public void setFocoId(Long focoId) {
        this.focoId = focoId;
    }

    public Long getCategoriaAnomaliaId() {
        return categoriaAnomaliaId;
    }

    public void setCategoriaAnomaliaId(Long categoriaAnomaliaId) {
        this.categoriaAnomaliaId = categoriaAnomaliaId;
    }

    public Long getUsuarioCreaId() {
        return usuarioCreaId;
    }

    public void setUsuarioCreaId(Long usuarioCreaId) {
        this.usuarioCreaId = usuarioCreaId;
    }

    public Boolean getVerificado() {
        return verificado;
    }

    public void setVerificado(Boolean verificado) {
        this.verificado = verificado;
    }

    public Boolean getValvulopatia() {
        return valvulopatia;
    }

    public void setValvulopatia(Boolean valvulopatia) {
        this.valvulopatia = valvulopatia;
    }

    public List<Long> getEnfermedadesBaseIds() {
        return enfermedadesBaseIds;
    }

    public void setEnfermedadesBaseIds(List<Long> enfermedadesBaseIds) {
        this.enfermedadesBaseIds = enfermedadesBaseIds;
    }
}