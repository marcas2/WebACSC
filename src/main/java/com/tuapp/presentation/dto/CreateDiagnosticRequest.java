package com.tuapp.presentation.dto;

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

    @NotNull(message = "isNormal es obligatorio")
    private Boolean isNormal;

    @NotNull(message = "age es obligatorio")
    @Min(value = 0, message = "La edad no puede ser menor que 0")
    @Max(value = 120, message = "La edad no puede ser mayor que 120")
    private Integer age;

    @NotBlank(message = "gender es obligatorio")
    @Size(max = 20, message = "gender no puede superar 20 caracteres")
    private String gender;

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

    private List<Long> enfermedadesBaseIds = new ArrayList<>();

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public Boolean getIsNormal() {
        return isNormal;
    }

    public void setIsNormal(Boolean isNormal) {
        this.isNormal = isNormal;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public List<Long> getEnfermedadesBaseIds() {
        return enfermedadesBaseIds;
    }

    public void setEnfermedadesBaseIds(List<Long> enfermedadesBaseIds) {
        this.enfermedadesBaseIds = enfermedadesBaseIds;
    }
}